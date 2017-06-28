package nc.noumea.mairie.sirh.service;

import java.util.List;
import java.util.Map;

import com.sun.jersey.api.client.ClientResponse;

public interface IDownloadDocumentService {

	public void downloadDocumentToLocalPath(String url, Map<String, String> urlParameters, String localPath) throws Exception;

	public <R> R downloadDocumentAs(Class<R> resultClass, String url, Map<String, String> urlParameters) throws Exception;

	public <R> List<R> downloadJsonDocumentAsList(Class<R> resultClass, String url, Map<String, String> urlParameters) throws Exception;

	public <R> R postAs(Class<R> resultClass, String url, Map<String, String> urlParameters) throws Exception;	
	
	public ClientResponse createAndFireRequest(String url, Map<String, String> urlParameters) throws Exception;

	public <T> T readResponse(Class<T> targetClass, ClientResponse response, String url) throws Exception;



}
