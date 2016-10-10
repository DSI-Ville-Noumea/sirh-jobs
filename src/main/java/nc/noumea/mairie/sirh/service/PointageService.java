package nc.noumea.mairie.sirh.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.noumea.mairie.ptg.dao.IPointagesDao;
import nc.noumea.mairie.ptg.domain.EtatPointageEnum;
import nc.noumea.mairie.sirh.ws.BaseWsConsumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.sun.jersey.api.client.ClientResponse;

@Service
public class PointageService extends BaseWsConsumer implements IPointageService {

	@Autowired
	@Qualifier("SIRH_PTG_WS_Base_URL")
	private String SIRH_PTG_WS_Base_URL;
	
	private static final String etatPointageUrl = "etatPointage/majEtatPointagesByListId";
	
	@Autowired
	private IPointagesDao pointagesDao;
	
	@Override
	public void majEtatPointagesRefusesEtRejetesPlus3Mois() {

		pointagesDao.beginTransaction();
		
		Date dateJour = new Date();
		
		majEtatPointages(EtatPointageEnum.REFUSE, dateJour);
		majEtatPointages(EtatPointageEnum.REJETE, dateJour);
		
		pointagesDao.commitTransaction();
	}
	
	private void majEtatPointages(EtatPointageEnum etat, Date dateJour) {
		
		List<Integer> listEp = null;
		
		listEp = pointagesDao.getListePtgRefusesEtRejetesPlus3Mois(etat);
		
		if(null != listEp) {
			for(Integer ep : listEp) {
				
				String etatParam = null;
				Map<String, String> map = new HashMap<String, String>();
				map.put("idEtatPointage", String.valueOf(ep));
				
				if(EtatPointageEnum.REFUSE.equals(etat)) {
					etatParam = EtatPointageEnum.REFUSE_DEFINITIVEMENT.toString();
				}else if(EtatPointageEnum.REJETE.equals(etat)) {
					etatParam = EtatPointageEnum.REJETE_DEFINITIVEMENT.toString();
				}
				
				map.put("etat", String.valueOf(etatParam));
				
				String url = String.format("%s%s", SIRH_PTG_WS_Base_URL, etatPointageUrl);
				
				ClientResponse res = createAndFirePostRequest(map, url);

				readResponse(res, url);
			}
		}
	}

	
	
}
