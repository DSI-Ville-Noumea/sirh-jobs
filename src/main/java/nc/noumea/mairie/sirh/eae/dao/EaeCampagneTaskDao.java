package nc.noumea.mairie.sirh.eae.dao;

import java.util.List;

import nc.noumea.mairie.sirh.eae.domain.EaeCampagneTask;

import org.hibernate.LockMode;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class EaeCampagneTaskDao implements IEaeCampagneTaskDao {

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
	public EaeCampagneTask getNextEaeCampagneTask() {
		
		@SuppressWarnings("unchecked")
		List<EaeCampagneTask> result = eaeSessionFactory.getCurrentSession().getNamedQuery("EaeCampagneTask.getNextEaeCampagneTask")
				.setLockMode("e", LockMode.PESSIMISTIC_WRITE).setMaxResults(1).list();
		
		if (result.size() == 0)
			return null;
		
		return result.get(0);
	}
}
