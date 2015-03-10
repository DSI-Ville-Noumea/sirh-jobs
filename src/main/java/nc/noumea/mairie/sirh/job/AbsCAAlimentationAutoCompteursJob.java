package nc.noumea.mairie.sirh.job;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nc.noumea.mairie.abs.dao.IAbsencesDao;
import nc.noumea.mairie.abs.domain.CongeAnnuelAlimAutoHisto;
import nc.noumea.mairie.sirh.tools.Helper;
import nc.noumea.mairie.sirh.tools.IIncidentLoggerService;
import nc.noumea.mairie.sirh.ws.IAbsWSConsumer;
import nc.noumea.mairie.sirh.ws.ISirhWSConsumer;
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
public class AbsCAAlimentationAutoCompteursJob extends QuartzJobBean {

	private Logger logger = LoggerFactory.getLogger(AbsCAAlimentationAutoCompteursJob.class);

	@Autowired
	@Qualifier("SIRH_ABS_WS_Base_URL")
	private String SIRH_ABS_WS_Base_URL;

	@Autowired
	private IAbsWSConsumer absWSConsumer;

	@Autowired
	private ISirhWSConsumer sirhWSConsumer;

	@Autowired
	private IIncidentLoggerService incidentLoggerService;

	@Autowired
	private Helper helper;

	@Autowired
	private IAbsencesDao absencesDao;

	@Override
	public void executeInternal(JobExecutionContext arg0) throws JobExecutionException {

		logger.info("Start AbsCAAlimentationAutoCompteursJob");

		List<Integer> listNomatrAgents = new ArrayList<Integer>();
		try {
			listNomatrAgents = sirhWSConsumer.getListAgentPourAlimAutoCompteursCongesAnnuels(
					helper.getFirstDayOfPreviousMonth(), helper.getLastDayOfPreviousMonth());
		} catch (Exception ex) {
			logger.error("Une erreur technique est survenue lors du traitement : ", ex);
			incidentLoggerService.logIncident("AbsCAAlimentationAutoCompteursJob", ex.getMessage(), ex);
		}

		logger.info("Found {} agents counters to update...", listNomatrAgents.size());

		boolean isError = false;
		for (Integer nomatr : listNomatrAgents) {
			logger.debug("Processing agent counters idAgent {}...", helper.getIdAgent(nomatr));

			String error = "";
			ReturnMessageDto result = null;
			try {
				result = absWSConsumer.alimentationAutoCongesAnnuels(nomatr, helper.getFirstDayOfPreviousMonth(),
						helper.getLastDayOfPreviousMonth());
				if (result.getErrors().size() == 0) {
					// redmine #14036 alimentation solde SPSOLD et SPSORC
					ReturnMessageDto resultSpsold = absWSConsumer.miseAJourSpSoldAgent(helper.getIdAgent(nomatr));
					if (resultSpsold.getErrors().size() > 0) {
						result.getErrors().addAll(resultSpsold.getErrors());
					}
					ReturnMessageDto resultSpsorc = absWSConsumer.miseAJourSpSorcAgent(helper.getIdAgent(nomatr));
					if (resultSpsorc.getErrors().size() > 0) {
						result.getErrors().addAll(resultSpsorc.getErrors());
					}
				}

			} catch (Exception ex) {
				logger.error("Une erreur technique est survenue lors du traitement : ", ex);
				incidentLoggerService.logIncident("AbsCAAlimentationAutoCompteursJob", ex.getMessage(), ex);
				error = ex.getMessage();
			}

			if (result != null && result.getErrors().size() != 0) {
				isError = true;

				for (String err : result.getErrors()) {
					logger.info(err);
					error += err + " ; ";
				}
			}

			if ("".equals(error)) {
				error = "OK";
			}

			createCongeAnnuelAlimAutoHisto(Integer.valueOf(helper.getIdAgent(nomatr)), error);
		}

		if (isError) {
			incidentLoggerService.logIncident("AbsCAAlimentationAutoCompteursJob",
					"Erreur de AbsCAAlimentationAutoCompteursJob : voir ABS_CA_ALIM_AUTO_HISTO", null);
		}

		logger.info("Processed AbsCAAlimentationAutoCompteursJob");
	}

	private void createCongeAnnuelAlimAutoHisto(Integer idAgent, String error) {

		absencesDao.beginTransaction();

		if (!"".equals(error)) {
			if (255 < error.length()) {
				error = error.substring(0, 255);
			}
		}

		CongeAnnuelAlimAutoHisto histo = new CongeAnnuelAlimAutoHisto();
		histo.setDateMonth(helper.getFirstDayOfPreviousMonth());
		histo.setDateModification(new Date());
		histo.setIdAgent(idAgent);
		histo.setStatus(error);

		absencesDao.persistObject(histo);

		absencesDao.commitTransaction();
	}
}
