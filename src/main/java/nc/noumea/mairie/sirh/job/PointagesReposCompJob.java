package nc.noumea.mairie.sirh.job;
import nc.noumea.mairie.ptg.dao.IPointagesDao;
import nc.noumea.mairie.ptg.domain.ReposCompTask;
import nc.noumea.mairie.sirh.service.IDownloadDocumentService;
import nc.noumea.mairie.sirh.tools.Helper;
import nc.noumea.mairie.sirh.tools.IIncidentLoggerService;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Service;

@Service
public class PointagesReposCompJob extends QuartzJobBean {

	private Logger logger = LoggerFactory.getLogger(PointagesReposCompJob.class);
	
	@Autowired
	private IPointagesDao pointagesDao;
	
	@Autowired
	private IDownloadDocumentService downloadDocumentService;
	
	@Autowired
	private Helper helper;
	
	@Autowired
	@Qualifier("SIRH_PTG_WS_Base_URL")
	private String SIRH_PTG_WS_Base_URL;
	
	@Autowired
	@Qualifier("SIRH_PTG_WS_ReposCompTaskUrlPart")
	private String SIRH_PTG_WS_ReposCompTaskUrlPart;

	@Autowired
	private IIncidentLoggerService incidentLoggerService;
	
	@Override
	protected void executeInternal(JobExecutionContext arg0) throws JobExecutionException {
		
		ReposCompTask rcT = null;
		
		do {
			pointagesDao.beginTransaction();
			rcT = pointagesDao.getNextReposCompTask();
			
			if (rcT == null) {
				logger.info("Did not find any ReposCompTask to process... exiting job.");
				pointagesDao.rollBackTransaction();
				continue;
			}
			
			logger.info("Processing ReposCompTask id [{}] for Agent [{}] and idVentilDate [{}], schedulded by [{}]...", rcT.getIdRcTask(), rcT.getIdVentilDate(), rcT.getIdAgent(), rcT.getIdAgentCreation());
	
			try {
				downloadDocumentService.downloadDocumentAs(String.class, String.format("%s%s%s", SIRH_PTG_WS_Base_URL, SIRH_PTG_WS_ReposCompTaskUrlPart, rcT.getIdRcTask()), null);
				rcT.setTaskStatus("OK");
			} catch (Exception ex) {
				logger.error("An error occured trying to process ReposCompTask :", ex);
				rcT.setTaskStatus(String.format("Erreur: %s", ex.getMessage()));
				incidentLoggerService.logIncident("PointagesReposCompJob", ex.getCause().getMessage(), ex);
			}
			
			rcT.setDateCalcul(helper.getCurrentDate());
			pointagesDao.commitTransaction();
			
			logger.info("Processed ReposCompTask id [{}].", rcT.getIdRcTask());
		
		} while (rcT != null);
		
	}

}
