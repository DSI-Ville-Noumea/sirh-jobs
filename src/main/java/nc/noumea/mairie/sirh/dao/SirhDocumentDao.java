package nc.noumea.mairie.sirh.dao;

import nc.noumea.mairie.sirh.domain.DocumentAssocie;

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

}
