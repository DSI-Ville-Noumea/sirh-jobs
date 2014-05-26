package nc.noumea.mairie.sirh.dao;

import java.util.List;

import nc.noumea.mairie.sirh.domain.AvctCapPrintJob;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class AvctCapPrintJobDao implements IAvctCapPrintJobDao {

	private Logger logger = LoggerFactory.getLogger(AvctCapPrintJobDao.class);

	@Autowired
	@Qualifier("sirhSessionFactory")
	private SessionFactory sirhSessionFactory;

	@SuppressWarnings("rawtypes")
	@Override
	public AvctCapPrintJob getNextPrintJob() {

		logger.debug("Entrée fonction getNextPrintJob de AvctCapPrintJobDao");

		Session session = sirhSessionFactory.getCurrentSession();

		if (!session.getTransaction().isActive()) {
			session.beginTransaction();
			logger.debug("AvctCapPrintJobDao : la session n'est pas active");
		}

		Query jobQuery = session
				.createQuery("select job from AvctCapPrintJob job where job.jobId is null order by job.submissionDate asc");
		jobQuery.setMaxResults(1);

		List result = jobQuery.list();
		logger.debug("AvctCapPrintJobDao : taille de la liste result = " + result.size());

		if (result.size() != 1)
			return null;

		AvctCapPrintJob pj = (AvctCapPrintJob) result.get(0);

		session.getTransaction().rollback();

		logger.debug("Sortie fonction getNextPrintJob de AvctCapPrintJobDao");

		return pj;
	}

	@Override
	public void updateAvctCapPrintJob(AvctCapPrintJob job) {

		Session session = sirhSessionFactory.getCurrentSession();
		session.beginTransaction();
		session.merge(job);
		session.getTransaction().commit();
	}
}
