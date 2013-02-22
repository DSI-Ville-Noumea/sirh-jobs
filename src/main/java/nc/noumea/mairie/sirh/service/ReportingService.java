package nc.noumea.mairie.sirh.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.sun.jersey.api.client.ClientResponse;

@Service
public class ReportingService extends DownloadDocumentService implements IReportingService {
	
	@Autowired
	@Qualifier("reportingBaseUrl")
	private String reportingBaseUrl;
	
	@Autowired
	@Qualifier("reportServerPath")
	private String reportServerPath;
	
	private static final String REPORT_PAGE = "frameset";
	private static final String PARAM_REPORT = "__report";
	private static final String PARAM_FORMAT = "__format";
	
	@Override
	public void getTableauAvancementsReportAndSaveItToFile(int idCap, int idCadreEmploi, String targetPath) throws Exception {

		Map<String, String> map = new HashMap<String, String>();
		map.put(PARAM_REPORT, reportServerPath + "avctFonctCap.rptdesign");
		map.put(PARAM_FORMAT, "PDF");
		map.put("idCap", String.valueOf(idCap));
		map.put("idCadreEmploi", String.valueOf(idCadreEmploi));
		
		String url = reportingBaseUrl + REPORT_PAGE;
		
		ClientResponse response = createAndFireRequest(url, map);
		readResponseIntoFile(response, url, map, targetPath);
	}
}
