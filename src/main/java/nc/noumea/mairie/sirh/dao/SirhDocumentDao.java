package nc.noumea.mairie.sirh.dao;

import nc.noumea.mairie.sirh.domain.DocumentAssocie;
import nc.noumea.mairie.sirh.domain.SIIDMA_SIRH;
import nc.noumea.mairie.sirh.eae.dao.DaoException;
import nc.noumea.mairie.sirh.ws.dto.LightUser;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class SirhDocumentDao implements ISirhDocumentDao {

	private Logger logger = LoggerFactory.getLogger(SirhDocumentDao.class);

	@Autowired
	@Qualifier("sirhSessionFactory")
	private SessionFactory sirhSessionFactory;

	@Override
	public DocumentAssocie getDocumentAssocie(int id) {

		sirhSessionFactory.getCurrentSession().beginTransaction();

		DocumentAssocie result = (DocumentAssocie) sirhSessionFactory.getCurrentSession()
				.get(DocumentAssocie.class, id);

		sirhSessionFactory.getCurrentSession().getTransaction().rollback();

		return result;
	}

	@Override
	public void deleteSIIDMAEntries() {
		logger.debug(" entrée deleteSIIDMAEntries with sirhSessionFactory " + sirhSessionFactory);

		Session session = sirhSessionFactory.getCurrentSession();

		if (!session.getTransaction().isActive()) {
			session.beginTransaction();
			logger.debug("deleteSIIDMAEntries : la session n'est pas active");
		}

		Query jobQuery = session.createQuery("delete from SIIDMA_SIRH");

		jobQuery.executeUpdate();

		session.getTransaction().commit();

	}

	@Override
	public void addSIISDMA(LightUser user) throws DaoException {
		sirhSessionFactory.getCurrentSession().beginTransaction();

		SIIDMA_SIRH siidma = new SIIDMA_SIRH();
		siidma.setIdAgent(Integer.valueOf(getIdAgent(user.getEmployeeNumber())));
		siidma.setLogin(user.getsAMAccountName());
		siidma.setMail(user.getMail());
		siidma.setNomatr(Integer.valueOf(getNomatr(user.getEmployeeNumber())));

		try {
			sirhSessionFactory.getCurrentSession().save(siidma);
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
