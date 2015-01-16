package nc.noumea.mairie.sirh.ws;

import java.util.Date;
import java.util.List;

import nc.noumea.mairie.sirh.ws.dto.AgentDto;

public interface ISirhWSConsumer {

	List<AgentDto> getListeAgentEligibleEAESansAffectes();
	
	List<AgentDto> getListeAgentEligibleEAEAffectes();

	List<Integer> getListAgentPourAlimAutoCompteursCongesAnnuels(
			Date dateDebut, Date dateFin);
}
