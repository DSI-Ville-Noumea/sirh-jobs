package nc.noumea.mairie.sirh.dao;

import java.util.List;

import nc.noumea.mairie.sirh.domain.AvctCapPrintJob;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class AvctCapPrintJobDao implements IAvctCapPrintJobDao {

	@Autowired
	@Qualifier("sirhSessionFactory")
	private SessionFactory sirhSessionFactory;
	
	@SuppressWarnings("rawtypes")
	@Override
	public AvctCapPrintJob getNextPrintJob() {

		sirhSessionFactory.getCurrentSession().beginTransaction();
		
		Query jobQuery = sirhSessionFactory.getCurrentSession()
				.createQuery("select job from AvctCapPrintJob job where job.jobId is null order by job.submissionDate asc");
		jobQuery.setMaxResults(1);
		
		List result = jobQuery.list();
		
		if (result.size() != 1)
			return null;
		
		AvctCapPrintJob pj = (AvctCapPrintJob) result.get(0);

		sirhSessionFactory.getCurrentSession().getTransaction().rollback();
		
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
