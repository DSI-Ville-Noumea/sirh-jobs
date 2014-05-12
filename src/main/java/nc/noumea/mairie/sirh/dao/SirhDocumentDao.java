package nc.noumea.mairie.sirh.dao;

import nc.noumea.mairie.sirh.domain.DocumentAssocie;
import nc.noumea.mairie.sirh.eae.dao.DaoException;
import nc.noumea.mairie.sirh.ws.dto.LightUser;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

//@Repository
public class SirhDocumentDao implements ISirhDocumentDao {

	@Autowired
	@Qualifier("sirhSessionFactory")
	private SessionFactory sirhSessionFactory;

	@Override
	public DocumentAssocie getDocumentAssocie(int id) {
		return (DocumentAssocie) sirhSessionFactory.getCurrentSession().get(DocumentAssocie.class, id);
	}

	@Override
	public void deleteSIIDMAEntries() {

		if (!sirhSessionFactory.getCurrentSession().getTransaction().isActive())
			sirhSessionFactory.getCurrentSession().beginTransaction();

		Query jobQuery = sirhSessionFactory.getCurrentSession().createQuery("delete from SIIDMA_SIRH");

		jobQuery.executeUpdate();

		sirhSessionFactory.getCurrentSession().getTransaction().commit();

	}

	@Override
	public void addSIISDMA(LightUser user) throws DaoException {

		if (!sirhSessionFactory.getCurrentSession().getTransaction().isActive())
			sirhSessionFactory.getCurrentSession().beginTransaction();

		Query q = sirhSessionFactory.getCurrentSession().createQuery(
				"INSERT INTO SIIDMA_SIRH(ID_AGENT,LOGIN,MAIL,NOMATR) values ( :idAgent , :login , :mail , :nomatr )");
		q.setParameter("idAgent", getIdAgent(user.getEmployeeNumber()));
		q.setParameter("login", user.getsAMAccountName());
		q.setParameter("mail", user.getMail());
		q.setParameter("nomatr", getNomatr(user.getEmployeeNumber()));

		try {
			q.executeUpdate();
			sirhSessionFactory.getCurrentSession().getTransaction().commit();
		} catch (Exception ex) {
			throw new DaoException("An error occured while inserting SIIDMA: ", ex);
		}

	}

	private String getNomatr(int employeeNumber) {
		return String.valueOf(employeeNumber).substring(2, String.valueOf(employeeNumber).length());
	}

	private String getIdAgent(int employeeNumber) {
		return "900" + getNomatr(employeeNumber);
	}

}
