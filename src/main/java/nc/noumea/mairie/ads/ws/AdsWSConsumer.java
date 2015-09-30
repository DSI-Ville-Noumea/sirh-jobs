package nc.noumea.mairie.ads.ws;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.noumea.mairie.ads.ws.dto.EntiteDto;
import nc.noumea.mairie.ads.ws.dto.EntiteHistoDto;
import nc.noumea.mairie.sirh.ws.BaseWsConsumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.sun.jersey.api.client.ClientResponse;

@Service
public class AdsWSConsumer extends BaseWsConsumer implements IAdsWSConsumer {

	@Autowired
	@Qualifier("ADS_WS_Base_URL")
	private String ADS_WS_Base_URL;

	@Autowired
	@Qualifier("ADS_WS_ListEntityHistoChangementStatutVeille")
	private String listEntityHistoChangementStatutVeille;

	@Autowired
	@Qualifier("ADS_WS_ListIdAgentEmailInfo")
	private String listeIdAgentEmailInfo;

	@Autowired
	@Qualifier("ADS_WS_SirhAdsGetInfoSiservUrl")
	private String sirhAdsGetInfoSiservUrl;
	
	

	@Override
	public List<EntiteHistoDto> getListeEntiteHistoChangementStatutVeille() {

		String url = String.format(ADS_WS_Base_URL + listEntityHistoChangementStatutVeille);

		Map<String, String> parameters = new HashMap<String, String>();

		ClientResponse res = createAndFireGetRequest(parameters, url);

		return readResponseAsList(EntiteHistoDto.class, res, url);
	}

	@Override
	public List<Integer> getListeIdAgentEmailInfo() {

		String url = String.format(ADS_WS_Base_URL + listeIdAgentEmailInfo);

		Map<String, String> parameters = new HashMap<String, String>();

		ClientResponse res = createAndFireGetRequest(parameters, url);

		return readResponseAsList(Integer.class, res, url);
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
}
