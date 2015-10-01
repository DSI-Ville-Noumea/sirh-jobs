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
public class ActionsFDPJob extends QuartzJobBean {

	private Logger logger = LoggerFactory.getLogger(ActionsFDPJob.class);

	@Autowired
	private ISirhDao sirhDao;

	@Autowired
	private IIncidentLoggerService incidentLoggerService;

	@Autowired
	private ISirhWSConsumer sirhWSConsumer;

	private static final String SUPPRESSION_ACTION = "SUPPRESSION";
	private static final String DUPLICATION_ACTION = "DUPLICATION";
	private static final String ACTIVATION_ACTION = "ACTIVATION";

	@Override
	protected void executeInternal(JobExecutionContext jobContext) throws JobExecutionException {

		ActionFDPJob eT = null;

		do {
			sirhDao.beginTransaction();
			eT = sirhDao.getNextActionFDPTask();

			if (eT == null) {
				logger.info("Did not find any ActionFDPTask to process... exiting job.");
				sirhDao.rollBackTransaction();
				return;
			}

			logger.info("Processing ActionFDPTask id [{}] for idFDP [{} : {}], schedulded by [{}]...", eT.getIdActionFdpJob(), eT.getIdFichePoste(), eT.getTypeAction(), eT.getIdAgent());

			try {
				if (ACTIVATION_ACTION.equals(eT.getTypeAction())) {
					ReturnMessageDto result = sirhWSConsumer.activeFDP(eT.getIdFichePoste(), eT.getIdAgent());
					if (result.getErrors().size() > 0) {
						eT.setStatut(result.getErrors().get(0));
					} else {
						// At this point, everything went allright, the status
						// can be
						// updated to OK
						eT.setStatut("OK");
					}
				}
				if (DUPLICATION_ACTION.equals(eT.getTypeAction())) {
					ReturnMessageDto result = sirhWSConsumer.dupliqueFDP(eT.getIdFichePoste(), eT.getIdNewServiceAds(), eT.getIdAgent());
					if (result.getErrors().size() > 0) {
						eT.setStatut(result.getErrors().get(0));
					} else {
						// At this point, everything went allright, the status
						// can be
						// updated to OK
						if (result.getInfos().size() > 0) {
							String info = "";
							for (String inf : result.getInfos()) {
								info += inf + " ";
							}
							eT.setStatut("OK : " + info);
						} else {
							eT.setStatut("OK");
						}
						if (result.getId() != null)
							eT.setNewIdFichePoste(result.getId());
					}
				}
				if (SUPPRESSION_ACTION.equals(eT.getTypeAction())) {
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
				}

			} catch (Exception ex) {
				logger.error("An error occured trying to process ActionFDPTask :", ex);
				eT.setStatut(String.format("Erreur: %s", ex.getMessage()));
				try {
					incidentLoggerService.logIncident("ActionFDPJob", ex.getCause() == null ? ex.getMessage() : ex.getCause().getMessage(), ex);
				} catch (Exception e) {
					logger.error("An error occured trying to process ActionFDPTask and logIncident in Redmine :", e);
				}
			}

			if (eT.getStatut().length() > 255) {
				eT.setStatut(eT.getStatut().substring(0, 255));
			}

			eT.setDateStatut(new Date());

			logger.info("Processed ActionFDPTask id [{}].", eT.getIdActionFdpJob());
			sirhDao.commitTransaction();
		} while (eT != null);

	}
}
