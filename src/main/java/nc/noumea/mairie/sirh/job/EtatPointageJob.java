package nc.noumea.mairie.sirh.job;

import nc.noumea.mairie.sirh.service.IPointageService;

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
	
	@Override
	public void executeInternal(JobExecutionContext arg0)
			throws JobExecutionException {
		
		logger.info("Start EtatPointageJob");
		
		service.majEtatPointagesRefusesEtRejetesPlus3Mois();
		
		logger.info("Processed EtatPointageJob");
	}
}
