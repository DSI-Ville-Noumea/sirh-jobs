package nc.noumea.mairie.sirh.job;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.internet.MimeMessage;

import org.apache.commons.io.IOUtils;
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

import nc.noumea.mairie.alfresco.cmis.IAlfrescoCMISService;
import nc.noumea.mairie.sirh.dao.IAvctCapPrintJobDao;
import nc.noumea.mairie.sirh.domain.AvctCapPrintJob;
import nc.noumea.mairie.sirh.eae.dao.DaoException;
import nc.noumea.mairie.sirh.service.IDownloadDocumentService;
import nc.noumea.mairie.sirh.service.IReportingService;
import nc.noumea.mairie.sirh.service.PrinterHelper;
import nc.noumea.mairie.sirh.tools.AvancementsWithEaesMassPrintJobStatusEnum;
import nc.noumea.mairie.sirh.tools.Helper;
import nc.noumea.mairie.sirh.ws.IRadiWSConsumer;
import nc.noumea.mairie.sirh.ws.dto.LightUser;

@Service
@DisallowConcurrentExecution
public class AvancementsWithEaesMassPrintJob extends QuartzJobBean implements IAvancementsWithEaesMassPrintJob {

	private Logger						logger	= LoggerFactory.getLogger(AvancementsWithEaesMassPrintJob.class);

	@Autowired
	private Helper						helper;

	@Autowired
	private IAvctCapPrintJobDao			printJobDao;

	@Autowired
	private IReportingService			reportingService;

	@Autowired
	private IDownloadDocumentService	downloadDocumentService;

	@Autowired
	private IRadiWSConsumer				radiWSConsumer;

	@Autowired
	private JavaMailSender				mailSender;

	@Autowired
	private VelocityEngine				velocityEngine;

	@Autowired
	private IAlfrescoCMISService		alfrescoCMISService;

	@Autowired
	@Qualifier("sirhWsAvctEaesEndpointUrl")
	private String						sirhWsAvctEaesEndpointUrl;

	@Autowired
	@Qualifier("cupsServerHostName")
	private String						cupsServerHostName;

	@Autowired
	@Qualifier("cupsServerPort")
	private int							cupsServerPort;

	@Autowired
	@Qualifier("cupsSirhPrinterName")
	private String						cupsSirhPrinterName;

	private FileSystemManager			vfsManager;

	public FileSystemManager getVfsManager() throws FileSystemException {

		if (vfsManager == null)
			vfsManager = VFS.getManager();

		return vfsManager;
	}

	@Override
	protected void executeInternal(JobExecutionContext arg0) throws JobExecutionException {

		AvctCapPrintJob job = null;

		try {

			job = getNextPrintJob();
			if (job == null)
				return;

			// initialize printer helper early so that documents are not
			// downloaded when no printer is reachable
			PrinterHelper pH = new PrinterHelper(cupsServerHostName, cupsServerPort,
					String.format("http://%s:%s/printers/%s", cupsServerHostName, cupsServerPort, cupsSirhPrinterName),
					"SIRH - Impression des documents de commissions d'avancements");

			// initialize the print job id and status
			initializePrintJob(job);

			// generate the avct reports, cover and back pages
			generateAvancementsReport(job, pH);

			// if selected, the eaes should be downloaded from sharepoint
			if (job.isEaes())
				printRelatedEaes(job, pH);

		} catch (Exception e) {
			logger.error("An error occured during 'Avancement Print Job'", e);

			if (job != null) {

				try {
					sendErrorEmail(job);
				} catch (DaoException e1) {
					logger.error("An error occured while sending the error email", e);
				}

				updateStatus(job, AvancementsWithEaesMassPrintJobStatusEnum.ERROR);
			}

			throw new JobExecutionException(String.format("An error occured during 'Avancement Print Job' [%s]", job != null ? job.getJobId() : "-"),
					e);
		} finally {
		}

		// set the job as DONE
		updateStatus(job, AvancementsWithEaesMassPrintJobStatusEnum.DONE);
	}

	@Override
	public AvctCapPrintJob getNextPrintJob() {

		AvctCapPrintJob result = printJobDao.getNextPrintJob();

		if (result != null)
			logger.info("Found 1 print job for CAP [{}] CadreEmploi [{}] avisEAE[{}] submitted by [{}] on [{}]", result.getIdCap(),
					result.getIdCadreEmploi(), result.isAvisEAE(), result.getAgentId(), result.getSubmissionDate());
		else
			logger.info("Did not find any print job");

		return result;
	}

	@Override
	public void initializePrintJob(AvctCapPrintJob job) {

		SimpleDateFormat df = new SimpleDateFormat("yyyMMdd-HHmmss");
		String jobId = String.format("SIRH_AVCT_%s_%s_%s_%s_%s", df.format(helper.getCurrentDate()), job.getAgentId(), job.getIdCap(),
				job.getIdCadreEmploi(), job.isAvisEAE());
		job.setJobId(jobId);
		updateStatus(job, AvancementsWithEaesMassPrintJobStatusEnum.START);

		logger.info("Generated Job Id: [{}]", job.getJobId());
	}

