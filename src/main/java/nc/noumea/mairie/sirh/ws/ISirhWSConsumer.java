package nc.noumea.mairie.sirh.ws;

import java.util.Date;
import java.util.List;

import nc.noumea.mairie.sirh.ws.dto.AgentDto;

public interface ISirhWSConsumer {

	List<AgentDto> getListeAgentEligibleEAESansAffectes();

	List<AgentDto> getListeAgentEligibleEAEAffectes();

	List<Integer> getListAgentPourAlimAutoCompteursCongesAnnuels(Date dateDebut, Date dateFin);

	ReturnMessageDto isPaieEnCours();

	ReturnMessageDto deleteFDP(Integer idFichePoste, Integer idAgent);

	ReturnMessageDto dupliqueFDP(Integer idFichePoste, Integer idNewServiceAds, Integer idAgent);

	ReturnMessageDto activeFDP(Integer idFichePoste, Integer idAgent);

	byte[] getBordereauRecap() throws Exception;
}
