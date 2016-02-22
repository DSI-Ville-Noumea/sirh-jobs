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
public class RAZCompteurCongeAnnuelJob extends QuartzJobBean {

	private Logger logger = LoggerFactory.getLogger(RAZCompteurCongeAnnuelJob.class);

	@Autowired
	private IAbsWSConsumer absWSConsumer;

	@Autowired
	private IIncidentLoggerService incidentLoggerService;

	@Override
	protected void executeInternal(JobExecutionContext arg0) throws JobExecutionException {

		logger.info("Start RAZCompteurCongeAnnuelJob");

		List<Integer> listeIdCompteur = new ArrayList<Integer>();
		try {
			listeIdCompteur = absWSConsumer.getListCompteurCongeAnnuel();
		} catch (Exception ex) {
			logger.error("Une erreur technique est survenue lors du traitement : ", ex);
			incidentLoggerService.logIncident(
					"RAZCompteurCongeAnnuelJob", ex.getMessage(), 
					"Erreur a l appel de absWSConsumer.getListCompteurCongeAnnuel()", ex);
		}

		logger.info("Found {} Conge annuel counters to update...", listeIdCompteur.size());

		VoRedmineIncidentLogger incidentRedmine = new VoRedmineIncidentLogger(this.getClass().getSimpleName());
		for (Integer idCompteur : listeIdCompteur) {

			logger.debug("Processing counter id {}...", idCompteur);

			ReturnMessageDto result = null;
			try {
				result = absWSConsumer.resetCompteurCongeAnnuel(idCompteur);
			} catch (Exception ex) {
				logger.error("Une erreur technique est survenue lors du traitement : ", ex);
				// #28781 ne pas logger plein d incidents
				incidentRedmine.addException(ex.getClass().getName(), ex.getMessage(), ex, idCompteur);
				//incidentLoggerService.logIncident("RAZCompteurCongeAnnuelJob", ex.getMessage(), ex);
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

		logger.info("Processed RAZCompteurCongeAnnuelJob");
	}
}