	@Override
	public void generateAvancementsReport(AvctCapPrintJob job, PrinterHelper pH) throws AvancementsWithEaesMassPrintException {

		// Update status
		updateStatus(job, AvancementsWithEaesMassPrintJobStatusEnum.AVCT_REPORT);
		InputStream inputStream = null;
		try {
			// download report and add it to the list of prints
			String targetReportFilePath = String.format("%s_001_%s", job.getJobId(), "avct_table_report.pdf");
			inputStream = reportingService.getTableauAvancementsReport(job.getIdCap(), job.getIdCadreEmploi(), job.isAvisEAE());

			pH.printDocument(inputStream, targetReportFilePath, job.getLogin());

			targetReportFilePath = String.format("%s_000_%s", job.getJobId(), "firstPage.pdf");
			inputStream = reportingService.getAvctFirstLastPrintPage(job.getJobId(), job.getLogin(), job.getCodeCap(), job.getLibCadreEmploi(),
					job.getSubmissionDate(), true, job.isEaes());

			pH.printDocument(inputStream, targetReportFilePath, job.getLogin());

			targetReportFilePath = String.format("%s_999_%s", job.getJobId(), "lastPage.pdf");
			inputStream = reportingService.getAvctFirstLastPrintPage(job.getJobId(), job.getLogin(), job.getCodeCap(), job.getLibCadreEmploi(),
					job.getSubmissionDate(), false, job.isEaes());

			pH.printDocument(inputStream, targetReportFilePath, job.getLogin());

		} catch (Exception e) {
			throw new AvancementsWithEaesMassPrintException(
					String.format("An error occured while generating AVCT reports for job id [%s]", job.getJobId()), e);
		} finally {
			IOUtils.closeQuietly(inputStream);
		}

	}

	@Override
	public void printRelatedEaes(AvctCapPrintJob job, PrinterHelper pH) throws AvancementsWithEaesMassPrintException {

		// Update status
		updateStatus(job, AvancementsWithEaesMassPrintJobStatusEnum.EAE_DOWNLOAD);

		try {
			// Get the list of EAEs documents to download from sharepoint
			Map<String, String> sirhParams = new HashMap<String, String>();
			sirhParams.put("idCap", String.valueOf(job.getIdCap()));
			sirhParams.put("idCadreEmploi", String.valueOf(job.getIdCadreEmploi()));
			sirhParams.put("avisEAE", String.valueOf(job.isAvisEAE()));
			List<String> eaesToDownload = downloadDocumentService.downloadJsonDocumentAsList(String.class, sirhWsAvctEaesEndpointUrl, sirhParams);

			Integer i = job.getFilesToPrint().size();

			// For each document, download it to local temp path
			for (String eaeId : eaesToDownload) {
				logger.info("Downloading EAE document [{}] to local path...", eaeId);
				printEae(job, eaeId, ++i, pH);
			}

		} catch (Exception e) {
			throw new AvancementsWithEaesMassPrintException(
					String.format("An error occured while downloading AVCT EAEs reports for job id [%s]", job.getJobId()), e);
		}
	}

	@Override
	public void updateStatus(AvctCapPrintJob job, AvancementsWithEaesMassPrintJobStatusEnum status) {

		job.setStatus(status.toString());
		job.setStatusDate(helper.getCurrentDate());
		logger.info("Changing Job Id [{}] to status [{}]...", job.getJobId(), job.getStatus());

		printJobDao.updateAvctCapPrintJob(job);
	}

	public void printEae(AvctCapPrintJob job, String eaeId, int sequenceNumber, PrinterHelper pH) throws Exception {

		String baseName = String.format("%s_%03d_%s.pdf", job.getJobId(), sequenceNumber, eaeId);

		// on recupère l'EAE sous Alfresco
		File file = alfrescoCMISService.getFile(eaeId);

		// Copy doc using downloadDocument
		logger.debug("Print [{}]", baseName);

		try {
			pH.printDocument(new FileInputStream(file), baseName, job.getLogin());
		} catch (Exception ex) {
			throw new Exception("An error occured while copying a remote EAE into the local file path", ex);
		} finally {
		}
	}

	public void sendErrorEmail(final AvctCapPrintJob job) throws DaoException {

		logger.debug("Sending error email for job id [{}] on status {}", job.getJobId(), null == job.getStatus() ? "" : job.getStatus().toString());

		final LightUser user = radiWSConsumer.retrieveAgentFromLdapFromMatricule(helper.getEmployeeNumber(job.getAgentId()));

		MimeMessagePreparator preparator = new MimeMessagePreparator() {
			@SuppressWarnings({ "rawtypes", "unchecked" })
			public void prepare(MimeMessage mimeMessage) throws Exception {
				MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");

				// Set the To
				message.setTo(user.getMail());

				// Set the body with velocity
				Map model = new HashMap();
				model.put("jobId", job.getJobId());
				model.put("CAP", job.getCodeCap());
				model.put("CE", job.getLibCadreEmploi());
				model.put("status", null == job.getStatus() ? "" : job.getStatus().toString());
				String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "templates/sirhAvctErrorMailTemplate.vm", "UTF-8", model);
				message.setText(text, true);

				// Set the subject
				message.setSubject(String.format("[SIRH-JOBS] Erreur lors de l'impression des avancements"));
			}
		};

		// Actually send the email
		mailSender.send(preparator);
	}
}
