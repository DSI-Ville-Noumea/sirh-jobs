package nc.noumea.mairie.sirh.dao;

import java.util.List;

import nc.noumea.mairie.sirh.domain.ActionFDPJob;

public interface ISirhDao {

	List<Integer> getReferentRHService(Integer idServiceADS);

	Integer getReferentRHGlobal();

	void beginTransaction();

	void commitTransaction();

	void rollBackTransaction();

	ActionFDPJob getNextSuppressionFDPTask();

	ActionFDPJob getNextDuplicationFDPTask();
}
