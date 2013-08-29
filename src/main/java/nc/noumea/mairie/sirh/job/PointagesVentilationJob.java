package nc.noumea.mairie.sirh.job;

import java.util.Date;

import nc.noumea.mairie.ptg.dao.IPointagesDao;
import nc.noumea.mairie.ptg.domain.VentilTask;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Service;

@Service
public class PointagesVentilationJob extends QuartzJobBean implements IPointagesVentilationJob {

	private Logger logger = LoggerFactory.getLogger(PointagesVentilationJob.class);
	
	@Autowired
	private IPointagesDao pointagesDao;
	
	@Override
	protected void executeInternal(JobExecutionContext arg0)
			throws JobExecutionException {
		
		pointagesDao.beginTransaction();
		VentilTask vT = pointagesDao.getNextVentilTask();
		
		if (vT == null) {
			logger.info("Did not find any VentilTask to process... exiting job.");
			pointagesDao.rollBackTransaction();
			return;
		}
		
		logger.debug("has been called for task id {} !", vT.getIdVentilTask());
		vT.setTaskStatus("DONE !");
		vT.setDateVentilation(new Date());
		pointagesDao.commitTransaction();
	}

}
