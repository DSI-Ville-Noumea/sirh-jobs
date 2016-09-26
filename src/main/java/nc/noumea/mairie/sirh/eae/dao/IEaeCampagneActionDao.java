package nc.noumea.mairie.sirh.eae.dao;

import java.util.Date;
import java.util.List;

import nc.noumea.mairie.sirh.eae.domain.EaeCampagneAction;

public interface IEaeCampagneActionDao {

	void beginTransaction();

	void commitTransaction();

	void rollBackTransaction();

	public EaeCampagneAction getNextEaeCampagneActionToSend(Date asOfDate);

	public List<EaeCampagneAction> getEaeCampagneActionToSend(Date asOfDate);

	public int setDateMailEnvoye(EaeCampagneAction eaeCampagneAction, Date dateMailEnvoye) throws DaoException;

}
