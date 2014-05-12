package nc.noumea.mairie.sirh.ws;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.noumea.mairie.sirh.ws.dto.LightUser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.sun.jersey.api.client.ClientResponse;

@Service
public class RadiWSConsumer extends BaseWsConsumer implements IRadiWSConsumer {

	@Autowired
	@Qualifier("RADI_WS_Base_URL")
	private String RADI_WS_Base_URL;

	@Autowired
	@Qualifier("RADI_WS_SearchUser")
	private String searchUserUrl;

	@Override
	public List<LightUser> getListeAgentMairie() {

		String url = String.format(RADI_WS_Base_URL + searchUserUrl);

		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("employeenumber", "90*");

		ClientResponse res = createAndFireGetRequest(parameters, url);

		return readResponseAsList(LightUser.class, res, url);
	}
}
