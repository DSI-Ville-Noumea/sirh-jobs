package nc.noumea.mairie.sirh.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import nc.noumea.mairie.sirh.domain.DocumentAssocie;

import org.springframework.stereotype.Repository;

@Repository
public class SirhDocumentDao implements ISirhDocumentDao {

	@PersistenceContext(unitName = "sirhPersistenceUnit")
	private EntityManager sirhEntityManager;
	
	@Override
	public DocumentAssocie getDocumentAssocie(int id) {
		return sirhEntityManager.find(DocumentAssocie.class, id);
	}

}
