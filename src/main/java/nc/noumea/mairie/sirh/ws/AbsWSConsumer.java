package nc.noumea.mairie.sirh.ws;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.sun.jersey.api.client.ClientResponse;

import nc.noumea.mairie.sirh.job.AbsencePriseJob;
import nc.noumea.mairie.sirh.ws.dto.ActeursDto;
import nc.noumea.mairie.sirh.ws.dto.DemandeDto;
import nc.noumea.mairie.sirh.ws.dto.EmailInfoDto;

@Service
public class AbsWSConsumer extends BaseWsConsumer implements IAbsWSConsumer {

	@Autowired
	@Qualifier("SIRH_ABS_WS_Base_URL")
	private String				SIRH_ABS_WS_Base_URL;

	private Logger logger = LoggerFactory.getLogger(AbsWSConsumer.class);

	private static final String	emailInformationUrl				= "email/listDestinatairesEmailInfo";
	private static final String	emailMaladiesUrl				= "email/listApprobateursEmailMaladie";

	private static final String	listeCompteurAnneePrecedenteUrl	= "reposcomps/getListeCompteurAnneePrecedente";
	private static final String	resetCompteurAnneePrecedenteUrl	= "reposcomps/resetCompteurAnneePrecedente";
	private static final String	listeCompteurAnneeEnCoursUrl	= "reposcomps/getListeCompteurAnneeEnCours";
	private static final String	resetCompteurAnneeEnCoursUrl	= "reposcomps/resetCompteurAnneenCours";
	private static final String	listeCompteurCongeAnnuelUrl		= "congeannuel/getListeCompteurCongeAnnuel";
	private static final String	resetCompteurCongeAnnuelUrl		= "congeannuel/resetCompteurCongeAnnuel";
	private static final String	getDemande						= "demandes/demande";
	private static final String	listDemandeRejetDRHStatutVeille	= "demandes/listDemandeRejetDRHStatutVeille";
	private static final String	listIdActeursByAgent			= "droits/listeActeurs";

	private static final String	alimentationAutoCongeAnnuelUrl	= "congeannuel/alimentationAutoCongesAnnuels";
	private static final String	miseAJourSpsoldUrl				= "congeannuel/miseAJourSpsold";
	private static final String	miseAJourSpsorcUrl				= "reposcomps/miseAJourSpsorc";

	@Override
	public EmailInfoDto getListIdDestinatairesEmailInfo() {

		String url = String.format(SIRH_ABS_WS_Base_URL + emailInformationUrl);

		Map<String, String> parameters = new HashMap<String, String>();

		ClientResponse res = createAndFireGetRequest(parameters, url);

		return readResponse(EmailInfoDto.class, res, url);
	}

	@Override
	public EmailInfoDto getListIdApprobateursEmailMaladie() {

		String url = String.format(SIRH_ABS_WS_Base_URL + emailMaladiesUrl);

		Map<String, String> parameters = new HashMap<String, String>();

		ClientResponse res = createAndFireGetRequest(parameters, url);

		return readResponse(EmailInfoDto.class, res, url);
	}

	@Override
	public List<Integer> getListeCompteurAnneePrecedente() {

		String url = String.format(SIRH_ABS_WS_Base_URL + listeCompteurAnneePrecedenteUrl);

		Map<String, String> parameters = new HashMap<String, String>();

		ClientResponse res = createAndFireGetRequest(parameters, url);

		return readResponseAsList(Integer.class, res, url);
	}

	@Override
	public ReturnMessageDto resetCompteurAnneePrecedente(Integer idAgentReposCompCount) {

		String url = String.format(SIRH_ABS_WS_Base_URL + resetCompteurAnneePrecedenteUrl);

		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("idAgentReposCompCount", String.valueOf(idAgentReposCompCount));

		ClientResponse res = createAndFireGetRequest(parameters, url);

		return readResponse(ReturnMessageDto.class, res, url);
	}

