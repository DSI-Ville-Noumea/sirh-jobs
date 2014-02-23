package nc.noumea.mairie.sirh.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;

public interface IDownloadDocumentService {

	public void downloadDocumentToLocalPath(String url, Map<String, String> urlParameters, String localPath)
			throws Exception;

	public <R> R downloadDocumentAs(Class<R> resultClass, String url, Map<String, String> urlParameters)
			throws Exception;

	public <R> List<R> downloadJsonDocumentAsList(Class<R> resultClass, String url, Map<String, String> urlParameters)
			throws Exception;

	public <R> R postAs(Class<R> resultClass, String url, Map<String, String> urlParameters) throws Exception;

	public String downloadDocumentAccesNTLMAs(String url) throws Exception;
	
	public HttpResponse createAndFireRequestNTLM(String url) throws ClientProtocolException, IOException;

}
