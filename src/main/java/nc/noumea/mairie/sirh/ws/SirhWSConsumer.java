package nc.noumea.mairie.sirh.ws;

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

}
