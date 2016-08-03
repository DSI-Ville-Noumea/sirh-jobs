package nc.noumea.mairie.sirh.service;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
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

	@Autowired
	@Qualifier("sirhWsBaseUrl")
	private String sirhWsBaseUrl;

	private static final String sirhDownloadTabAvctPDFUrl = "avancements/downloadTableauAvancementsPDF"; // edition
																											// PDF

	private static final String REPORT_PAGE = "frameset";
	private static final String PARAM_REPORT = "__report";
	private static final String PARAM_FORMAT = "__format";

	@Override
	public InputStream getTableauAvancementsReport(int idCap, int idCadreEmploi, boolean avisEAE) throws Exception {

		String url = String.format(sirhWsBaseUrl + sirhDownloadTabAvctPDFUrl);

		String urlWSTableauAvctCAP = url + "?idCap=" + idCap + "&idCadreEmploi=" + idCadreEmploi + "&avisEAE=" + avisEAE;

		Map<String, String> map = new HashMap<String, String>();
		ClientResponse response = createAndFireRequest(urlWSTableauAvctCAP, map);
		return readResponseAsInputStream(response, urlWSTableauAvctCAP, map);
	}

	@Override
	public InputStream getAvctFirstLastPrintPage(String jobId, String jobUser, String codeCap, String cadreEmploi,
			Date submissionDate, boolean isFirst, boolean isEaes) throws Exception {

		Map<String, String> map = new HashMap<String, String>();
		map.put(PARAM_REPORT, reportServerPath + "pageGardeJobAvctCap.rptdesign");
		map.put(PARAM_FORMAT, "PDF");
		map.put("jobId", jobId);
		map.put("jobUser", jobUser);
		map.put("codeCap", codeCap);
		map.put("cadreEmploi", cadreEmploi);
		map.put("debut", String.valueOf(isFirst));
		map.put("isEaes", String.valueOf(isEaes));
		SimpleDateFormat sf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		map.put("dateSubmission", sf.format(submissionDate));

		String url = reportingBaseUrl + REPORT_PAGE;

		ClientResponse response = createAndFireRequest(url, map);
		return readResponseAsInputStream(response, url, map);
	}
}
