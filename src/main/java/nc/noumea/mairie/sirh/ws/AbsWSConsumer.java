package nc.noumea.mairie.sirh.ws;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
}
