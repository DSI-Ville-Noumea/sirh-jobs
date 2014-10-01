package nc.noumea.mairie.sirh.ws;

import java.util.HashMap;
import java.util.Map;

import nc.noumea.mairie.sirh.ws.dto.EmailInfoDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.sun.jersey.api.client.ClientResponse;

@Service
public class PtgWSConsumer extends BaseWsConsumer implements IPtgWSConsumer {

	@Autowired
	@Qualifier("SIRH_PTG_WS_Base_URL")
	private String SIRH_PTG_WS_Base_URL;

	@Autowired
	@Qualifier("SIRH_PTG_WS_emailInformationUrl")
	private String emailInformationUrl;

	@Override
	public EmailInfoDto getListIdDestinatairesEmailInfo() {

		String url = String.format(SIRH_PTG_WS_Base_URL + emailInformationUrl);

		Map<String, String> parameters = new HashMap<String, String>();

		ClientResponse res = createAndFireGetRequest(parameters, url);

		return readResponse(EmailInfoDto.class, res, url);
	}
}
