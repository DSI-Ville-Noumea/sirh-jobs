package nc.noumea.mairie.sirh.job;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.noumea.mairie.sirh.dao.IPrintJobDao;
import nc.noumea.mairie.sirh.domain.PrintJob;
import nc.noumea.mairie.sirh.service.IDownloadDocumentService;
import nc.noumea.mairie.sirh.service.IReportingService;
import nc.noumea.mairie.sirh.service.PrinterHelper;
import nc.noumea.mairie.sirh.tools.AvancementsWithEaesMassPrintJobStatusEnum;
import nc.noumea.mairie.sirh.tools.Helper;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Service;

@Service
public class AvancementsWithEaesMassPrintJob extends QuartzJobBean implements
		IAvancementsWithEaesMassPrintJob {

	@Autowired
	private Helper helper;
	
	@Autowired
	private IPrintJobDao printJobDao;
	
	@Autowired
	private IReportingService reportingService;

	@Autowired
	private IDownloadDocumentService downloadDocumentService;
	
	@Autowired
	@Qualifier("avcstTempWorkspacePath")
	private String avcstTempWorkspacePath;
	
	@Autowired
	@Qualifier("sharepointEaeDocBaseUrl")
	private String sharepointEaeDocBaseUrl;
	
	@Autowired
	@Qualifier("sirhWsEndpointUrl")
	private String sirhWsEndpointUrl;
	
	@Autowired
	@Qualifier("cupsServerHostName")
	private String cupsServerHostName;
	
	@Autowired
	@Qualifier("cupsServerPort")
	private int cupsServerPort;
	
	@Autowired
	@Qualifier("cupsSirhPrinterName")
	private String cupsSirhPrinterName;
	
	@Override
	protected void executeInternal(JobExecutionContext arg0)
			throws JobExecutionException {
		
		PrintJob job = null;
		
		try {
			job = getNextPrintJob();
			PrinterHelper pH = new PrinterHelper(cupsServerHostName, cupsServerPort, cupsSirhPrinterName, "SIRH - Impression des documents de commissions d'avancements");
			initializePrintJob(job);
			generateAvancementsReport(job);
			downloadRelatedEaes(job);
			printAllDocuments(job, pH);
		} catch (Exception e) {
			throw new JobExecutionException(String.format("An error occured during 'Avancement Print Job' [%s]", job != null ? job.getJobId() : "-"), e);
		} finally {
			try {
				wipeJobDocuments(job);
			}
			catch (Exception e) {
				
			}
		}
	}
	
	@Override
	public PrintJob getNextPrintJob() {

		return printJobDao.getNextPrintJob();
	}
	
	@Override
	public void initializePrintJob(PrintJob job) {

		SimpleDateFormat df = new SimpleDateFormat("yyyMMdd-HHmmss");
		String jobId = String.format("%s_%s_%s_%s", df.format(helper.getCurrentDate()), job.getAgentId(), job.getIdCap(), job.getIdCadreEmploi());
		job.setJobId(jobId);
		job.setStatus(AvancementsWithEaesMassPrintJobStatusEnum.START.toString());
		printJobDao.updateJobIdAndStatus(job);
	}

	@Override
	public void generateAvancementsReport(PrintJob job) throws Exception {
		
		// Update status
		job.setStatus(AvancementsWithEaesMassPrintJobStatusEnum.AVCT_REPORT.toString());
		printJobDao.updateStatus(job);

		// TODO Download front and back page and add it to the list of prints
		
		// download report and add it to the list of prints
		String targetReportFilePath = String.format("%s%s_001_%s", avcstTempWorkspacePath, job.getJobId(), "avct_table_report.pdf");
		reportingService.getTableauAvancementsReportAndSaveItToFile(job.getIdCap(), job.getIdCadreEmploi(), targetReportFilePath);
		job.getFilesToPrint().add(targetReportFilePath);
	}

	@Override
	public void downloadRelatedEaes(PrintJob job) throws Exception {
		
		// Update status
		job.setStatus(AvancementsWithEaesMassPrintJobStatusEnum.EAE_DOWNLOAD.toString());
		printJobDao.updateStatus(job);
		
		// Get the list of EAEs documents to download from sharepoint
		Map<String, String> sirhParams = new HashMap<String, String>();
		sirhParams.put("idCap", String.valueOf(job.getIdCap()));
		sirhParams.put("idCadreEmploi", String.valueOf(job.getIdCadreEmploi()));
		List<String> eaesToDownload = downloadDocumentService.downloadJsonDocumentAs(String.class, sirhWsEndpointUrl, sirhParams);

		Integer i = job.getFilesToPrint().size();
		
		// For each document, download it to local temp path
		for(String eaeId : eaesToDownload) {
			String eaeLocalFilePath = String.format("%s%s_%03d_%s.pdf", avcstTempWorkspacePath, job.getJobId(), i++ ,eaeId);
			Map<String, String> params = new HashMap<String, String>();
			params.put("eaeId", eaeId);
			downloadDocumentService.downloadDocumentToLocalPath(sharepointEaeDocBaseUrl, params, eaeLocalFilePath);
			job.getFilesToPrint().add(eaeLocalFilePath);
		}
	}

	@Override
	public void printAllDocuments(PrintJob job, PrinterHelper pH) throws Exception {

		// Update status
		job.setStatus(AvancementsWithEaesMassPrintJobStatusEnum.QUEUE_PRINT.toString());
		printJobDao.updateStatus(job);
		
		 // Send each file to the printer
		for(String filePath : job.getFilesToPrint()) {
			Map<String, String> properties = createPrintProperties(job, filePath);
			pH.printDocument(filePath, properties);
		}
	}

	private Map<String, String> createPrintProperties(PrintJob job, String filePath) {

		Map<String, String> attributes = new HashMap<String, String>();
        attributes.put("", "job-name=SIRH");
        attributes.put("job-attributes", String.format("job-name=%s", job.getJobId()));
        attributes.put("job-attributes", "job-more-info=Impression d'une commission d'avancement");
        attributes.put("job-attributes", String.format("job-originating-user-name=%s", job.getAgentId()));
		attributes.put("job-attributes", "detailed-name=Impression d'une commission d'avancement");
		attributes.put("job-attributes", String.format("document-name=%s", filePath));
		attributes.put("job-attributes", "document-natural-language=FR");
        
        return attributes;
	}

	@Override
	public void wipeJobDocuments(PrintJob job) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateStatus(PrintJob job, String status) {
		// TODO Auto-generated method stub
		
	}

}
