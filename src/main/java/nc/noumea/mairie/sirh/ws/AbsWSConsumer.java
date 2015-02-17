package nc.noumea.mairie.sirh.ws;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.noumea.mairie.sirh.ws.dto.DemandeDto;
import nc.noumea.mairie.sirh.ws.dto.EmailInfoDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.sun.jersey.api.client.ClientResponse;

@Service
public class AbsWSConsumer extends BaseWsConsumer implements IAbsWSConsumer {

	@Autowired
	@Qualifier("SIRH_ABS_WS_Base_URL")
	private String SIRH_ABS_WS_Base_URL;

	@Autowired
	@Qualifier("SIRH_ABS_WS_emailInformationUrl")
	private String emailInformationUrl;

	@Autowired
	@Qualifier("SIRH_ABS_WS_ListeCompteurAnneePrecedenteUrl")
	private String listeCompteurAnneePrecedenteUrl;

	@Autowired
	@Qualifier("SIRH_ABS_WS_ResetCompteurAnneePrecedenteUrl")
	private String resetCompteurAnneePrecedenteUrl;

	@Autowired
	@Qualifier("SIRH_ABS_WS_ListeCompteurAnneeEnCoursUrl")
	private String listeCompteurAnneeEnCoursUrl;

	@Autowired
	@Qualifier("SIRH_ABS_WS_ResetCompteurAnneeEnCoursUrl")
	private String resetCompteurAnneeEnCoursUrl;

	@Autowired
	@Qualifier("SIRH_ABS_WS_ListeCompteurCongeAnnuelUrl")
	private String listeCompteurCongeAnnuelUrl;

	@Autowired
	@Qualifier("SIRH_ABS_WS_ResetCompteurCongeAnnuelUrl")
	private String resetCompteurCongeAnnuelUrl;

	@Autowired
	@Qualifier("SIRH_ABS_WS_GetDemandeUrl")
	private String getDemande;
	
	@Autowired
	@Qualifier("SIRH_ABS_WS_AlimentationAutoCongeAnnuelUrl")
	private String alimentationAutoCongeAnnuelUrl;

	@Override
	public EmailInfoDto getListIdDestinatairesEmailInfo() {

		String url = String.format(SIRH_ABS_WS_Base_URL + emailInformationUrl);

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

		ClientResponse res = createAndFireGetRequest(parameters, url);

		return readResponse(DemandeDto.class, res, url);
	}

	@Override
	public ReturnMessageDto alimentationAutoCongesAnnuels(Integer nomatr,
			Date dateDebut, Date dateFin) {
		
		String url = String.format(SIRH_ABS_WS_Base_URL + alimentationAutoCongeAnnuelUrl);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("nomatr", String.valueOf(nomatr));
		parameters.put("dateDebut", sdf.format(dateDebut));
		parameters.put("dateFin", sdf.format(dateFin));

		ClientResponse res = createAndFirePostRequest(parameters, url);

		return readResponse(ReturnMessageDto.class, res, url);
	}
}
