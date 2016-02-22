package nc.noumea.mairie.sirh.job;

import java.util.Date;

import nc.noumea.mairie.ptg.dao.IPointagesDao;
import nc.noumea.mairie.ptg.domain.ExportPaieTask;
import nc.noumea.mairie.sirh.service.IDownloadDocumentService;
import nc.noumea.mairie.sirh.tools.IIncidentLoggerService;
import nc.noumea.mairie.sirh.tools.VoRedmineIncidentLogger;

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
public class PointagesExportPaieJob extends QuartzJobBean {

	private Logger logger = LoggerFactory.getLogger(PointagesExportPaieJob.class);
	
	@Autowired
	private IPointagesDao pointagesDao;
	
	@Autowired
	@Qualifier("SIRH_PTG_WS_Base_URL")
	private String SIRH_PTG_WS_Base_URL;
	
	@Autowired
	@Qualifier("SIRH_PTG_WS_ExportPaieTaskUrl")
	private String SIRH_PTG_WS_ExportPaieTaskUrl;
	
	@Autowired
	@Qualifier("SIRH_PTG_WS_ExportPaieDoneUrl")
	private String SIRH_PTG_WS_ExportPaieDoneUrl;
	
	@Autowired
	private IDownloadDocumentService downloadDocumentService;

	@Autowired
	private IIncidentLoggerService incidentLoggerService;
	
	@Override
	protected void executeInternal(JobExecutionContext arg0)
			throws JobExecutionException {
		
		ExportPaieTask eT = null;
		String exportedChainePaie = null;

		VoRedmineIncidentLogger incidentRedmine = new VoRedmineIncidentLogger(this.getClass().getSimpleName());
		
		do {
			pointagesDao.beginTransaction();
			eT = pointagesDao.getNextExportPaieTask();
			
			if (eT == null) {
				logger.info("Did not find any ExportPaieTask to process... exiting job.");
				pointagesDao.rollBackTransaction();
				continue;
			}
			
			logger.info("Processing ExportPaieTask id [{}] for Agent [{}], schedulded by [{}]...", eT.getIdExportPaieTask(), eT.getIdAgent(), eT.getIdAgentCreation());
			exportedChainePaie = eT.getTypeChainePaie();
			try {
				downloadDocumentService.downloadDocumentAs(String.class, String.format("%s%s%s", SIRH_PTG_WS_Base_URL, SIRH_PTG_WS_ExportPaieTaskUrl, eT.getIdExportPaieTask()), null);
				eT.setTaskStatus("OK");
			} catch (Exception ex) {
				logger.error("An error occured trying to process ExportPaieTask :", ex);
				eT.setTaskStatus(String.format("Erreur: %s", ex.getMessage()));
				// #28784 ne pas boucler sur le logger redmine
				incidentRedmine.addException(ex, eT.getIdExportPaieTask());
			}
			
			eT.setDateExport(new Date());
			pointagesDao.commitTransaction();
			
			logger.info("Processed ExportPaieTask id [{}].", eT.getIdExportPaieTask());
		
		} while (eT != null);
		
		if(!incidentRedmine.getListException().isEmpty()) {
			incidentLoggerService.logIncident(incidentRedmine);
		}
		
		if (exportedChainePaie == null)
			return;
		
		// If at least one task has been processed, SIRH-PTG-WS needs to update the workflow status for the given typeChainePaie
		try {
			downloadDocumentService.downloadDocumentAs(String.class, String.format("%s%s%s", SIRH_PTG_WS_Base_URL, SIRH_PTG_WS_ExportPaieDoneUrl, exportedChainePaie), null);
		} catch (Exception ex) {
			logger.error("An error occured trying to notify SIRH-PTG-WS that all ExportPaieTask have been processed :", ex);
			incidentLoggerService.logIncident("PointagesExportPaieJob", ex.getCause() == null ? ex.getMessage() : ex.getCause()
					.getMessage(), "Erreur de la finalisation de l export paie", ex);
		}
		
	}

}
