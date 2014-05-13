package nc.noumea.mairie.sirh.ws;

import java.util.List;

import nc.noumea.mairie.sirh.eae.dao.DaoException;
import nc.noumea.mairie.sirh.ws.dto.LightUser;

public interface IRadiWSConsumer {

	List<LightUser> getListeAgentMairie();

	LightUser retrieveAgentFromLdapFromMatricule(String employeeNumber) throws DaoException;
}