	@Override
	public List<Integer> getListeCompteurAnneeEnCours() {

		String url = String.format(SIRH_ABS_WS_Base_URL + listeCompteurAnneeEnCoursUrl);

		Map<String, String> parameters = new HashMap<String, String>();

		ClientResponse res = createAndFireGetRequest(parameters, url);

		return readResponseAsList(Integer.class, res, url);
	}

	@Override
	public ReturnMessageDto resetCompteurAnneeEnCours(Integer idAgentReposCompCount) {

		String url = String.format(SIRH_ABS_WS_Base_URL + resetCompteurAnneeEnCoursUrl);

		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("idAgentReposCompCount", String.valueOf(idAgentReposCompCount));

		ClientResponse res = createAndFireGetRequest(parameters, url);

		return readResponse(ReturnMessageDto.class, res, url);
	}

	@Override
	public List<Integer> getListCompteurCongeAnnuel() {

		String url = String.format(SIRH_ABS_WS_Base_URL + listeCompteurCongeAnnuelUrl);

		Map<String, String> parameters = new HashMap<String, String>();

		ClientResponse res = createAndFireGetRequest(parameters, url);

		return readResponseAsList(Integer.class, res, url);
	}

	@Override
	public ReturnMessageDto resetCompteurCongeAnnuel(Integer idAgentCongeAnnuelCount) {

		String url = String.format(SIRH_ABS_WS_Base_URL + resetCompteurCongeAnnuelUrl);

		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("idAgentCongeAnnuelCount", String.valueOf(idAgentCongeAnnuelCount));

		ClientResponse res = createAndFireGetRequest(parameters, url);

		return readResponse(ReturnMessageDto.class, res, url);
	}

	@Override
	public DemandeDto getDemandeAbsence(Integer idDemande) {

		String url = String.format(SIRH_ABS_WS_Base_URL + getDemande);

		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("idAgent", "90");
		parameters.put("idDemande", String.valueOf(idDemande));
		
		logger.debug("Enter getDemandeAbsence for idDemande = " + idDemande);

		ClientResponse res = createAndFireGetRequest(parameters, url);

		return readResponse(DemandeDto.class, res, url);
	}

	@Override
	public ReturnMessageDto alimentationAutoCongesAnnuels(Integer nomatr, Date dateDebut, Date dateFin) {

		String url = String.format(SIRH_ABS_WS_Base_URL + alimentationAutoCongeAnnuelUrl);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("nomatr", String.valueOf(nomatr));
		parameters.put("dateDebut", sdf.format(dateDebut));
		parameters.put("dateFin", sdf.format(dateFin));

		ClientResponse res = createAndFirePostRequest(parameters, url);

		return readResponse(ReturnMessageDto.class, res, url);
	}

	@Override
	public ReturnMessageDto miseAJourSpSoldAgent(String idAgent) {
		String url = String.format(SIRH_ABS_WS_Base_URL + miseAJourSpsoldUrl);

		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("idAgent", idAgent);

		ClientResponse res = createAndFireGetRequest(parameters, url);

		return readResponse(ReturnMessageDto.class, res, url);
	}

	@Override
	public ReturnMessageDto miseAJourSpSorcAgent(String idAgent) {
		String url = String.format(SIRH_ABS_WS_Base_URL + miseAJourSpsorcUrl);

		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("idAgent", idAgent);

		ClientResponse res = createAndFireGetRequest(parameters, url);

		return readResponse(ReturnMessageDto.class, res, url);
	}

	@Override
	public List<DemandeDto> getListDemandeAbsenceRejetDRHVeille() {

		String url = String.format(SIRH_ABS_WS_Base_URL + listDemandeRejetDRHStatutVeille);

		Map<String, String> parameters = new HashMap<String, String>();

		ClientResponse res = createAndFireGetRequest(parameters, url);

		return readResponseAsList(DemandeDto.class, res, url);
	}

	@Override
	public ActeursDto getListIdActeursByAgent(String idAgent) {

		String url = String.format(SIRH_ABS_WS_Base_URL + listIdActeursByAgent);

		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("idAgent", idAgent);

		ClientResponse res = createAndFireGetRequest(parameters, url);

		return readResponse(ActeursDto.class, res, url);
	}
}
