package nc.noumea.mairie.sirh.ws;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.noumea.mairie.sirh.ws.dto.AgentDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.sun.jersey.api.client.ClientResponse;

@Service
public class SirhWSConsumer extends BaseWsConsumer implements ISirhWSConsumer {

	@Autowired
	@Qualifier("sirhWsBaseUrl")
	private String sirhWsBaseUrl;

	private static final String listeAgentEligibleEAESansAffectesUrl = "calculEae/listeAgentEligibleEAESansAffectes";
	private static final String listeAgentEligibleEAEAffectesUrl = "calculEae/listeAgentEligibleEAEAffectes";
	private static final String listAgentPourAlimAutoCompteursCongesAnnuelsUrl = "absences/listAgentPourAlimAutoCompteursCongesAnnuels";
	
	private String getWSUrl(String pUrl) {
		return sirhWsBaseUrl + pUrl;
	}
	
	@Override
	public List<AgentDto> getListeAgentEligibleEAESansAffectes() {
		
		Map<String, String> parameters = new HashMap<String, String>();
		
		ClientResponse res = createAndFireGetRequest(parameters, getWSUrl(listeAgentEligibleEAESansAffectesUrl));
		
		return readResponseAsList(AgentDto.class, res, getWSUrl(listeAgentEligibleEAESansAffectesUrl));
	}

	@Override
	public List<AgentDto> getListeAgentEligibleEAEAffectes() {

		Map<String, String> parameters = new HashMap<String, String>();
		
		ClientResponse res = createAndFireGetRequest(parameters, getWSUrl(listeAgentEligibleEAEAffectesUrl));
		
		return readResponseAsList(AgentDto.class, res, getWSUrl(listeAgentEligibleEAEAffectesUrl));
	}
	
	@Override
	public List<Integer> getListAgentPourAlimAutoCompteursCongesAnnuels(Date dateDebut, Date dateFin) {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("dateDebut", sdf.format(dateDebut));
		parameters.put("dateFin", sdf.format(dateFin));
		
		ClientResponse res = createAndFireGetRequest(parameters, getWSUrl(listAgentPourAlimAutoCompteursCongesAnnuelsUrl));
		
		return readResponseAsList(Integer.class, res, getWSUrl(listAgentPourAlimAutoCompteursCongesAnnuelsUrl));
	}

}
