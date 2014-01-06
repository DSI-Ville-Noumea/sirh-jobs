package nc.noumea.mairie.sirh.job;

import nc.noumea.mairie.sirh.service.IAbsenceService;

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
public class AbsenceSupprimerJob extends QuartzJobBean {

	private Logger logger = LoggerFactory.getLogger(AbsenceSupprimerJob.class);
	
	@Autowired
	private IAbsenceService service;
	
	@Override
	public void executeInternal(JobExecutionContext arg0)
			throws JobExecutionException {
		
		logger.info("Start AbsenceSupprimerJob");
		
		service.supprimerAbsencesProvisoires();
		
		logger.info("Processed AbsenceSupprimerJob");
	}
}
