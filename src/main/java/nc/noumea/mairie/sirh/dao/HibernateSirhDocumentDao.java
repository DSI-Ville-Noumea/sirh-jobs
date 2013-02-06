package nc.noumea.mairie.sirh.dao;

import nc.noumea.mairie.sirh.domain.DocumentAssocie;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class HibernateSirhDocumentDao implements ISirhDocumentDao {

	@Autowired
	@Qualifier("sirhSessionFactory")
	private SessionFactory sirhSessionFactory;
	
	@Override
	public DocumentAssocie getDocumentAssocie(int id) {

		sirhSessionFactory.getCurrentSession().beginTransaction();
		
		DocumentAssocie result = (DocumentAssocie) sirhSessionFactory.getCurrentSession().get(DocumentAssocie.class, id);
		
		sirhSessionFactory.getCurrentSession().getTransaction().rollback();
		
		return result;
	}

}
