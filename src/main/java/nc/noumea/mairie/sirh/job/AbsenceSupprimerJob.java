package nc.noumea.mairie.sirh.job;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.noumea.mairie.abs.dao.IAbsencesDao;
import nc.noumea.mairie.abs.domain.EtatAbsenceEnum;
import nc.noumea.mairie.sirh.service.IDownloadDocumentService;
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
public class AbsenceSupprimerJob extends QuartzJobBean {

	private Logger logger = LoggerFactory.getLogger(AbsenceSupprimerJob.class);
	
	@Autowired
	@Qualifier("SIRH_ABS_WS_Base_URL")
	private String SIRH_ABS_WS_Base_URL;

	@Autowired
	@Qualifier("SIRH_ABS_WS_suppressionAbsenceUrl")
	private String suppressionAbsenceUrl;

	@Autowired
	private IDownloadDocumentService downloadDocumentService;
	
	@Autowired
	private IIncidentLoggerService incidentLoggerService;
	
	@Autowired
	private IAbsencesDao absencesDao;
	
	@Override
	public void executeInternal(JobExecutionContext arg0)
			throws JobExecutionException {
		
		logger.info("Start AbsenceSupprimerJob");
		
		absencesDao.beginTransaction();
		
		List<Integer> listEp = absencesDao.getListeAbsWithEtat(EtatAbsenceEnum.PROVISOIRE);
		logger.info("Found {} demandes to delete...", listEp.size());
		
		absencesDao.rollBackTransaction();
		
		for (Integer idDemande : listEp) {
			
			logger.debug("Processing demande id {}...", idDemande);
			
			Map<String, String> map = new HashMap<String, String>();
			map.put("idDemande", String.valueOf(idDemande));

			String url = String.format("%s%s", SIRH_ABS_WS_Base_URL, suppressionAbsenceUrl);
			ReturnMessageDto result = null;
			
			try {
				result = downloadDocumentService.postAs(ReturnMessageDto.class, url, map);
			} catch (Exception ex) {
				logger.error("Une erreur technique est survenue lors du traitement de cette demande.", ex);
				incidentLoggerService.logIncident("AbsenceSupprimerJob", ex.getCause() == null ? ex.getMessage() : ex.getCause()
						.getMessage(), ex);
			}
			
			if (result != null && result.getErrors().size() != 0) {
				for (String err : result.getErrors()) {
					logger.info(err);
				}
			}
		}
		
		logger.info("Processed AbsenceSupprimerJob");
	}
}
