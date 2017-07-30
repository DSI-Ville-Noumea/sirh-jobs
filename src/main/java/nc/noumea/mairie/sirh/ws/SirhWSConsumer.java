package nc.noumea.mairie.sirh.ws;

import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.noumea.mairie.sirh.ws.dto.AgentDto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.sun.jersey.api.client.ClientResponse;

@Service
public class SirhWSConsumer extends BaseWsConsumer implements ISirhWSConsumer {

	private Logger logger = LoggerFactory.getLogger(SirhWSConsumer.class);

	@Autowired
	@Qualifier("sirhWsBaseUrl")
	private String sirhWsBaseUrl;

	private static final String listeAgentEligibleEAESansAffectesUrl = "calculEae/listeAgentEligibleEAESansAffectes";
	private static final String listeAgentEligibleEAEAffectesUrl = "calculEae/listeAgentEligibleEAEAffectes";
	private static final String listAgentPourAlimAutoCompteursCongesAnnuelsUrl = "absences/listAgentPourAlimAutoCompteursCongesAnnuels";
	private static final String isPaieEnCoursUrl = "utils/isPaieEnCours";
	private static final String deleteFDPUrl = "fichePostes/deleteFichePosteByIdFichePoste";
	private static final String dupliqueFDPUrl = "fichePostes/dupliqueFichePosteByIdFichePoste";
	private static final String activeFDPUrl = "fichePostes/activeFichePosteByIdFichePoste";

	private static final String downloadRecapMdf = "edition/downloadRecapMdf";

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

		ClientResponse res = createAndFireGetRequest(parameters,
				getWSUrl(listAgentPourAlimAutoCompteursCongesAnnuelsUrl));

		return readResponseAsList(Integer.class, res, getWSUrl(listAgentPourAlimAutoCompteursCongesAnnuelsUrl));
	}

	@Override
	public ReturnMessageDto isPaieEnCours() {
		Map<String, String> parameters = new HashMap<String, String>();

		ClientResponse res = createAndFireGetRequest(parameters, getWSUrl(isPaieEnCoursUrl));

		return readResponse(ReturnMessageDto.class, res, getWSUrl(isPaieEnCoursUrl));
	}

	@Override
	public ReturnMessageDto deleteFDP(Integer idFichePoste, Integer idAgent) {
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("idFichePoste", idFichePoste.toString());
		parameters.put("idAgent", idAgent.toString());

		logger.debug("Call sirhWS for deleteFDP with url [{}] for idFDP [{}]", getWSUrl(deleteFDPUrl), idFichePoste);

		ClientResponse res = createAndFireGetRequest(parameters, getWSUrl(deleteFDPUrl));

		return readResponse(ReturnMessageDto.class, res, getWSUrl(deleteFDPUrl));
	}

	@Override
	public ReturnMessageDto dupliqueFDP(Integer idFichePoste, Integer idNewServiceAds, Integer idAgent) {
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("idFichePoste", idFichePoste.toString());
		parameters.put("idEntite", idNewServiceAds.toString());
		parameters.put("idAgent", idAgent.toString());

		logger.debug("Call sirhWS for dupliqueFDP with url [{}] for idFDP [{}]", getWSUrl(dupliqueFDPUrl), idFichePoste);

		ClientResponse res = createAndFireGetRequest(parameters, getWSUrl(dupliqueFDPUrl));

		return readResponse(ReturnMessageDto.class, res, getWSUrl(dupliqueFDPUrl));
	}

	@Override
	public ReturnMessageDto activeFDP(Integer idFichePoste, Integer idAgent) {
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("idFichePoste", idFichePoste.toString());
		parameters.put("idAgent", idAgent.toString());

		logger.debug("Call sirhWS for activeFDP with url [{}] for idFDP [{}]", getWSUrl(activeFDPUrl), idFichePoste);

		ClientResponse res = createAndFireGetRequest(parameters, getWSUrl(activeFDPUrl));

		return readResponse(ReturnMessageDto.class, res, getWSUrl(activeFDPUrl));
	}

	@Override
	@SuppressWarnings("unchecked")
	public FileInputStream getBordereauRecap() throws Exception {
		Map<String, String> parameters = new HashMap<String, String>();
		
		ClientResponse res = createAndFireGetRequest(parameters, getWSUrl(downloadRecapMdf));

		return readResponseAsInputStream(res, getWSUrl(downloadRecapMdf));
	}

}
