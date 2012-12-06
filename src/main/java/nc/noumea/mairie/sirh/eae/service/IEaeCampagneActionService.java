package nc.noumea.mairie.sirh.eae.service;

import java.util.List;

import nc.noumea.mairie.sirh.eae.domain.EaeCampagneAction;

public interface IEaeCampagneActionService {

	List<EaeCampagneAction> getEaeCampagneActionToSend();
}
