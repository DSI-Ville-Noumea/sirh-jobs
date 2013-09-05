package nc.noumea.mairie.sirh.job;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.internet.MimeMessage;

import nc.noumea.mairie.ldap.dao.AgentLdapDaoException;
import nc.noumea.mairie.ldap.dao.IAgentLdapDao;
import nc.noumea.mairie.ldap.domain.AgentLdap;
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
import org.apache.velocity.app.VelocityEngine;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Service;
import org.springframework.ui.velocity.VelocityEngineUtils;

@Service
@DisallowConcurrentExecution
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
	private IAgentLdapDao agentLdapDao;
	
	@Autowired
	private JavaMailSender mailSender;
	
	@Autowired
	private VelocityEngine velocityEngine;
	
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
			
			// initialize printer helper early so that documents are not downloaded when no printer is reachable
			PrinterHelper pH = new PrinterHelper(cupsServerHostName, cupsServerPort, cupsSirhPrinterName, 
					"SIRH - Impression des documents de commissions d'avancements");
			
			// initialize the print job id and status
			initializePrintJob(job);
			
			// generate the avct reports, cover and back pages
			generateAvancementsReport(job);
			
			// if selected, the eaes should be downloaded from sharepoint
			if (job.isEaes())
				downloadRelatedEaes(job);
			
			// send all the documents above to the configured printer
			printAllDocuments(job, pH);
			
		} catch (Exception e) {
			logger.error("An error occured during 'Avancement Print Job'", e);
			
			if (job != null) {
				
				try {
					sendErrorEmail(job);
				} catch (AgentLdapDaoException e1) {
					logger.error("An error occured while sending the error email", e);
				}
				
				updateStatus(job, AvancementsWithEaesMassPrintJobStatusEnum.ERROR);
			}
			
			throw new JobExecutionException(
					String.format("An error occured during 'Avancement Print Job' [%s]", 
							job != null ? job.getJobId() : "-"), e);
		} finally {
			try {
				if (job == null)
					return;

				wipeJobDocuments(job);
			}
			catch (Exception e) {
				logger.error("An error occured during 'wipeJobDocuments'", e);
			}
		}

		// set the job as DONE
		updateStatus(job, AvancementsWithEaesMassPrintJobStatusEnum.DONE);
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
				pH.printDocument(filePath, job.getLogin());
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
	
	public void sendErrorEmail(final AvctCapPrintJob job) throws AgentLdapDaoException {
		
		logger.debug("Sending error email for job id [{}] on status {}", job.getJobId(), job.getStatus().toString());
		
		final AgentLdap agentTo = agentLdapDao.retrieveAgentFromLdapFromMatricule(String.valueOf(job.getAgentId()));
		
		MimeMessagePreparator preparator = new MimeMessagePreparator() {
	        @SuppressWarnings({ "rawtypes", "unchecked" })
			public void prepare(MimeMessage mimeMessage) throws Exception {
	            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
	            
	            // Set the To
	            message.setTo(agentTo.getMail());
	            
	            // Set the body with velocity
	            Map model = new HashMap();
	            model.put("jobId", job.getJobId());
	            model.put("CAP", job.getCodeCap());
	            model.put("CE", job.getLibCadreEmploi());
	            model.put("status", job.getStatus().toString());
	            String text = VelocityEngineUtils.mergeTemplateIntoString(
	               velocityEngine, "templates/sirhAvctErrorMailTemplate.vm", "UTF-8", model);
	            message.setText(text, true);
	            
	            // Set the subject
	            message.setSubject(String.format("[SIRH-JOBS] Erreur lors de l'impression des avancements"));
	         }
	      };
	      
	      // Actually send the email
	      mailSender.send(preparator);
	}
}
