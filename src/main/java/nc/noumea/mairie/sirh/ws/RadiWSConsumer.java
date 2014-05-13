package nc.noumea.mairie.sirh.ws;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.noumea.mairie.sirh.eae.dao.DaoException;
import nc.noumea.mairie.sirh.ws.dto.LightUser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

	private Logger logger = LoggerFactory.getLogger(RadiWSConsumer.class);

	@Override
	public List<LightUser> getListeAgentMairie() {

		String url = String.format(RADI_WS_Base_URL + searchUserUrl);

		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("employeenumber", "90*");

		ClientResponse res = createAndFireGetRequest(parameters, url);

		return readResponseAsList(LightUser.class, res, url);
	}

	@Override
	public LightUser retrieveAgentFromLdapFromMatricule(String employeeNumber) throws DaoException {

		String url = String.format(RADI_WS_Base_URL + searchUserUrl);

		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("employeenumber", employeeNumber);

		ClientResponse res = createAndFireGetRequest(parameters, url);

		LightUser user = readResponse(LightUser.class, res, url);
		if (user == null || user.getMail() == null) {
			throw new DaoException(String.format(
					"Expected 1 user corresponding to this employeeNumber '%s' but found null.", employeeNumber));
		}
		logger.info("Agent found: employeeNumber={}, mail={}, login={}.",
				new Object[] { user.getEmployeeNumber(), user.getMail(), user.getsAMAccountName() });
		return user;
	}
}
