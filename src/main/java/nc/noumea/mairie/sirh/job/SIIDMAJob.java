package nc.noumea.mairie.sirh.job;

import java.util.List;

import nc.noumea.mairie.sirh.dao.ISirhDocumentDao;
import nc.noumea.mairie.sirh.tools.IIncidentLoggerService;
import nc.noumea.mairie.sirh.ws.IRadiWSConsumer;
import nc.noumea.mairie.sirh.ws.dto.LightUser;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Service;

@Service
@DisallowConcurrentExecution
public class SIIDMAJob extends QuartzJobBean {

	private Logger logger = LoggerFactory.getLogger(SIIDMAJob.class);

	@Autowired
	private IRadiWSConsumer radiWSConsumer;

	@Autowired
	private IIncidentLoggerService incidentLoggerService;

	@Autowired
	private ISirhDocumentDao sirhDocumentDao;

	@Override
	public void executeInternal(JobExecutionContext jobContext) throws JobExecutionException {

		logger.info("Start SIIDMAJob");

		// Run the job
		try {
			if (!alimenteSIIDMA()) {
				logger.error("An error occured while trying to delete SIIDMA entries.");
			}
		} catch (Exception e) {
			logger.error("An error occured trying to process SIIDMAJob :", e);
			incidentLoggerService.logIncident("SIIDMAJob", e.getCause() == null ? e.getMessage() : e.getCause()
					.getMessage(), e);
			throw new JobExecutionException(e);
		}
		logger.info("Processed SIIDMAJob");
	}

	protected boolean alimenteSIIDMA() {

		List<LightUser> listUser = radiWSConsumer.getListeAgentMairie();

		logger.info("There are {} user for SIIDMA...", listUser.size());

		if (!videSIIDMA()) {
			return false;
		}

		rempliSIIDMA(listUser);

		logger.info("Finished updating SIIDMA...");
		return true;
	}

	private void rempliSIIDMA(List<LightUser> listUser) {

		for (LightUser user : listUser) {

			logger.debug("Processing user login {} , employeeNumber {} ...", user.getsAMAccountName(),
					user.getEmployeeNumber());

			try {
				sirhDocumentDao.addSIISDMA(user);
			} catch (Exception ex) {
				logger.error("Une erreur technique est survenue lors du traitement de ce user.", ex);
				incidentLoggerService.logIncident("SIIDMAJob", ex.getCause().getMessage(), ex);
			}
		}

	}

	protected boolean videSIIDMA() {
		try {
			sirhDocumentDao.deleteSIIDMAEntries();
		} catch (Exception ex) {
			logger.warn("An error occured while trying to delete SIIDMA entries.");
			logger.warn("Here follows the exception : ", ex);
			incidentLoggerService.logIncident("SIIDMAJob", ex.getMessage(), ex);
			return false;
		}
		return true;
	}
}
