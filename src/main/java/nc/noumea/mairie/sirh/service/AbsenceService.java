package nc.noumea.mairie.sirh.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.noumea.mairie.abs.dao.IAbsencesDao;
import nc.noumea.mairie.abs.domain.EtatAbsenceEnum;
import nc.noumea.mairie.sirh.ws.BaseWsConsumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.sun.jersey.api.client.ClientResponse;

@Service
public class AbsenceService extends BaseWsConsumer implements IAbsenceService {

	@Autowired
	@Qualifier("SIRH_ABS_WS_Base_URL")
	private String SIRH_ABS_WS_Base_URL;

	@Autowired
	@Qualifier("SIRH_ABS_WS_etatAbsenceUrl")
	private String etatAbsenceUrl;

	@Autowired
	@Qualifier("SIRH_ABS_WS_suppressionAbsenceUrl")
	private String suppressionAbsenceUrl;

	@Autowired
	private IAbsencesDao absencesDao;

	@Override
	public void majEtatAbencePrises() {

		absencesDao.beginTransaction();

		Date dateJour = new Date();

		majEtatAbsences(EtatAbsenceEnum.APPROUVEE, dateJour);

		absencesDao.commitTransaction();
	}

	private void majEtatAbsences(EtatAbsenceEnum etat, Date dateJour) {

		List<Integer> listEp = absencesDao.getListeAbsWithEtat(etat);

		if (listEp != null) {
			String csvIdDemande = "";
			for (Integer i : listEp) {
				csvIdDemande += i + ",";
			}
			if (csvIdDemande.endsWith(",")) {
				csvIdDemande = csvIdDemande.substring(0, csvIdDemande.length() - 1);
			}

			Map<String, String> map = new HashMap<String, String>();
			map.put("listIdDemande", csvIdDemande);

			String url = String.format("%s%s", SIRH_ABS_WS_Base_URL, etatAbsenceUrl);

			ClientResponse res = createAndFirePostRequest(map, url);

			readResponse(res, url);
		}

	}

	@Override
	public void supprimerAbsencesProvisoires() {

		absencesDao.beginTransaction();

		supprimerAbsences(EtatAbsenceEnum.PROVISOIRE);

		absencesDao.commitTransaction();
	}

	private void supprimerAbsences(EtatAbsenceEnum etat) {

		List<Integer> listEp = absencesDao.getListeAbsWithEtat(etat);

		if (listEp != null) {
			String csvIdDemande = "";
			for (Integer i : listEp) {
				csvIdDemande += i + ",";
			}
			if (csvIdDemande.endsWith(",")) {
				csvIdDemande = csvIdDemande.substring(0, csvIdDemande.length() - 1);
			}

			Map<String, String> map = new HashMap<String, String>();
			map.put("listIdDemande", csvIdDemande);

			String url = String.format("%s%s", SIRH_ABS_WS_Base_URL, suppressionAbsenceUrl);

			ClientResponse res = createAndFirePostRequest(map, url);

			readResponse(res, url);
		}
		
	}

}
