package nc.noumea.mairie.sirh.job;

import java.util.Date;

import nc.noumea.mairie.ptg.dao.IPointagesDao;
import nc.noumea.mairie.ptg.domain.VentilTask;
import nc.noumea.mairie.sirh.service.IDownloadDocumentService;
import nc.noumea.mairie.sirh.tools.IIncidentLoggerService;

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
public class PointagesVentilationJob extends QuartzJobBean {

	private Logger logger = LoggerFactory.getLogger(PointagesVentilationJob.class);

	@Autowired
	private IPointagesDao pointagesDao;

	@Autowired
	@Qualifier("SIRH_PTG_WS_VentilationTaskUrl")
	private String SIRH_PTG_WS_VentilationTaskUrl;

	@Autowired
	@Qualifier("SIRH_PTG_WS_Base_URL")
	private String SIRH_PTG_WS_Base_URL;

	@Autowired
	private IDownloadDocumentService downloadDocumentService;

	@Autowired
	private IIncidentLoggerService incidentLoggerService;

	@Override
	protected void executeInternal(JobExecutionContext arg0) throws JobExecutionException {

		VentilTask vT = null;

		do {
			pointagesDao.beginTransaction();
			vT = pointagesDao.getNextVentilTask();

			if (vT == null) {
				logger.info("Did not find any VentilTask to process... exiting job.");
				pointagesDao.rollBackTransaction();
				continue;
			}

			logger.info("Processing VentilTask id [{}] for Agent [{}], schedulded by [{}]...", vT.getIdVentilTask(),
					vT.getIdAgent(), vT.getIdAgentCreation());

			try {
				downloadDocumentService.downloadDocumentAs(
						String.class,
						String.format("%s%s%s", SIRH_PTG_WS_Base_URL, SIRH_PTG_WS_VentilationTaskUrl,
								vT.getIdVentilTask()), null);
				vT.setTaskStatus("OK");
			} catch (Exception ex) {
				logger.error("An error occured trying to process VentilTask :", ex);
				vT.setTaskStatus(String.format("Erreur: %s", ex.getMessage()));
				incidentLoggerService.logIncident("PointagesVentilationJob", ex.getCause() == null ? ex.getMessage()
						: ex.getCause().getMessage(), ex);
			}

			vT.setDateVentilation(new Date());
			pointagesDao.commitTransaction();

			logger.info("Processed VentilTask id [{}].", vT.getIdVentilTask());

		} while (vT != null);
	}

}
