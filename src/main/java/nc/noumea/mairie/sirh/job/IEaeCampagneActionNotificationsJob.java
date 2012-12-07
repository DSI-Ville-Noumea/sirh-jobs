package nc.noumea.mairie.sirh.job;

import java.util.Date;

import nc.noumea.mairie.sirh.eae.domain.EaeCampagneAction;

public interface IEaeCampagneActionNotificationsJob {

	public void sendNotificationsOneByOne() throws EaeCampagneActionNotificationsException;
	
	public void sendNotification(EaeCampagneAction eaeCampagneAction, Date theDate) throws EaeCampagneActionNotificationsException, Exception;
}
