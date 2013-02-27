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

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;
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
	@Qualifier("sirhWsAvctEaesEndpointUrl")
	private String sirhWsAvctEaesEndpointUrl;
	
	@Autowired
	@Qualifier("cupsServerHostName")
	private String cupsServerHostName;
	
	@Autowired
	@Qualifier("cupsServerPort")
	private int cupsServerPort;
	
	@Autowired
	@Qualifier("cupsSirhPrinterName")
	private String cupsSirhPrinterName;
	
	private FileSystemManager vfsManager;
	
	public FileSystemManager getVfsManager() throws FileSystemException {
		
		if (vfsManager == null)
			vfsManager = VFS.getManager();
		
		return vfsManager;
	}
	
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
		updateStatus(job, AvancementsWithEaesMassPrintJobStatusEnum.AVCT_REPORT);

		// TODO Download front and back page and add it to the list of prints
		
		// download report and add it to the list of prints
		String targetReportFilePath = String.format("%s%s_001_%s", avcstTempWorkspacePath, job.getJobId(), "avct_table_report.pdf");
		reportingService.getTableauAvancementsReportAndSaveItToFile(job.getIdCap(), job.getIdCadreEmploi(), targetReportFilePath);
		job.getFilesToPrint().add(targetReportFilePath);
	}

	@Override
	public void downloadRelatedEaes(PrintJob job) throws Exception {
		
		// Update status
		updateStatus(job, AvancementsWithEaesMassPrintJobStatusEnum.EAE_DOWNLOAD);
		
		// Get the list of EAEs documents to download from sharepoint
		Map<String, String> sirhParams = new HashMap<String, String>();
		sirhParams.put("idCap", String.valueOf(job.getIdCap()));
		sirhParams.put("idCadreEmploi", String.valueOf(job.getIdCadreEmploi()));
		List<String> eaesToDownload = downloadDocumentService.downloadJsonDocumentAs(String.class, sirhWsAvctEaesEndpointUrl, sirhParams);

		Integer i = job.getFilesToPrint().size();
		
		// For each document, download it to local temp path
		for(String eaeId : eaesToDownload) {
			String locaFilePath = copyEaeToLocalPathAndPrint(job, eaeId, i++);
			job.getFilesToPrint().add(locaFilePath);
		}
	}

	@Override
	public void printAllDocuments(PrintJob job, PrinterHelper pH) throws Exception {

		// Update status
		updateStatus(job, AvancementsWithEaesMassPrintJobStatusEnum.QUEUE_PRINT);
		
		 // Send each file to the printer
		for(String filePath : job.getFilesToPrint()) {
			Map<String, String> properties = createPrintProperties(job, filePath);
			pH.printDocument(filePath, properties);
		}
	}

	@Override
	public void wipeJobDocuments(PrintJob job) throws Exception {
		
		FileSystemManager fsManager = getVfsManager();
		
		for(String file : job.getFilesToPrint()) {
			fsManager.resolveFile(file).delete();
		}
		
	}

	@Override
	public void updateStatus(PrintJob job, AvancementsWithEaesMassPrintJobStatusEnum status) {
		job.setStatus(status.toString());
		printJobDao.updateStatus(job);
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

	private String copyEaeToLocalPathAndPrint(PrintJob job, String eaeId, int sequenceNumber) throws Exception {
		
		String eaeRemoteFileUrl = sharepointEaeDocBaseUrl.concat(eaeId);
		String eaeLocalFilePath = String.format("%s%s_%03d_%s.pdf", avcstTempWorkspacePath, job.getJobId(), sequenceNumber ,eaeId);
		
		try {
			FileSystemManager fsManager = getVfsManager();
			FileObject fsource = fsManager.resolveFile(eaeRemoteFileUrl);
			FileObject ftarget = fsManager.resolveFile(eaeLocalFilePath);
			ftarget.copyFrom(fsource, null);
		} catch (FileSystemException e) {
			throw new Exception(String.format("An error occured when trying to copy EAE document [%s] url [%s] to local path [%s]", eaeId, eaeRemoteFileUrl, eaeLocalFilePath), e);
		}
		
		return eaeLocalFilePath;
	}
}
