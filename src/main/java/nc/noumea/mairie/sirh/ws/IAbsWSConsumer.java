package nc.noumea.mairie.sirh.ws;

import java.util.Date;
import java.util.List;

import nc.noumea.mairie.sirh.ws.dto.ActeursDto;
import nc.noumea.mairie.sirh.ws.dto.DemandeDto;
import nc.noumea.mairie.sirh.ws.dto.EmailInfoDto;

public interface IAbsWSConsumer {

	public EmailInfoDto getListIdDestinatairesEmailInfo();

	public ActeursDto getListIdActeursByAgent(String idAgent);

	List<Integer> getListeCompteurAnneePrecedente();

	ReturnMessageDto resetCompteurAnneePrecedente(Integer idAgentReposCompCount);

	List<Integer> getListeCompteurAnneeEnCours();

	ReturnMessageDto resetCompteurAnneeEnCours(Integer idAgentReposCompCount);

	List<Integer> getListCompteurCongeAnnuel();

	ReturnMessageDto resetCompteurCongeAnnuel(Integer idAgentCongeAnnuelCount);

	DemandeDto getDemandeAbsence(Integer idDemande);

	ReturnMessageDto alimentationAutoCongesAnnuels(Integer nomatr, Date dateDebut, Date dateFin);

	public ReturnMessageDto miseAJourSpSoldAgent(String idAgent);

	public ReturnMessageDto miseAJourSpSorcAgent(String idAgent);

	public List<DemandeDto> getListDemandeAbsenceRejetDRHVeille();
}
