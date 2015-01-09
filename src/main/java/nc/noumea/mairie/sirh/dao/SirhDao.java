package nc.noumea.mairie.sirh.dao;

import java.util.List;

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
	public List<Integer> getReferentRHService(String codeService) {
		Session session = sirhSessionFactory.getCurrentSession();

		if (!session.getTransaction().isActive()) {
			session.beginTransaction();
			logger.debug("SirhDao : la session n'est pas active");
		}

		StringBuilder sb = new StringBuilder();
		sb.append("SELECT ref.ID_AGENT_REFERENT AS idReferent FROM P_REFERENT_RH ref ");
		sb.append("WHERE ref.SERVI = :codeService ");

		@SuppressWarnings("unchecked")
		List<Integer> result = session.createSQLQuery(sb.toString())
				.addScalar("idReferent", StandardBasicTypes.INTEGER).setParameter("codeService", codeService).list();

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
		sb.append("WHERE ref.SERVI is null ");

		@SuppressWarnings("unchecked")
		List<Integer> result = session.createSQLQuery(sb.toString()).addScalar("idReferent", StandardBasicTypes.INTEGER)
				.list();

		session.getTransaction().rollback();

		return result.size()==0 ? null : result.get(0);
	}

}
