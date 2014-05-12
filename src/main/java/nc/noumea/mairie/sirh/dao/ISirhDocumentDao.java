package nc.noumea.mairie.sirh.dao;

import nc.noumea.mairie.sirh.domain.DocumentAssocie;
import nc.noumea.mairie.sirh.eae.dao.DaoException;
import nc.noumea.mairie.sirh.ws.dto.LightUser;

public interface ISirhDocumentDao {

	DocumentAssocie getDocumentAssocie(int id);

	void deleteSIIDMAEntries();

	void addSIISDMA(LightUser user) throws DaoException;
}
