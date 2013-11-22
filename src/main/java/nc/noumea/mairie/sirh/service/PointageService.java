package nc.noumea.mairie.sirh.service;

import java.util.Date;
import java.util.List;

import nc.noumea.mairie.ptg.dao.IPointagesDao;
import nc.noumea.mairie.ptg.domain.EtatPointage;
import nc.noumea.mairie.ptg.domain.EtatPointageEnum;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PointageService implements IPointageService {

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
		
		List<EtatPointage> listEp = null;
		EtatPointage newEp = null;
		
		listEp = pointagesDao.getListePtgRefusesEtRejetesPlus3Mois(etat);
		
		if(null != listEp) {
			for(EtatPointage ep : listEp) {
				
				newEp = new EtatPointage();
				newEp.setDateEtat(dateJour);
				newEp.setDateMaj(dateJour);
				
				if(EtatPointageEnum.REFUSE.equals(ep.getEtat())) {
					newEp.setEtat(EtatPointageEnum.REFUSE_DEFINITIVEMENT);
				}else if(EtatPointageEnum.REJETE.equals(ep.getEtat())) {
					newEp.setEtat(EtatPointageEnum.REJETE_DEFINITIVEMENT);
				}
				
				newEp.setIdAgent(ep.getIdAgent());
				newEp.setIdPointage(ep.getIdPointage());
				newEp.setVersion(ep.getVersion());
				
				pointagesDao.createEtatPointage(newEp);
			}
		}
	}

}
