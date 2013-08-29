package nc.noumea.mairie.sirh.job;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Service;

@Service
public class PointagesVentilationJob extends QuartzJobBean implements IPointagesVentilationJob {

	private Logger logger = LoggerFactory.getLogger(PointagesVentilationJob.class);
	
	@Override
	protected void executeInternal(JobExecutionContext arg0)
			throws JobExecutionException {
		// TODO Auto-generated method stub
		logger.debug("has been called !!!");
	}

}
