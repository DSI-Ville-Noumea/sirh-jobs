package nc.noumea.mairie.sirh.job;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.noumea.mairie.abs.dao.IAbsencesDao;
import nc.noumea.mairie.abs.domain.EtatAbsenceEnum;
import nc.noumea.mairie.abs.domain.RefTypeGroupeAbsenceEnum;
import nc.noumea.mairie.sirh.service.IDownloadDocumentService;
import nc.noumea.mairie.sirh.tools.Helper;
import nc.noumea.mairie.sirh.tools.IIncidentLoggerService;
import nc.noumea.mairie.sirh.ws.ReturnMessageDto;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Service;

@Service
@DisallowConcurrentExecution
public class AbsencePriseJob extends QuartzJobBean {

	private Logger logger = LoggerFactory.getLogger(AbsencePriseJob.class);

	@Autowired
	@Qualifier("SIRH_ABS_WS_Base_URL")
	private String SIRH_ABS_WS_Base_URL;

	@Autowired
	@Qualifier("SIRH_ABS_WS_etatAbsenceUrl")
	private String etatAbsenceUrl;

	@Autowired
	private IDownloadDocumentService downloadDocumentService;

	@Autowired
	private IIncidentLoggerService incidentLoggerService;

	@Autowired
	private IAbsencesDao absencesDao;

	@Autowired
	private Helper helper;

	@Override
	public void executeInternal(JobExecutionContext arg0) throws JobExecutionException {

		logger.info("Start AbsencePriseJob");

		absencesDao.beginTransaction();

		// pour les RECUP et les REPOS COMP
		List<Integer> listEpAApprouver = absencesDao.getListeAbsWithEtatAndTypeAbsence(
				getTypeGroupeAbsenceFromApprouveToPrise(), EtatAbsenceEnum.APPROUVEE);
		// pour les ASA, CONGES_EXCEP
		List<Integer> listEpAValider = absencesDao.getListeAbsWithEtatAndTypeAbsence(
				getTypeGroupeAbsenceFromValideToPrise(), EtatAbsenceEnum.VALIDEE);
		// pour les CONGES ANNUELS
		List<Integer> listTypeGroupeAbs = new ArrayList<>();
		listTypeGroupeAbs.add(RefTypeGroupeAbsenceEnum.CONGES_ANNUELS.getValue());
		List<Integer> listCongeAApprouver = absencesDao.getListeAbsWithEtatAndTypeAbsence(listTypeGroupeAbs,
				EtatAbsenceEnum.APPROUVEE);
		List<Integer> listCongeAValider = absencesDao.getListeAbsWithEtatAndTypeAbsence(listTypeGroupeAbs,
				EtatAbsenceEnum.VALIDEE);

		List<Integer> listEp = new ArrayList<>();
		listEp.addAll(listEpAApprouver);
		listEp.addAll(listEpAValider);
		listEp.addAll(listCongeAApprouver);
		listEp.addAll(listCongeAValider);
		logger.info("Found {} demandes to update...", listEp.size());

		absencesDao.rollBackTransaction();

		for (Integer idDemande : listEp) {

			logger.debug("Processing demande id {}...", idDemande);

			Map<String, String> map = new HashMap<String, String>();
			map.put("idDemande", String.valueOf(idDemande));

			String url = String.format("%s%s", SIRH_ABS_WS_Base_URL, etatAbsenceUrl);
			ReturnMessageDto result = null;

			try {
				result = downloadDocumentService.postAs(ReturnMessageDto.class, url, map);
			} catch (Exception ex) {
				logger.error("Une erreur technique est survenue lors du traitement de cette demande.", ex);
				incidentLoggerService.logIncident("AbsencePriseJob", ex.getCause().getMessage(), ex);
			}

			if (result != null && result.getErrors().size() != 0) {
				for (String err : result.getErrors()) {
					logger.info(err);
				}
			}
		}

		logger.info("Processed AbsencePriseJob");
	}

	/**
	 * 
	 * @return la liste des groupes qui sont a valider par SIRH pour passer a l
	 *         etat PRIS
	 */
	private List<Integer> getTypeGroupeAbsenceFromValideToPrise() {
		List<Integer> listTypeAbsASA = new ArrayList<>();
		listTypeAbsASA.add(RefTypeGroupeAbsenceEnum.ASA.getValue());
		listTypeAbsASA.add(RefTypeGroupeAbsenceEnum.CONGES_EXCEP.getValue());
		return listTypeAbsASA;
	}

	/**
	 * 
	 * @return la liste des groupes qui sont a approuver par SIRH pour passer a
	 *         l etat PRIS
	 */
	private List<Integer> getTypeGroupeAbsenceFromApprouveToPrise() {
		List<Integer> listTypeAbsRetRC = new ArrayList<>();
		listTypeAbsRetRC.add(RefTypeGroupeAbsenceEnum.RECUP.getValue());
		listTypeAbsRetRC.add(RefTypeGroupeAbsenceEnum.REPOS_COMP.getValue());
		return listTypeAbsRetRC;
	}
}
