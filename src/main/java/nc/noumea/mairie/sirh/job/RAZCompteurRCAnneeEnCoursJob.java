package nc.noumea.mairie.sirh.job;

import java.util.ArrayList;
import java.util.List;

import nc.noumea.mairie.sirh.tools.IIncidentLoggerService;
import nc.noumea.mairie.sirh.ws.IAbsWSConsumer;
import nc.noumea.mairie.sirh.ws.ReturnMessageDto;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Service;

@Service
@DisallowConcurrentExecution
public class RAZCompteurRCAnneeEnCoursJob extends QuartzJobBean {

	private Logger logger = LoggerFactory.getLogger(RAZCompteurRCAnneeEnCoursJob.class);
	
	@Autowired
	private IAbsWSConsumer absWSConsumer;

	@Autowired
	private IIncidentLoggerService incidentLoggerService;
	
	@Override
	protected void executeInternal(JobExecutionContext arg0) throws JobExecutionException {
		
		logger.info("Start RAZCompteurRCAnneeEnCoursJob");
		
		List<Integer> listeIdCompteur = new ArrayList<Integer>();
		try {
			listeIdCompteur = absWSConsumer.getListeCompteurAnneeEnCours();
		} catch (Exception ex) {
			logger.error("Une erreur technique est survenue lors du traitement : ", ex);
			incidentLoggerService.logIncident("RAZCompteurRCAnneeEnCoursJob", ex.getMessage(), ex);
		}
		
		logger.info("Found {} current year Repos Comp. counters to update...", listeIdCompteur.size());
		
		for (Integer idCompteur : listeIdCompteur) {
			
			logger.debug("Processing counter id {}...", idCompteur);
			
			ReturnMessageDto result = null;
			try {
				result = absWSConsumer.resetCompteurAnneeEnCours(idCompteur);
			} catch (Exception ex) {
				logger.error("Une erreur technique est survenue lors du traitement : ", ex);
				incidentLoggerService.logIncident("RAZCompteurRCAnneeEnCoursJob", ex.getMessage(), ex);
			}
			
			if (result != null && result.getErrors().size() != 0) {
				for (String err : result.getErrors()) {
					logger.info(err);
				}
			}
		}
		
		logger.info("Processed RAZCompteurRCAnneeEnCoursJob");
	}
}
