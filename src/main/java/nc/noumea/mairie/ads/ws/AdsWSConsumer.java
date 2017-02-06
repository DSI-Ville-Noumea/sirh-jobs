package nc.noumea.mairie.ads.ws;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.sun.jersey.api.client.ClientResponse;

import nc.noumea.mairie.ads.ws.dto.EntiteDto;
import nc.noumea.mairie.ads.ws.dto.EntiteHistoDto;
import nc.noumea.mairie.ads.ws.dto.MailADSDto;
import nc.noumea.mairie.sirh.ws.BaseWsConsumer;

@Service
public class AdsWSConsumer extends BaseWsConsumer implements IAdsWSConsumer {

	@Autowired
	@Qualifier("ADS_WS_Base_URL")
	private String				ADS_WS_Base_URL;
	private static final String	sirhAdsInfoEmailUrl						= "api/email/listeEmailInfo";
	private static final String	listEntityHistoChangementStatutVeille	= "api/email/histoChangementStatutVeille";
	private static final String	sirhAdsGetInfoSiservUrl					= "api/entite/infoSiserv/";

	@Override
	public List<EntiteHistoDto> getListeEntiteHistoChangementStatutVeille() {

		String url = String.format(ADS_WS_Base_URL + listEntityHistoChangementStatutVeille);

		Map<String, String> parameters = new HashMap<String, String>();

		ClientResponse res = createAndFireGetRequest(parameters, url);

		return readResponseAsList(EntiteHistoDto.class, res, url);
	}

	@Override
	public EntiteDto getInfoSiservByIdEntite(Integer idEntite) {

		if (null == idEntite) {
			return null;
		}

		String url = String.format(ADS_WS_Base_URL + sirhAdsGetInfoSiservUrl + idEntite);

		Map<String, String> parameters = new HashMap<String, String>();

		ClientResponse res = createAndFireGetRequest(parameters, url);

		return readResponse(EntiteDto.class, res, url);
	}

	@Override
	public MailADSDto getListeEmailInfo() {

		String url = String.format(ADS_WS_Base_URL + sirhAdsInfoEmailUrl);

		Map<String, String> parameters = new HashMap<String, String>();

		ClientResponse res = createAndFireGetRequest(parameters, url);

		return readResponse(MailADSDto.class, res, url);
	}
}
