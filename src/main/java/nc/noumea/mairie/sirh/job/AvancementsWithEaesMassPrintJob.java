package nc.noumea.mairie.sirh.job;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.noumea.mairie.sirh.dao.IAvctCapPrintJobDao;
import nc.noumea.mairie.sirh.domain.AvctCapPrintJob;
import nc.noumea.mairie.sirh.service.IDownloadDocumentService;
import nc.noumea.mairie.sirh.service.IReportingService;
import nc.noumea.mairie.sirh.service.PrinterHelper;
import nc.noumea.mairie.sirh.tools.AvancementsWithEaesMassPrintJobStatusEnum;
import nc.noumea.mairie.sirh.tools.Helper;

import org.apache.commons.io.IOUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Service;

@Service
public class AvancementsWithEaesMassPrintJob extends QuartzJobBean implements
		IAvancementsWithEaesMassPrintJob {
	
	private Logger logger = LoggerFactory.getLogger(AvancementsWithEaesMassPrintJob.class);
	
	@Autowired
	private Helper helper;
	
	@Autowired
	private IAvctCapPrintJobDao printJobDao;
	
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
	
	@Autowired
	@Qualifier("gedWebdavUser")
	private String gedWebdavUser;
	
	@Autowired
	@Qualifier("gedWebdavPwd")
	private String gedWebdavPwd;
	
	private FileSystemManager vfsManager;
	
	public FileSystemManager getVfsManager() throws FileSystemException {
		
		if (vfsManager == null)
			vfsManager = VFS.getManager();
		
		return vfsManager;
	}
	
	@Override
	protected void executeInternal(JobExecutionContext arg0)
			throws JobExecutionException {
		
		AvctCapPrintJob job = null;
		
		try {
			
			job = getNextPrintJob();
			if (job == null)
				return;
			
			PrinterHelper pH = new PrinterHelper(cupsServerHostName, cupsServerPort, cupsSirhPrinterName, "SIRH - Impression des documents de commissions d'avancements");
			initializePrintJob(job);
			generateAvancementsReport(job);
			
			if (job.isEaes())
				downloadRelatedEaes(job);
			
			printAllDocuments(job, pH);
			
			updateStatus(job, AvancementsWithEaesMassPrintJobStatusEnum.DONE);
			
		} catch (Exception e) {
			logger.error("An error occured during 'Avancement Print Job'", e);
			throw new JobExecutionException(
					String.format("An error occured during 'Avancement Print Job' [%s]", 
							job != null ? job.getJobId() : "-"), e);
		} finally {
			try {
				if (job != null)
					wipeJobDocuments(job);
			}
			catch (Exception e) {
				logger.error("An error occured during 'wipeJobDocuments'", e);
			}
		}
	}
	
	@Override
	public AvctCapPrintJob getNextPrintJob() {

		AvctCapPrintJob result = printJobDao.getNextPrintJob();
		
		if (result != null)
			logger.info("Found 1 print job for CAP [{}] CadreEmploi [{}] submitted by [{}] on [{}]", 
					result.getIdCap(), result.getIdCadreEmploi(), result.getAgentId(), result.getSubmissionDate());
		else
			logger.info("Did not find any print job");
		
		return result;
	}
	
	@Override
	public void initializePrintJob(AvctCapPrintJob job) {

		SimpleDateFormat df = new SimpleDateFormat("yyyMMdd-HHmmss");
		String jobId = String.format("SIRH_AVCT_%s_%s_%s_%s", df.format(helper.getCurrentDate()), job.getAgentId(), job.getIdCap(), job.getIdCadreEmploi());
		job.setJobId(jobId);
		updateStatus(job, AvancementsWithEaesMassPrintJobStatusEnum.START);
		
		logger.info("Generated Job Id: [{}]", job.getJobId());
	}

	@Override
	public void generateAvancementsReport(AvctCapPrintJob job) throws AvancementsWithEaesMassPrintException {
		
		// Update status
		updateStatus(job, AvancementsWithEaesMassPrintJobStatusEnum.AVCT_REPORT);

		try {
			// download report and add it to the list of prints
			String targetReportFilePath = String.format("%s%s_001_%s", avcstTempWorkspacePath, job.getJobId(), "avct_table_report.pdf");
			reportingService.getTableauAvancementsReportAndSaveItToFile(job.getIdCap(), job.getIdCadreEmploi(), targetReportFilePath);
			job.getFilesToPrint().add(targetReportFilePath);
			
			targetReportFilePath = String.format("%s%s_000_%s", avcstTempWorkspacePath, job.getJobId(), "firstPage.pdf");
			reportingService.getAvctFirstLastPrintPage(job.getJobId(), job.getLogin(), job.getCodeCap(), job.getLibCadreEmploi(), job.getSubmissionDate(), true, job.isEaes(), targetReportFilePath);
			job.getFilesToPrint().add(targetReportFilePath);
			
			targetReportFilePath = String.format("%s%s_999_%s", avcstTempWorkspacePath, job.getJobId(), "lastPage.pdf");
			reportingService.getAvctFirstLastPrintPage(job.getJobId(), job.getLogin(), job.getCodeCap(), job.getLibCadreEmploi(), job.getSubmissionDate(), false, job.isEaes(), targetReportFilePath);
			job.getFilesToPrint().add(targetReportFilePath);
		} catch (Exception e) {
			throw new AvancementsWithEaesMassPrintException(
					String.format("An error occured while generating AVCT reports for job id [%s]", 
							job.getJobId()), e);
		}
		
	}

	@Override
	public void downloadRelatedEaes(AvctCapPrintJob job) throws AvancementsWithEaesMassPrintException {
		
		// Update status
		updateStatus(job, AvancementsWithEaesMassPrintJobStatusEnum.EAE_DOWNLOAD);
		
		try {
			// Get the list of EAEs documents to download from sharepoint
			Map<String, String> sirhParams = new HashMap<String, String>();
			sirhParams.put("idCap", String.valueOf(job.getIdCap()));
			sirhParams.put("idCadreEmploi", String.valueOf(job.getIdCadreEmploi()));
			List<String> eaesToDownload = downloadDocumentService.downloadJsonDocumentAsList(String.class, sirhWsAvctEaesEndpointUrl, sirhParams);

			Integer i = job.getFilesToPrint().size();

			// For each document, download it to local temp path
			for (String eaeId : eaesToDownload) {
				logger.info("Downloading EAE document [{}] to local path...", eaeId);
				String locaFilePath = copyEaeToLocalPath(job, eaeId, ++i);
				job.getFilesToPrint().add(locaFilePath);
			}

		} catch (Exception e) {
			throw new AvancementsWithEaesMassPrintException(
					String.format("An error occured while downloading AVCT EAEs reports for job id [%s]",
							job.getJobId()), e);
		}
	}

	@Override
	public void printAllDocuments(AvctCapPrintJob job, PrinterHelper pH) throws AvancementsWithEaesMassPrintException {

		// Update status
		updateStatus(job, AvancementsWithEaesMassPrintJobStatusEnum.QUEUE_PRINT);
		
		Collections.sort(job.getFilesToPrint());
		
		try {
			// Send each file to the printer
			for(String filePath : job.getFilesToPrint()) {
				logger.info("Sending document [{}] to printer...", filePath);
				Map<String, String> properties = createPrintProperties(job, filePath);
				pH.printDocument(filePath, properties);
			}
		} catch (Exception e) {
			throw new AvancementsWithEaesMassPrintException(
					String.format("An error occured while sending AVCT and EAEs reports to print server for job id [%s]",
							job.getJobId()), e);
		}
	}

	@Override
	public void wipeJobDocuments(AvctCapPrintJob job) throws AvancementsWithEaesMassPrintException {

		logger.info("Removing documents from temp path...", job.getJobId(), job.getStatus());
		
		// Update status
		updateStatus(job, AvancementsWithEaesMassPrintJobStatusEnum.HOUSEKEEPING);
		
		try {

			FileSystemManager fsManager = getVfsManager();
			fsManager.resolveFile(avcstTempWorkspacePath);
			for (FileObject file : fsManager.resolveFile(avcstTempWorkspacePath).getChildren()) {
				file.delete();
			}
			
		} catch (Exception e) {
			throw new AvancementsWithEaesMassPrintException(
					String.format("An error occured while deleting all AVCT and EAEs documents from temp path for job id [%s]",
							job.getJobId()), e);
		}

	}

	@Override
	public void updateStatus(AvctCapPrintJob job, AvancementsWithEaesMassPrintJobStatusEnum status) {

		job.setStatus(status.toString());
		job.setStatusDate(helper.getCurrentDate());
		logger.info("Changing Job Id [{}] to status [{}]...", job.getJobId(), job.getStatus());
		
		printJobDao.updateAvctCapPrintJob(job);
	}

	private Map<String, String> createPrintProperties(AvctCapPrintJob job, String filePath) {

		Map<String, String> attributes = new HashMap<String, String>();
        attributes.put("job-name", job.getJobId());
        attributes.put("job-more-info", "Impression d'une commission d'avancement");
        attributes.put("job-originating-user-name", job.getLogin());
		attributes.put("detailed-name", String.format("Impression de la commission d'avancement %s %s", job.getCodeCap(), job.getLibCadreEmploi()));
		attributes.put("document-name", filePath);
		attributes.put("document-natural-language", "FR");
        
        return attributes;
	}

	public String copyEaeToLocalPath(AvctCapPrintJob job, String eaeId, int sequenceNumber) throws Exception {
		
		String eaeLocalFilePath = String.format("%s%s_%03d_%s.pdf", avcstTempWorkspacePath, job.getJobId(), sequenceNumber ,eaeId);
		String eaeRemoteFileUri = null;
		
		// GET url from sharepoint
		String url = sharepointEaeDocBaseUrl.concat(eaeId);
		String webDavUri = downloadDocumentService.downloadDocumentAs(String.class, url, null);
		logger.debug("Sharepoint WS query: URL [{}] Response [{}]", url, webDavUri);
		
		// format result
		eaeRemoteFileUri = String.format(webDavUri, gedWebdavUser, gedWebdavPwd);
		
		// Copy doc using downloadDocument
		logger.debug("Copying [{}] into [{}]", eaeRemoteFileUri, eaeLocalFilePath);
		
		BufferedInputStream in = null;
		BufferedOutputStream out = null;
		
		try {
			in = new BufferedInputStream(getVfsManager().resolveFile(eaeRemoteFileUri).getContent().getInputStream());
			out = new BufferedOutputStream(getVfsManager().resolveFile(eaeLocalFilePath).getContent().getOutputStream(false));
			IOUtils.copy(in, out);
		} catch (Exception ex) {
			throw new Exception("An error occured while copying a remote EAE into the local file path", ex);
		} finally {
			IOUtils.closeQuietly(out);
			IOUtils.closeQuietly(in);
		}
		
//		downloadDocumentService.downloadDocumentToLocalPathUsingVfs(eaeRemoteFileUri, eaeLocalFilePath);
		
		return eaeLocalFilePath;
	}
}
