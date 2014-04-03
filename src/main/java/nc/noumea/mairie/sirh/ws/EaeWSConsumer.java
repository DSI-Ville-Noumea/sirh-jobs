package nc.noumea.mairie.sirh.ws;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.sun.jersey.api.client.ClientResponse;

@Service
public class EaeWSConsumer extends BaseWsConsumer implements IEaeWSConsumer {

	@Autowired
	@Qualifier("eaeWsBaseUrl")
	private String eaeWsBaseUrl;

	private static final String creerEAESansAffecteUrl = "calculEae/creerEAESansAffecte";
	private static final String creerEaeAffecteUrl = "calculEae/creerEaeAffecte";
	
	private String getWSUrl(String pUrl) {
		return eaeWsBaseUrl + pUrl;
	}
	
	@Override
	public ReturnMessageDto creerEAESansAffecte(Integer idCampagneEae, Integer idAgent) {

		Map<String, String> parameters = new HashMap<String, String>();
			parameters.put("idCampagneEae", String.valueOf(idCampagneEae));
			parameters.put("idAgent", String.valueOf(idAgent));
		
		ClientResponse res = createAndFirePostRequest(parameters, getWSUrl(creerEAESansAffecteUrl));
		
		return readResponse(ReturnMessageDto.class, res, getWSUrl(creerEAESansAffecteUrl));
	}

	@Override
	public ReturnMessageDto creerEaeAffecte(Integer idCampagneEae, Integer idAgent) {

		Map<String, String> parameters = new HashMap<String, String>();
			parameters.put("idCampagneEae", String.valueOf(idCampagneEae));
			parameters.put("idAgent", String.valueOf(idAgent));
		
		ClientResponse res = createAndFirePostRequest(parameters, getWSUrl(creerEaeAffecteUrl));
		
		return readResponse(ReturnMessageDto.class, res, getWSUrl(creerEaeAffecteUrl));
	}
	
	
}
