package nc.noumea.mairie.sirh.dao;

import java.util.List;

import nc.noumea.mairie.sirh.domain.ActionFDPJob;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.type.StandardBasicTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class SirhDao implements ISirhDao {

	private Logger logger = LoggerFactory.getLogger(SirhDao.class);

	@Autowired
	@Qualifier("sirhSessionFactory")
	private SessionFactory sirhSessionFactory;

	@Override
	public List<Integer> getReferentRHService(Integer idServiceADS) {
		Session session = sirhSessionFactory.getCurrentSession();

		if (!session.getTransaction().isActive()) {
			session.beginTransaction();
			logger.debug("SirhDao : la session n'est pas active");
		}

		StringBuilder sb = new StringBuilder();
		sb.append("SELECT ref.ID_AGENT_REFERENT AS idReferent FROM P_REFERENT_RH ref ");
		sb.append("WHERE ref.ID_SERVICE_ADS = :idServiceADS ");

		@SuppressWarnings("unchecked")
		List<Integer> result = session.createSQLQuery(sb.toString())
				.addScalar("idReferent", StandardBasicTypes.INTEGER).setParameter("idServiceADS", idServiceADS).list();

		session.getTransaction().rollback();
		return result;
	}

	@Override
	public Integer getReferentRHGlobal() {
		Session session = sirhSessionFactory.getCurrentSession();

		if (!session.getTransaction().isActive()) {
			session.beginTransaction();
			logger.debug("SirhDao : la session n'est pas active");
		}

		StringBuilder sb = new StringBuilder();
		sb.append("SELECT ref.ID_AGENT_REFERENT AS idReferent FROM P_REFERENT_RH ref ");
		sb.append("WHERE ref.ID_SERVICE_ADS is null ");

		@SuppressWarnings("unchecked")
		List<Integer> result = session.createSQLQuery(sb.toString())
				.addScalar("idReferent", StandardBasicTypes.INTEGER).list();

		session.getTransaction().rollback();

		return result.size() == 0 ? null : result.get(0);
	}

	public void beginTransaction() {
		sirhSessionFactory.getCurrentSession().beginTransaction();
	}

	public void commitTransaction() {
		sirhSessionFactory.getCurrentSession().getTransaction().commit();
	}

	public void rollBackTransaction() {
		sirhSessionFactory.getCurrentSession().getTransaction().rollback();
	}

	@Override
	public ActionFDPJob getNextActionFDPTask() {

		@SuppressWarnings("unchecked")
		List<ActionFDPJob> result = sirhSessionFactory.getCurrentSession()
				.getNamedQuery("ActionFDPJob.getNextActionFDPTask").setMaxResults(1).list();

		if (result.size() == 0)
			return null;

		return result.get(0);
	}
}
