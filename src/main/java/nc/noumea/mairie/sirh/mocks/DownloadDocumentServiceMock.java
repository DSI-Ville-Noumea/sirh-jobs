package nc.noumea.mairie.sirh.mocks;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import nc.noumea.mairie.sirh.service.DownloadDocumentService;
import nc.noumea.mairie.sirh.service.IDownloadDocumentService;

//@Service
public class DownloadDocumentServiceMock extends DownloadDocumentService implements IDownloadDocumentService {

	@Override
	public <R> List<R> downloadJsonDocumentAsList(Class<R> resultClass, String url,
			Map<String, String> urlParameters) throws Exception {
		
		return (List<R>) Arrays.asList("MAINOU-6-25", "MAINOU-6-26");
	}

}
