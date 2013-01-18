package nc.noumea.mairie.sirh.eae.dao;

import java.util.Date;
import java.util.List;

import nc.noumea.mairie.sirh.eae.domain.EaeCampagneAction;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class HibernateEaeCampagneActionDao implements IEaeCampagneActionDao {

	@Autowired
	@Qualifier("eaeSessionFactory")
	private SessionFactory eaeSessionFactory;

	@Override
	public long countEaeCampagneActionToSend(Date asOfDate) {

		Query q = eaeSessionFactory.getCurrentSession().createQuery("SELECT count(eA.idCampagneAction) from EaeCampagneAction eA WHERE (eA.dateTransmission <= :todayDate AND eA.dateMailEnvoye is NULL)");
		q.setReadOnly(true);
		int r = (int) q.uniqueResult();
		
		return r;
	}

	@Override
	public EaeCampagneAction getNextEaeCampagneActionToSend(Date asOfDate) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<EaeCampagneAction> getEaeCampagneActionToSend(Date asOfDate) {
		
		eaeSessionFactory.getCurrentSession().beginTransaction();
		
		List l = eaeSessionFactory.getCurrentSession().getNamedQuery("EaeCampagneAction.getTodayNotifications")
				.setParameter("todayDate", asOfDate)
				.list();
		
		eaeSessionFactory.getCurrentSession().getTransaction().rollback();
		
		return l;
	}

	@Override
	public int setDateMailEnvoye(EaeCampagneAction eaeCampagneAction,
			Date dateMailEnvoye) throws DaoException {
		
		eaeSessionFactory.getCurrentSession().beginTransaction();
		
		Query q = eaeSessionFactory.getCurrentSession().createSQLQuery("UPDATE EAE_CAMPAGNE_ACTION SET DATE_MAIL_ENVOYE = :date WHERE ID_CAMPAGNE_ACTION = :id");
		q.setParameter("date", dateMailEnvoye);
		q.setParameter("id", eaeCampagneAction.getIdCampagneAction());
		
		int result = 0;
		try {
			result = q.executeUpdate();
		}
		catch (Exception ex) {
			throw new DaoException("An error occured while updating the EaeCampagneAction: ", ex);
		}
		
		eaeSessionFactory.getCurrentSession().getTransaction().commit();

		return result;
	}

}
