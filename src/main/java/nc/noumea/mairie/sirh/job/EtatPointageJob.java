package nc.noumea.mairie.sirh.job;

import nc.noumea.mairie.sirh.service.IPointageService;
import nc.noumea.mairie.sirh.tools.IIncidentLoggerService;

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
public class EtatPointageJob extends QuartzJobBean {

	private Logger logger = LoggerFactory.getLogger(EtatPointageJob.class);
	
	@Autowired
	private IPointageService service;

	@Autowired
	private IIncidentLoggerService incidentLoggerService;
	
	@Override
	public void executeInternal(JobExecutionContext arg0)
			throws JobExecutionException {
		
		logger.info("Start EtatPointageJob");
		
		try {
			service.majEtatPointagesRefusesEtRejetesPlus3Mois();
		} catch(Exception ex) {
			// #28785
			logger.error("Une erreur technique est survenue lors du traitement : ", ex);
			incidentLoggerService.logIncident("EtatPointageJob", ex.getMessage(), 
					null, ex);
		}
		
		logger.info("Processed EtatPointageJob");
	}
}
