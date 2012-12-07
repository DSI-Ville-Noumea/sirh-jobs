package nc.noumea.mairie.sirh.eae.dao;

import java.util.Date;

import nc.noumea.mairie.sirh.eae.domain.EaeCampagneAction;

public interface IEaeCampagneActionDao {

	public int countEaeCampagneActionToSend(Date asOfDate);
	public EaeCampagneAction getNextEaeCampagneActionToSend(Date asOfDate);
	public int setDateMailEnvoye(EaeCampagneAction eaeCampagneAction, Date dateMailEnvoye) throws DaoException;
	
}
