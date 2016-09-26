package nc.noumea.mairie.sirh.eae.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import nc.noumea.mairie.sirh.eae.domain.EaeCampagneAction;

@Repository
public class EaeCampagneActionDao implements IEaeCampagneActionDao {

	@Autowired
	@Qualifier("eaeSessionFactory")
	private SessionFactory eaeSessionFactory;

	public void beginTransaction() {
		eaeSessionFactory.getCurrentSession().beginTransaction();
	}

	public void commitTransaction() {
		eaeSessionFactory.getCurrentSession().getTransaction().commit();
	}

	public void rollBackTransaction() {
		eaeSessionFactory.getCurrentSession().getTransaction().rollback();
	}

	@Override
	public List<EaeCampagneAction> getEaeCampagneActionToSend(Date asOfDate) {

		@SuppressWarnings("unchecked")
		List<EaeCampagneAction> l = eaeSessionFactory.getCurrentSession().getNamedQuery("EaeCampagneAction.getTodayNotifications")
				.setParameter("todayDate", asOfDate).list();

		return l;
	}

	@Override
	public EaeCampagneAction getNextEaeCampagneActionToSend(Date asOfDate) {

		@SuppressWarnings("unchecked")
		List<EaeCampagneAction> result = eaeSessionFactory.getCurrentSession().getNamedQuery("EaeCampagneAction.getNextTodayNotification")
				.setParameter("todayDate", asOfDate).setMaxResults(1).list();

		return result.size() > 0 ? result.get(0) : null;
	}

	@Override
	public int setDateMailEnvoye(final EaeCampagneAction eaeCampagneAction, final Date dateMailEnvoye) throws DaoException {
		Query q = eaeSessionFactory.getCurrentSession()
				.createQuery("UPDATE EAE_CAMPAGNE_ACTION SET DATE_MAIL_ENVOYE = :date WHERE ID_CAMPAGNE_ACTION = :id")
				.setParameter("date", dateMailEnvoye).setParameter("id", eaeCampagneAction.getIdCampagneAction());

		int result = 0;
		try {
			result = q.executeUpdate();
		} catch (Exception ex) {
			throw new DaoException("An error occured while updating the EaeCampagneAction: ", ex);
		}

		return result;

	}
}
