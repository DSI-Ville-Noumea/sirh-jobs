package nc.noumea.mairie.sirh.job;

import java.util.Date;

import nc.noumea.mairie.ptg.dao.IPointagesDao;
import nc.noumea.mairie.ptg.domain.ExportEtatsPayeurTask;
import nc.noumea.mairie.sirh.service.IDownloadDocumentService;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Service;

@Service
@DisallowConcurrentExecution
public class PointagesExportEtatsPayeurJob extends QuartzJobBean {

	private Logger logger = LoggerFactory.getLogger(PointagesExportEtatsPayeurJob.class);

	@Autowired
	private IPointagesDao pointagesDao;

	@Autowired
	@Qualifier("SIRH_PTG_WS_ExportEtatsPayeurTaskUrl")
	private String SIRH_PTG_WS_ExportEtatsPayeurTaskUrl;

	@Autowired
	@Qualifier("SIRH_PTG_WS_ExportEtatsPayeurDoneUrl")
	private String SIRH_PTG_WS_ExportEtatsPayeurDoneUrl;
	
	@Autowired
	@Qualifier("SIRH_PTG_WS_ExportEtatsPayeurDoneStopUrl")
	private String SIRH_PTG_WS_ExportEtatsPayeurDoneStopUrl;

	@Autowired
	private IDownloadDocumentService downloadDocumentService;

	@Override
	protected void executeInternal(JobExecutionContext arg0) throws JobExecutionException {

		pointagesDao.beginTransaction();
		ExportEtatsPayeurTask eT = pointagesDao.getNextExportEtatsPayeurTask();

		if (eT == null) {
			logger.info("Did not find any ExportEtatsPayeurTask to process... exiting job.");
			pointagesDao.rollBackTransaction();
			return;
		}

		logger.info("Processing ExportEtatsPayeurTask id [{}] for type [{} : {}], schedulded by [{}]...",
				eT.getIdExportEtatsPayeurTask(), eT.getTypeChainePaie(), eT.getTaskStatus(), eT.getIdAgent());

		try {
			// First WS call to export BIRT reports and save them
			String url = String.format("%s%s", SIRH_PTG_WS_ExportEtatsPayeurTaskUrl, eT.getIdExportEtatsPayeurTask());
			logger.info("Calling url {}...", url);
			downloadDocumentService.downloadDocumentAs(String.class, url, null);

			// Second WS call to mark all pointages as JOURNALISE 
			url = String.format("%s%s", SIRH_PTG_WS_ExportEtatsPayeurDoneUrl, eT.getIdExportEtatsPayeurTask());
			logger.info("Calling url {}...", url);
			downloadDocumentService.downloadDocumentAs(String.class, url, null);

			// At this point, everything went allright, the status can be updated to OK
			eT.setTaskStatus("OK");
		} catch (Exception ex) {
			logger.error("An error occured trying to process ExportEtatsPayeurTask :", ex);
			eT.setTaskStatus(String.format("Erreur: %s", ex.getMessage()));
		}

		// At last we call to stop the process and move the workflow status
		try {
			String url = String.format("%s%s", SIRH_PTG_WS_ExportEtatsPayeurDoneStopUrl, eT.getTypeChainePaie());
			logger.info("Calling url {}...", url);
			downloadDocumentService.downloadDocumentAs(String.class, url, null);
		} catch (Exception ex) {
			logger.error("An error occured trying to stop the ExportEtatsPayeurTask :", ex);
		}
		
		eT.setDateExport(new Date());
		pointagesDao.commitTransaction();

		logger.info("Processed ExportEtatsPayeurTask id [{}].", eT.getIdExportEtatsPayeurTask());
	}

}
