package nc.noumea.mairie.sirh.job;

import java.util.Date;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Service;

import nc.noumea.mairie.ptg.dao.IPointagesDao;
import nc.noumea.mairie.ptg.domain.TitreRepasExportEtatsPayeurTask;
import nc.noumea.mairie.sirh.service.IDownloadDocumentService;
import nc.noumea.mairie.sirh.tools.IIncidentLoggerService;

@Service
@DisallowConcurrentExecution
public class TitreRepasExportEtatsPayeurJob extends QuartzJobBean {

	private Logger						logger									= LoggerFactory.getLogger(TitreRepasExportEtatsPayeurJob.class);

	@Autowired
	private IPointagesDao				pointagesDao;

	@Autowired
	@Qualifier("SIRH_PTG_WS_Base_URL")
	private String						SIRH_PTG_WS_Base_URL;

	@Autowired
	private IDownloadDocumentService	downloadDocumentService;

	@Autowired
	private IIncidentLoggerService		incidentLoggerService;

	private static final String			SIRH_PTG_WS_TitreRepasGenerePayeurUrl	= "titreRepas/genereEtatPayeur?idAgentConnecte=";

	@Override
	protected void executeInternal(JobExecutionContext arg0) throws JobExecutionException {

		pointagesDao.beginTransaction();
		TitreRepasExportEtatsPayeurTask eT = pointagesDao.getNextTitreRepasExportEtatsPayeurTask();

		if (eT == null) {
			logger.info("Did not find any TitreRepasExportEtatsPayeurTask to process... exiting job.");
			pointagesDao.rollBackTransaction();
			return;
		}

		logger.info("Processing TitreRepasExportEtatsPayeurTask id [{}] , schedulded by [{}]...", eT.getIdTitreRepasExportEtatsPayeurTask(),
				eT.getIdAgent());

		try {
			// on appele le WS qui fait l'etat du payeur
			String url = String.format("%s%s%s", SIRH_PTG_WS_Base_URL, SIRH_PTG_WS_TitreRepasGenerePayeurUrl, eT.getIdAgent());
			logger.info("Calling url {}...", url);
			downloadDocumentService.downloadDocumentAs(String.class, url, null);

			// At this point, everything went allright, the status can be
			// updated to OK
			eT.setTaskStatus("OK");
		} catch (Exception ex) {
			logger.error("An error occured trying to process TitreRepasExportEtatsPayeurTask :", ex);
			eT.setTaskStatus(String.format("Erreur: %s", ex.getMessage()));
			incidentLoggerService.logIncident("TitreRepasExportEtatsPayeurJob", ex.getCause() == null ? ex.getMessage() : ex.getCause().getMessage(),
					"Erreur lors de l export des Etats Payeur des titres repas", ex);
		}

		eT.setDateExport(new Date());
		pointagesDao.commitTransaction();

		logger.info("Processed TitreRepasExportEtatsPayeurTask id [{}].", eT.getIdTitreRepasExportEtatsPayeurTask());
	}

}
