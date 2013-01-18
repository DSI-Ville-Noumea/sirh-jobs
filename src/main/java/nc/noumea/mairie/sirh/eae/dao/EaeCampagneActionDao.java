package nc.noumea.mairie.sirh.eae.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import nc.noumea.mairie.sirh.eae.domain.EaeCampagneAction;

//@Repository
public class EaeCampagneActionDao implements IEaeCampagneActionDao {

	@PersistenceContext(unitName = "eaePersistenceUnit")
	private EntityManager eaeEntityManager;
	
	@Override
	public long countEaeCampagneActionToSend(Date asOfDate) {
		TypedQuery<Long> eaeQuery = eaeEntityManager.createNamedQuery("EaeCampagneAction.countTodayNotifications", Long.class);
		eaeQuery.setParameter("todayDate", asOfDate);
		Long result = eaeQuery.getSingleResult();
		
		return result;
	}
	
	@Override
	public List<EaeCampagneAction> getEaeCampagneActionToSend(Date asOfDate) {
		TypedQuery<EaeCampagneAction> eaeQuery = eaeEntityManager.createNamedQuery("EaeCampagneAction.getTodayNotifications", EaeCampagneAction.class);
		eaeQuery.setParameter("todayDate", asOfDate);
		List<EaeCampagneAction> result = eaeQuery.getResultList();
		
		return result;
	}
	
	@Override
	public EaeCampagneAction getNextEaeCampagneActionToSend(Date asOfDate) {

		TypedQuery<EaeCampagneAction> eaeQuery = eaeEntityManager.createNamedQuery("EaeCampagneAction.getNextTodayNotification", EaeCampagneAction.class);
		eaeQuery.setParameter("todayDate", asOfDate);
		eaeQuery.setMaxResults(1);
		List<EaeCampagneAction> result = eaeQuery.getResultList();
		
		return result.size() > 0 ? result.get(0) : null;
	}
	
	@Override
	public int setDateMailEnvoye(final EaeCampagneAction eaeCampagneAction, final Date dateMailEnvoye) throws DaoException {
		
		
		Query q = eaeEntityManager.createNativeQuery("UPDATE EAE_CAMPAGNE_ACTION SET DATE_MAIL_ENVOYE = :date WHERE ID_CAMPAGNE_ACTION = :id");
		q.setParameter("date", dateMailEnvoye);
		q.setParameter("id", eaeCampagneAction.getIdCampagneAction());
		
		int result = 0;
		try {
			result = q.executeUpdate();
		}
		catch (Exception ex) {
			throw new DaoException("An error occured while updating the EaeCampagneAction: ", ex);
		}
		
		return result;
	}
}
