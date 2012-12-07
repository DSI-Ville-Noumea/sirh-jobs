package nc.noumea.mairie.sirh.job;

import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class SirhSpringJobFactory implements JobFactory {

	private Logger logger = LoggerFactory.getLogger(SirhSpringJobFactory.class);
	
	@Autowired
	private ApplicationContext applicationContext;
	
	@Override
	public Job newJob(TriggerFiredBundle bundle, Scheduler scheduler) throws SchedulerException {
		JobDetail jobDetail = bundle.getJobDetail();
		logger.debug("Trying to resolve bean [{}] in current application context...", jobDetail.getJobClass());
		return (Job) applicationContext.getBean(jobDetail.getJobClass());
	}
}
