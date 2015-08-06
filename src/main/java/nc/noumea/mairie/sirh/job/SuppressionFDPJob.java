package nc.noumea.mairie.sirh.job;

import java.util.Date;

import nc.noumea.mairie.sirh.dao.ISirhDao;
import nc.noumea.mairie.sirh.domain.ActionFDPJob;
import nc.noumea.mairie.sirh.tools.IIncidentLoggerService;
import nc.noumea.mairie.sirh.ws.ISirhWSConsumer;
import nc.noumea.mairie.sirh.ws.ReturnMessageDto;

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
public class SuppressionFDPJob extends QuartzJobBean {

	private Logger logger = LoggerFactory.getLogger(SuppressionFDPJob.class);

	@Autowired
	private ISirhDao sirhDao;

	@Autowired
	private IIncidentLoggerService incidentLoggerService;

	@Autowired
	private ISirhWSConsumer sirhWSConsumer;

	@Override
	protected void executeInternal(JobExecutionContext jobContext) throws JobExecutionException {

		sirhDao.beginTransaction();
		ActionFDPJob eT = sirhDao.getNextSuppressionFDPTask();

		if (eT == null) {
			logger.info("Did not find any SuppressionFDPTask to process... exiting job.");
			sirhDao.rollBackTransaction();
			return;
		}

		logger.info("Processing SuppressionFDPTask id [{}] for idFDP [{} : {}], schedulded by [{}]...",
				eT.getIdActionFdpJob(), eT.getIdFichePoste(), eT.getTypeAction(), eT.getIdAgent());

		try {
			ReturnMessageDto result = sirhWSConsumer.deleteFDP(eT.getIdFichePoste(), eT.getIdAgent());
			if (result.getErrors().size() > 0) {
				eT.setStatut(result.getErrors().get(0));
			} else {
				if (result.getInfos().size() > 0) {
					eT.setStatut(result.getInfos().get(0));
				} else {
					eT.setStatut("OK");
				}
			}
		} catch (Exception ex) {
			logger.error("An error occured trying to process SuppressionFDPTask :", ex);
			eT.setStatut(String.format("Erreur: %s", ex.getMessage()));
			incidentLoggerService.logIncident("SuppressionFDPJob", ex.getCause() == null ? ex.getMessage() : ex
					.getCause().getMessage(), ex);
		}

		eT.setDateStatut(new Date());
		sirhDao.commitTransaction();

		logger.info("Processed SuppressionFDPTask id [{}].", eT.getIdActionFdpJob());
	}
}
