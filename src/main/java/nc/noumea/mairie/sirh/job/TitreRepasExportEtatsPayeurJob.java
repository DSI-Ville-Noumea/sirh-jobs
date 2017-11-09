package nc.noumea.mairie.sirh.job;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Service;

import com.sun.jersey.api.client.ClientResponse;

import nc.noumea.mairie.ptg.dao.IPointagesDao;
import nc.noumea.mairie.ptg.domain.TitreRepasExportEtatsPayeurTask;
import nc.noumea.mairie.sirh.service.IDownloadDocumentService;
import nc.noumea.mairie.sirh.tools.IIncidentLoggerService;
import nc.noumea.mairie.sirh.ws.ReturnMessageDto;

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

	private static final String			SIRH_PTG_WS_TitreRepasGenerePayeurUrl	= "titreRepas/genereEtatPayeur";

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
			String url = String.format("%s%s", SIRH_PTG_WS_Base_URL, SIRH_PTG_WS_TitreRepasGenerePayeurUrl);

			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

			Map<String, String> parameters = new HashMap<String, String>();
			parameters.put("idAgentConnecte", String.valueOf(eT.getIdAgent()));
			parameters.put("dateGeneration", sdf.format(eT.getDateMonth()));
			
			ClientResponse res = downloadDocumentService.createAndFireRequest(url, parameters);

			ReturnMessageDto rmDto = downloadDocumentService.readResponse(ReturnMessageDto.class, res, url);


			// At this point, everything went allright, the status can be updated to OK
			if (rmDto != null) {
				// #38053 : jamais on n'affiche le messag d'erreur si il y a
				List<String> listErr = new ArrayList<>();
				if (rmDto.getErrors().size() > 0) {
					for (String erreur : rmDto.getErrors()) {
						if (!listErr.contains(erreur)) {
							listErr.add(erreur);
						}
					}
					eT.setTaskStatus("Erreur : " + StringUtils.join(listErr, "."));
				} else {
					eT.setTaskStatus("OK");
				}
			} else {
				eT.setTaskStatus("Erreur : ReturnMessageDto is null.");
			}
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
