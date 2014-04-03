package nc.noumea.mairie.sirh.eae.dao;

import nc.noumea.mairie.sirh.eae.domain.EaeCampagneTask;

public interface IEaeCampagneTaskDao {

	void beginTransaction();

	void commitTransaction();

	void rollBackTransaction();
	
	EaeCampagneTask getNextEaeCampagneTask();
}
