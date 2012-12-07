package nc.noumea.mairie.sirh.job;

import java.util.Date;

import nc.noumea.mairie.sirh.eae.dao.DaoException;
import nc.noumea.mairie.sirh.eae.dao.IEaeCampagneActionDao;
import nc.noumea.mairie.sirh.eae.domain.EaeCampagneAction;
import nc.noumea.mairie.sirh.tools.Helper;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EaeCampagneActionNotificationsJob extends QuartzJobBean implements IEaeCampagneActionNotificationsJob{

	private Logger logger = LoggerFactory.getLogger(EaeCampagneActionNotificationsJob.class);
	
	@Autowired
	private Helper helper;
	
	@Autowired
	private IEaeCampagneActionDao eaeCampagneActionDao;
	
	public EaeCampagneActionNotificationsJob() {
	}
	
	@Override
	protected void executeInternal(JobExecutionContext jobContext) throws JobExecutionException {

		// Run the job
		try {
			sendNotificationsOneByOne();
		} catch (EaeCampagneActionNotificationsException e) {
			throw new JobExecutionException(e);
		}
	}

	public void sendNotificationsOneByOne() throws EaeCampagneActionNotificationsException {
		
		Date today = helper.getCurrentDate();
		EaeCampagneAction eA = null;
		int i = 0;
		int nbErrors = 0;
		int nbNotifications = eaeCampagneActionDao.countEaeCampagneActionToSend(today);
		
		logger.info("There are {} EaeCampagneAction notifications to send...", nbNotifications);
		
		if (nbNotifications == 0)
			return;
		
		while(nbErrors < 3 && (eA = eaeCampagneActionDao.getNextEaeCampagneActionToSend(today)) != null) {
			
			logger.debug("action #{}: {}", ++i, eA.getNomAction());
			
			try {
				sendNotification(eA, today);
			} catch(Exception ex) {
				logger.error("An error occured while trying to send notification for EaeCampagneAction id {} with action name '{}' for EaeCampagne id {} of year {}.",
						new Object[] {eA.getIdCampagneAction(), eA.getNomAction(), eA.getEaeCampagne().getIdEaeCampagne(), eA.getEaeCampagne().getAnnee()});
				logger.error("Here follows the exception : ", ex);
				nbErrors++;
			}
			
		}
		
		if (nbErrors >= 3) {
			logger.error("Stopped sending notifications because exceeded the maximum authorized number of tries.");
			throw new EaeCampagneActionNotificationsException("Stopped sending notifications because exceeded the maximum authorized number of tries.");
		}
		else
			logger.info("Finished sending today's notifications...");
	}
	
	@Transactional(value = "eaeTransactionManager")
	public void sendNotification(EaeCampagneAction eaeCampagneAction, Date theDate) throws EaeCampagneActionNotificationsException, DaoException {
		
		logger.debug("Sending email for action : {} due on {}", eaeCampagneAction.getNomAction(), eaeCampagneAction.getDateAfaire());

//		if (eaeCampagneAction.getIdCampagneAction() == 23)
//			throw new EaeCampagneActionNotificationsException("the cause of the exception !!!");
		
		//TODO: Switch to using JPA entitymanager instead of manually writing the update query using plain SQL...
		eaeCampagneActionDao.setDateMailEnvoye(eaeCampagneAction, theDate);
	}
}
