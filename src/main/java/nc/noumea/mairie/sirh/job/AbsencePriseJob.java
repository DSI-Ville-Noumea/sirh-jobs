package nc.noumea.mairie.sirh.job;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.noumea.mairie.abs.dao.IAbsencesDao;
import nc.noumea.mairie.abs.domain.EtatAbsenceEnum;
import nc.noumea.mairie.abs.domain.RefTypeAbsenceEnum;
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
		List<Integer> listEpRetRC = absencesDao.getListeAbsWithEtatAndTypeAbsence(getTypeAbsenceRecupReposComp(),
				EtatAbsenceEnum.APPROUVEE);
		// pour les ASA A48
		List<Integer> listEpA48 = absencesDao.getListeAbsWithEtatAndTypeAbsence(getTypeAbsenceASA(),
				EtatAbsenceEnum.VALIDEE);

		List<Integer> listEp = new ArrayList<>();
		listEp.addAll(listEpRetRC);
		listEp.addAll(listEpA48);
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

	private List<Integer> getTypeAbsenceASA() {
		List<Integer> listTypeAbsASA = new ArrayList<>();
		listTypeAbsASA.add(RefTypeAbsenceEnum.ASA_A48.getValue());
		return listTypeAbsASA;
	}

	private List<Integer> getTypeAbsenceRecupReposComp() {
		List<Integer> listTypeAbsRetRC = new ArrayList<>();
		listTypeAbsRetRC.add(RefTypeAbsenceEnum.RECUP.getValue());
		listTypeAbsRetRC.add(RefTypeAbsenceEnum.REPOS_COMP.getValue());
		return listTypeAbsRetRC;
	}
}
