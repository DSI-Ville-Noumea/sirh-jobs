package nc.noumea.mairie.sirh.job;

import java.util.ArrayList;
import java.util.List;

import nc.noumea.mairie.sirh.tools.IIncidentLoggerService;
import nc.noumea.mairie.sirh.tools.VoRedmineIncidentLogger;
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
public class RAZCompteurRCAnneePrecedenteJob extends QuartzJobBean {

	private Logger logger = LoggerFactory.getLogger(RAZCompteurRCAnneePrecedenteJob.class);
	
	@Autowired
	private IAbsWSConsumer absWSConsumer;

	@Autowired
	private IIncidentLoggerService incidentLoggerService;
	
	@Override
	protected void executeInternal(JobExecutionContext arg0) throws JobExecutionException {
		
		logger.info("Start RAZCompteurRCAnneePrecedenteJob");
		
		List<Integer> listeIdCompteur = new ArrayList<Integer>();
		
		try {
			listeIdCompteur = absWSConsumer.getListeCompteurAnneePrecedente();
		} catch (Exception ex) {
			logger.error("Une erreur technique est survenue lors du traitement : ", ex);
			incidentLoggerService.logIncident("RAZCompteurRCAnneePrecedenteJob", ex.getMessage(), 
					"Erreur a l appel du WS absWSConsumer.getListeCompteurAnneePrecedente()", ex);
		}
		
		logger.info("Found {} previous year RC counters to update...", listeIdCompteur.size());
		
		VoRedmineIncidentLogger incidentRedmine = new VoRedmineIncidentLogger(this.getClass().getSimpleName());
		
		for (Integer idCompteur : listeIdCompteur) {
			
			logger.debug("Processing counter id {}...", idCompteur);
			
			ReturnMessageDto result = null;
			try {
				result = absWSConsumer.resetCompteurAnneePrecedente(idCompteur);
			} catch (Exception ex) {
				logger.error("Une erreur technique est survenue lors du traitement : ", ex);
				// #28779 ne pas boucler sur le logger redmine
				incidentRedmine.addException(ex, idCompteur);
//				incidentLoggerService.logIncident("RAZCompteurRCAnneePrecedenteJob", ex.getMessage(), ex);
			}
			
			if (result != null && result.getErrors().size() != 0) {
				for (String err : result.getErrors()) {
					logger.info(err);
				}
			}
		}
		
		if(!incidentRedmine.getListException().isEmpty()) {
			incidentLoggerService.logIncident(incidentRedmine);
		}
		
		logger.info("Processed RAZCompteurRCAnneePrecedenteJob");
	}
}
