package nc.noumea.mairie.sirh.service;

import java.util.Date;

import nc.noumea.mairie.abs.dao.IAbsencesDao;
import nc.noumea.mairie.abs.domain.CongeAnnuelAlimAutoHisto;
import nc.noumea.mairie.sirh.tools.Helper;
import nc.noumea.mairie.sirh.ws.BaseWsConsumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AbsenceService extends BaseWsConsumer implements IAbsenceService {

	@Autowired
	private IAbsencesDao absencesDao;

	@Autowired
	private Helper helper;

	@Override
	public void createCongeAnnuelAlimAutoHisto(Integer idAgent, String error) {

		absencesDao.beginTransaction();

		if (!"".equals(error)) {
			if (255 < error.length()) {
				error = error.substring(0, 255);
			}
		}

		CongeAnnuelAlimAutoHisto histo = new CongeAnnuelAlimAutoHisto();
		histo.setDateMonth(helper.getFirstDayOfPreviousMonth());
		histo.setDateModification(new Date());
		histo.setIdAgent(idAgent);
		histo.setStatus(error);

		absencesDao.commitTransaction();
	}

}
