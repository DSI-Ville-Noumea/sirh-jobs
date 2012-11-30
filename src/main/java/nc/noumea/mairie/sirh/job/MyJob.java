package nc.noumea.mairie.sirh.job;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

public class MyJob extends QuartzJobBean {

	private Logger logger = LoggerFactory.getLogger(MyJob.class);
	
	private SampleClass injectedString;
	
	public MyJob() {
		logger.info("ctor");
	}
	
	@Override
	protected void executeInternal(JobExecutionContext arg0)
			throws JobExecutionException {
		
		try {
			ApplicationContext c = (ApplicationContext) arg0.getScheduler().getContext().get("applicationContext");
			injectedString = c.getBean(SampleClass.class);
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		logger.info("injected value is : [" + injectedString.getTheValue() + "] - executed job !");
		
	}

}
