package nc.noumea.mairie.sirh.service;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.auth.NTLMSchemeFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import flexjson.JSONDeserializer;

@Service
public class DownloadDocumentService implements IDownloadDocumentService {

	@Autowired
	@Qualifier("kiosqueUserWebdav")
	private String kiosqueUserWebdav;

	@Autowired
	@Qualifier("kiosqueUserPwsWebdav")
	private String kiosqueUserPwsWebdav;

	@Autowired
	@Qualifier("kiosqueDomainWebdav")
	private String kiosqueDomainWebdav;

	@Autowired
	@Qualifier("kiosqueUrlWebdav")
	private String kiosqueUrlWebdav;

	@Autowired
	@Qualifier("kiosquePortWebdav")
	private String kiosquePortWebdav;

	public void downloadDocumentToLocalPath(String url, Map<String, String> urlParameters, String localPath)
			throws Exception {

		ClientResponse response = createAndFireRequest(url, urlParameters);
		readResponseIntoFile(response, url, urlParameters, localPath);
	}

	public <R> R downloadDocumentAs(Class<R> resultClass, String url, Map<String, String> urlParameters)
			throws Exception {

		ClientResponse response = createAndFireRequest(url, urlParameters);
		return readResponseAs(resultClass, response, url, urlParameters);
	}

	public <R> R postAs(Class<R> resultClass, String url, Map<String, String> urlParameters) throws Exception {

		ClientResponse response = createAndFireRequest(url, urlParameters, true);

		String responseAsText = readResponseAs(String.class, response, url, urlParameters);

		R result = resultClass.newInstance();
		return new JSONDeserializer<R>().deserializeInto(responseAsText, result);
	}

	public <R> List<R> downloadJsonDocumentAsList(Class<R> resultClass, String url, Map<String, String> urlParameters)
			throws Exception {

		ClientResponse response = createAndFireRequest(url, urlParameters);
		return readJsonResponseAsList(response, url, urlParameters);
	}

	protected ClientResponse createAndFireRequest(String url, Map<String, String> urlParameters) {
		return createAndFireRequest(url, urlParameters, false);
	}

	protected ClientResponse createAndFireRequest(String url, Map<String, String> urlParameters, boolean isPost) {

		Client client = Client.create();

		WebResource webResource = client.resource(url);

		if (urlParameters != null) {
			for (String key : urlParameters.keySet()) {
				webResource = webResource.queryParam(key, urlParameters.get(key));
			}
		}

		ClientResponse response = null;

		if (isPost)
			response = webResource.post(ClientResponse.class);
		else
			response = webResource.get(ClientResponse.class);

		return response;
	}

	protected void readResponseIntoFile(ClientResponse response, String url, Map<String, String> urlParameters,
			String targetPath) throws Exception {

		InputStream is = readResponseAsInputStream(response, url, urlParameters);

		BufferedOutputStream bos = null;
		FileObject pdfFile = null;

		try {

			FileSystemManager fsManager = VFS.getManager();
			pdfFile = fsManager.resolveFile(targetPath);
			bos = new BufferedOutputStream(pdfFile.getContent().getOutputStream());
			IOUtils.copy(is, bos);

		} catch (Exception e) {
			throw new Exception(String.format(
					"An error occured while writing the downloaded file to the following path '%s'.", targetPath), e);
		} finally {

			IOUtils.closeQuietly(bos);
			IOUtils.closeQuietly(is);

			if (pdfFile != null) {
				try {
					pdfFile.close();
				} catch (FileSystemException e) {
					// ignore the exception
				}
			}
		}

	}

	protected InputStream readResponseAsInputStream(ClientResponse response, String url,
			Map<String, String> urlParameters) throws Exception {
		checkResponseHttpStatus(response, url, urlParameters);
		return response.getEntityInputStream();
	}

	protected <R> R readResponseAs(Class<R> resultClass, ClientResponse response, String url,
			Map<String, String> urlParameters) throws Exception {
		checkResponseHttpStatus(response, url, urlParameters);
		return response.getEntity(resultClass);
	}

	protected <R> List<R> readJsonResponseAsList(ClientResponse response, String url, Map<String, String> urlParameters)
			throws Exception {
		String jsonString = readResponseAs(String.class, response, url, urlParameters);
		return new JSONDeserializer<List<R>>().deserialize(jsonString);
	}

	private void checkResponseHttpStatus(ClientResponse response, String url, Map<String, String> urlParameters)
			throws Exception {
		if (response.getStatus() != HttpStatus.OK.value()) {
			throw new Exception(
					String.format(
							"An error occured while querying the following URL '%s' with parameters '%s'. HTTP Status code is : %s.",
							url, getListOfParamsFromMap(urlParameters), response.getStatus()));
		}
	}

	private String getListOfParamsFromMap(Map<String, String> urlParameters) {

		StringBuilder sb = new StringBuilder();

		if (urlParameters != null) {
			for (String key : urlParameters.keySet()) {
				sb.append(String.format("[%s: %s] ", key, urlParameters.get(key)));
			}
		}

		return sb.toString();
	}

	@Override
	public String downloadDocumentAccesNTLMAs(Class<String> resultClass, String url) throws Exception {

		HttpResponse response = createAndFireRequestNTLM(url);
		return readResponseNTLM(resultClass, response, url);
	}

	private HttpResponse createAndFireRequestNTLM(String url) throws ClientProtocolException, IOException {

		DefaultHttpClient httpclient = new DefaultHttpClient();
		httpclient.getAuthSchemes().register("ntlm", new NTLMSchemeFactory());

		httpclient.getCredentialsProvider().setCredentials(
				new AuthScope(kiosqueUrlWebdav, Integer.valueOf(kiosquePortWebdav)),
				new NTCredentials(kiosqueUserWebdav, kiosqueUserPwsWebdav, "", kiosqueDomainWebdav));

		HttpHost target = new HttpHost(kiosqueUrlWebdav, Integer.valueOf(kiosquePortWebdav), "http");

		// Make sure the same context is used to execute logically related
		// requests
		HttpContext localContext = new BasicHttpContext();

		// Execute a cheap method first. This will trigger NTLM authentication

		HttpGet httpget = new HttpGet(url);

		HttpResponse response1 = httpclient.execute(target, httpget, localContext);

		return response1;
	}

	public String readResponseNTLM(Class<String> targetClass, HttpResponse response, String url) {

		if (response.getStatusLine().getStatusCode() == HttpStatus.NO_CONTENT.value()) {
			return null;
		}

		if (response.getStatusLine().getStatusCode() != HttpStatus.OK.value()) {
			return null;
		}

		String output = "";
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

			output = br.readLine();
		} catch (IOException e) {

		}

		return output == "" ? null : output;
	}
}
