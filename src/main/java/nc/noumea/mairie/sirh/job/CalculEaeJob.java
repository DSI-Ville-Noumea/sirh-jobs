package nc.noumea.mairie.sirh.job;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nc.noumea.mairie.sirh.eae.dao.IEaeCampagneTaskDao;
import nc.noumea.mairie.sirh.eae.domain.EaeCampagneTask;
import nc.noumea.mairie.sirh.tools.IIncidentLoggerService;
import nc.noumea.mairie.sirh.ws.IEaeWSConsumer;
import nc.noumea.mairie.sirh.ws.ISirhWSConsumer;
import nc.noumea.mairie.sirh.ws.ReturnMessageDto;
import nc.noumea.mairie.sirh.ws.dto.AgentDto;

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
public class CalculEaeJob extends QuartzJobBean {

	private Logger logger = LoggerFactory.getLogger(CalculEaeJob.class);
	
	@Autowired
	private ISirhWSConsumer sirhWSConsumer;
	
	@Autowired
	private IEaeWSConsumer eaeWSConsumer;
	
	@Autowired
	private IEaeCampagneTaskDao eaeCampagneTaskDao;
	
	@Autowired
	private IIncidentLoggerService incidentLoggerService;
	
	final static private Integer numberOfTries = 2;
	
	@Override
	protected void executeInternal(JobExecutionContext arg0) throws JobExecutionException {
		
		eaeCampagneTaskDao.beginTransaction();
		
		ReturnMessageDto rmDto = null;
		String taskStatus = "";
		EaeCampagneTask eT = null;
		eT = eaeCampagneTaskDao.getNextEaeCampagneTask();
		
		logger.info("Start CalculEaeJob");
		
		if(null == eT)
			logger.info("CalculEaeTask no exist");
		
		if(null != eT) {
			
			///////////////////// TRAITEMENT DES EAEs SANS AFFECTES //////////////////////
			List<AgentDto> listeAgentEaeSansAffectes = new ArrayList<AgentDto>();
			try {
				listeAgentEaeSansAffectes = sirhWSConsumer.getListeAgentEligibleEAESansAffectes();
			} catch (Exception ex) {
				logger.error("Une erreur technique est survenue lors du traitement des EAEs SANS affectes : ", ex);
				taskStatus += " Erreur récupération listeAgentEaeSansAffectes : " + ex.getMessage() + " ;\n ";
		//		incidentLoggerService.logIncident("CalculEaeJob", ex.getMessage(), ex);
			}
			
			logger.info("Found {} agents to calculate EAE SANS affectes ...", listeAgentEaeSansAffectes.size());
			
			for (AgentDto agent : listeAgentEaeSansAffectes) {
				
				logger.debug("Processing counter id {}...", agent.getIdAgent());
				int nbErrors = 0;
				boolean succeeded = false;
				while(nbErrors < numberOfTries && !succeeded) {
					try {
						rmDto = eaeWSConsumer.creerEAESansAffecte(eT.getEaeCampagne().getIdEaeCampagne(), agent.getIdAgent());
						succeeded = true;
					} catch (Exception ex) {
						logger.error("Une erreur technique est survenue lors du traitement des EAEs SANS affectes : ", ex);
						if(null != rmDto.getErrors() && !rmDto.getErrors().isEmpty()) {
							taskStatus += " Erreur creerEAESansAffecte sur l'agent " + agent.getIdAgent() + " : " + rmDto.getErrors().get(0).toString() + " ;\n ";
						}else{
							taskStatus += " Erreur creerEAESansAffecte sur l'agent " + agent.getIdAgent() + " : " + ex.getMessage() + " ;\n ";
						}
			//			incidentLoggerService.logIncident("CalculEaeJob", ex.getMessage(), ex);
						nbErrors++;
					}
				}
			}
	
			/////////////////// TRAITEMENT DES EAEs AFFECTES //////////////////////
			List<AgentDto> listeAgentEaeAffectes = new ArrayList<AgentDto>();
			try {
				listeAgentEaeAffectes = sirhWSConsumer.getListeAgentEligibleEAEAffectes();
			} catch (Exception ex) {
				logger.error("Une erreur technique est survenue lors du traitement des EAEs affectes : ", ex);
				taskStatus += " Erreur récupération listeAgentEaeAffectes : " + ex.getMessage() + " ;\n ";
//				incidentLoggerService.logIncident("CalculEaeJob", ex.getMessage(), ex);
			}
			
			logger.info("Found {} agents to calculate EAE affectes ...", listeAgentEaeAffectes.size());
			
			for (AgentDto agent : listeAgentEaeAffectes) {
				
				logger.debug("Processing counter id {}...", agent.getIdAgent());
				int nbErrors = 0;
				boolean succeeded = false;
				while(nbErrors < numberOfTries && !succeeded) {
					try {
						rmDto = eaeWSConsumer.creerEaeAffecte(eT.getEaeCampagne().getIdEaeCampagne(), agent.getIdAgent());
						succeeded = true;
					} catch (Exception ex) {
						logger.error("Une erreur technique est survenue lors du traitement des EAEs affectes : ", ex);
						if(null != rmDto.getErrors() && !rmDto.getErrors().isEmpty()) {
							taskStatus += " Erreur creerEaeAffecte sur l'agent " + agent.getIdAgent() + " : " + rmDto.getErrors().get(0).toString() + " ;\n ";
						}else{
							taskStatus += " Erreur creerEaeAffecte sur l'agent " + agent.getIdAgent() + " : " + ex.getMessage() + " ;\n ";
						}
	//					incidentLoggerService.logIncident("CalculEaeJob", ex.getMessage(), ex);
						nbErrors++;
					}
				}
			}
			
			if("".equals(taskStatus)) {
				eT.setTaskStatus("OK");
			}else{
				if(4000 < taskStatus.length()) {
					taskStatus = taskStatus.substring(0, 3995) + "...";
				}
				eT.setTaskStatus(taskStatus);
			}
			
			eT.setDateCalculEae(new Date());
			
			logger.info("Processed CalculEaeJob");
		}
		
		eaeCampagneTaskDao.commitTransaction();
	}
}
