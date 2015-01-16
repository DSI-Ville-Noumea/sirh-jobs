package nc.noumea.mairie.sirh.job;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nc.noumea.mairie.abs.dao.IAbsencesDao;
import nc.noumea.mairie.abs.domain.CongeAnnuelAlimAutoHisto;
import nc.noumea.mairie.sirh.tools.Helper;
import nc.noumea.mairie.sirh.tools.IIncidentLoggerService;
import nc.noumea.mairie.sirh.ws.IAbsWSConsumer;
import nc.noumea.mairie.sirh.ws.ISirhWSConsumer;
import nc.noumea.mairie.sirh.ws.ReturnMessageDto;

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
public class AbsCAAlimentationAutoCompteursJob extends QuartzJobBean {
	
	private Logger logger = LoggerFactory.getLogger(AbsCAAlimentationAutoCompteursJob.class);
	
	@Autowired
	@Qualifier("SIRH_ABS_WS_Base_URL")
	private String SIRH_ABS_WS_Base_URL;
	
	@Autowired
	private IAbsencesDao absencesDao;
	
	@Autowired
	private IAbsWSConsumer absWSConsumer;
	
	@Autowired
	private ISirhWSConsumer sirhWSConsumer;
	
	@Autowired
	private IIncidentLoggerService incidentLoggerService;
	
	@Autowired
	private Helper helper;
	
	@Override
	public void executeInternal(JobExecutionContext arg0) throws JobExecutionException {

		logger.info("Start AbsCAAlimentationAutoCompteursJob");
		
		List<Integer> listAgents = new ArrayList<Integer>();
		try {
			listAgents = sirhWSConsumer.getListAgentPourAlimAutoCompteursCongesAnnuels(
					helper.getFirstDayOfCurrentMonth(), helper.getLastDayOfCurrentMonth());
		} catch (Exception ex) {
			logger.error("Une erreur technique est survenue lors du traitement : ", ex);
			incidentLoggerService.logIncident("AbsCAAlimentationAutoCompteursJob", ex.getMessage(), ex);
		}
		
		logger.info("Found {} agents counters to update...", listAgents.size());
		
		boolean isError = false;
		for(Integer idAgent : listAgents) {
			logger.debug("Processing agent counters idAgent {}...", idAgent);
			
			ReturnMessageDto result = null;
			try {
				result = absWSConsumer.alimentationAutoCongesAnnuels(
						idAgent, helper.getFirstDayOfCurrentMonth(), helper.getLastDayOfCurrentMonth());
			} catch (Exception ex) {
				logger.error("Une erreur technique est survenue lors du traitement : ", ex);
				incidentLoggerService.logIncident("AbsCAAlimentationAutoCompteursJob", ex.getMessage(), ex);
			}
			
			if (result != null && result.getErrors().size() != 0) {
				isError = true;
				String error = "";
				for (String err : result.getErrors()) {
					logger.info(err);
					error += " ; " + err;
				}
				
				CongeAnnuelAlimAutoHisto histo = new CongeAnnuelAlimAutoHisto();
				histo.setDateMonth(helper.getFirstDayOfCurrentMonth());
				histo.setDateModification(new Date());
				histo.setIdAgent(idAgent);
				histo.setStatus(error);
				absencesDao.persistEntity(histo);
			}
		}
		
		if(isError) {
			incidentLoggerService.logIncident("AbsCAAlimentationAutoCompteursJob", "Erreur de AbsCAAlimentationAutoCompteursJob : voir ABS_CA_ALIM_AUTO_HISTO", null);
		}
		
		logger.info("Processed AbsCAAlimentationAutoCompteursJob");
	}
}
