package nc.noumea.mairie.sirh.job;

import java.util.List;

import nc.noumea.mairie.sirh.eae.domain.EaeCampagneAction;
import nc.noumea.mairie.sirh.eae.service.IEaeCampagneActionService;
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
	private IEaeCampagneActionService eaeCampagneActionService;
	
	@Autowired
	private Helper helper;
		
	public EaeCampagneActionNotificationsJob() {
	}
	
	@Override
	protected void executeInternal(JobExecutionContext jobContext) throws JobExecutionException {

		// Run the job
		SendNotifications();
	}

	protected void SendNotifications() throws JobExecutionException {
		
		List<EaeCampagneAction> notificationsToSend = eaeCampagneActionService.getEaeCampagneActionToSend();
		logger.info("There are {} EaeCampagneAction notifications to send...", notificationsToSend.size());
		
		int i = 0;
		for (final EaeCampagneAction eA : eaeCampagneActionService.getEaeCampagneActionToSend()) {
			logger.info("action #{}: {}", ++i, eA.getNomAction());
			SendNotification(eA);
		}
	}
	
	@Transactional
	public void SendNotification(EaeCampagneAction eaeCampagneAction)  {
		
		logger.info("Sending email for action : {} due on {}", eaeCampagneAction.getNomAction(), eaeCampagneAction.getDateAfaire());
		
		eaeCampagneAction.setDateMailEnvoye(helper.getCurrentDate());
	}
}
