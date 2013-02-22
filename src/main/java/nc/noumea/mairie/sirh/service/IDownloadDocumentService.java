package nc.noumea.mairie.sirh.service;

import java.util.List;
import java.util.Map;

public interface IDownloadDocumentService {

	public void downloadDocumentToLocalPath(String url, Map<String, String> urlParameters, String localPath) throws Exception;
	
	public <R> List<R> downloadJsonDocumentAs(Class<R> resultClass, String url, Map<String, String> urlParameters) throws Exception ;
	
}
