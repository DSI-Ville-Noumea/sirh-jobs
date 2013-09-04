package nc.noumea.mairie.sirh.job;

import java.util.Date;

import nc.noumea.mairie.ptg.dao.IPointagesDao;
import nc.noumea.mairie.ptg.domain.ExportPaieTask;
import nc.noumea.mairie.sirh.service.IDownloadDocumentService;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Service;

@Service
public class PointagesExportPaieJob  extends QuartzJobBean {

	private Logger logger = LoggerFactory.getLogger(PointagesExportPaieJob.class);
	
	@Autowired
	private IPointagesDao pointagesDao;
	
	@Autowired
	@Qualifier("SIRH_PTG_WS_ExportPaieTaskUrl")
	private String SIRH_PTG_WS_ExportPaieTaskUrl;
	
	@Autowired
	@Qualifier("SIRH_PTG_WS_ExportPaieDoneUrl")
	private String SIRH_PTG_WS_ExportPaieDoneUrl;
	
	@Autowired
	private IDownloadDocumentService downloadDocumentService;
	
	@Override
	protected void executeInternal(JobExecutionContext arg0)
			throws JobExecutionException {
		
		ExportPaieTask vT = null;
		boolean hasExported = false;
		
		do {
			pointagesDao.beginTransaction();
			vT = pointagesDao.getNextExportPaieTask();
			
			if (vT == null) {
				logger.info("Did not find any ExportPaieTask to process... exiting job.");
				pointagesDao.rollBackTransaction();
				continue;
			}
			
			logger.info("Processing ExportPaieTask id [{}] for Agent [{}], schedulded by [{}]...", vT.getIdExportPaieTask(), vT.getIdAgent(), vT.getIdAgentCreation());
	
			try {
				downloadDocumentService.downloadDocumentAs(String.class, String.format("%s%s", SIRH_PTG_WS_ExportPaieTaskUrl, vT.getIdExportPaieTask()), null);
				vT.setTaskStatus("OK");
			} catch (Exception ex) {
				logger.error("An error occured trying to process ExportPaieTask :", ex);
				vT.setTaskStatus(String.format("Erreur: %s", ex.getMessage()));
			}
			
			vT.setDateExport(new Date());
			pointagesDao.commitTransaction();
			
			logger.info("Processed ExportPaieTask id [{}].", vT.getIdExportPaieTask());
		
		} while (vT != null);
		
		if (!hasExported)
			return;
		
		// If at least one task has been processed, SIRH-PTG-WS needs to update the workflow status
		try {
			downloadDocumentService.downloadDocumentAs(String.class, SIRH_PTG_WS_ExportPaieDoneUrl, null);
		} catch (Exception ex) {
			logger.error("An error occured trying to notify SIRH-PTG-WS that all ExportPaieTask have been processed :", ex);
		}
		
	}

}
