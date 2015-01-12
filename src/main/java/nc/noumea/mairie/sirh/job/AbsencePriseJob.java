package nc.noumea.mairie.sirh.job;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.internet.MimeMessage;

import nc.noumea.mairie.abs.dao.IAbsencesDao;
import nc.noumea.mairie.abs.domain.EtatAbsenceEnum;
import nc.noumea.mairie.abs.domain.RefTypeGroupeAbsenceEnum;
import nc.noumea.mairie.sirh.dao.ISirhDao;
import nc.noumea.mairie.sirh.service.IDownloadDocumentService;
import nc.noumea.mairie.sirh.tools.Helper;
import nc.noumea.mairie.sirh.tools.IIncidentLoggerService;
import nc.noumea.mairie.sirh.ws.IAbsWSConsumer;
import nc.noumea.mairie.sirh.ws.IRadiWSConsumer;
import nc.noumea.mairie.sirh.ws.ReturnMessageDto;
import nc.noumea.mairie.sirh.ws.dto.DemandeDto;
import nc.noumea.mairie.sirh.ws.dto.LightUser;

import org.apache.velocity.app.VelocityEngine;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Service;
import org.springframework.ui.velocity.VelocityEngineUtils;

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
	private ISirhDao sirhDao;

	@Autowired
	private IAbsWSConsumer absWSConsumer;

	@Autowired
	private IRadiWSConsumer radiWSConsumer;

	@Autowired
	private Helper helper;

	@Autowired
	@Qualifier("numberOfTriesEmailInformation")
	private Integer numberOfTries;

	@Autowired
	private VelocityEngine velocityEngine;

	@Autowired
	private JavaMailSender mailSender;

	@Override
	public void executeInternal(JobExecutionContext arg0) throws JobExecutionException {

		logger.info("Start AbsencePriseJob");

		absencesDao.beginTransaction();

		// pour les RECUP et les REPOS COMP
		List<Integer> listEpAApprouver = absencesDao.getListeAbsWithEtatAndTypeAbsence(
				getTypeGroupeAbsenceFromApprouveToPrise(), EtatAbsenceEnum.APPROUVEE);
		// pour les ASA, CONGES_EXCEP
		List<Integer> listEpAValider = absencesDao.getListeAbsWithEtatAndTypeAbsence(
				getTypeGroupeAbsenceFromValideToPrise(), EtatAbsenceEnum.VALIDEE);
		// pour les CONGES ANNUELS
		List<Integer> listTypeGroupeAbs = new ArrayList<>();
		listTypeGroupeAbs.add(RefTypeGroupeAbsenceEnum.CONGES_ANNUELS.getValue());
		List<Integer> listCongeAApprouver = absencesDao.getListeAbsWithEtatAndTypeAbsence(listTypeGroupeAbs,
				EtatAbsenceEnum.APPROUVEE);
		List<Integer> listCongeAValider = absencesDao.getListeAbsWithEtatAndTypeAbsence(listTypeGroupeAbs,
				EtatAbsenceEnum.VALIDEE);

		// //////////////////////////////////////////////////////
		// on traite le cas du CONGE UNIQUE (conges exceptionnels)
		// //////////////////////////////////////////////////////
		traiteCongeUnique();

		List<Integer> listEp = new ArrayList<>();
		listEp.addAll(listEpAApprouver);
		listEp.addAll(listEpAValider);
		listEp.addAll(listCongeAApprouver);
		listEp.addAll(listCongeAValider);
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

	private void traiteCongeUnique() {
		List<Integer> listeCongeUnique = absencesDao.getListeCongeUnique();
		Integer idAgentGestionnaire = null;
		for (Integer idDemandeCongeUnique : listeCongeUnique) {
			// on recupere la demande
			DemandeDto demande = absWSConsumer.getDemandeAbsence(idDemandeCongeUnique);
			if (demande != null) {
				if (demande.getAgentWithServiceDto().getCodeService() != null) {
					// on cherche le gestionnaire de carriere de l'agent
					List<Integer> listIdAgentGestionnaire = sirhDao.getReferentRHService(demande
							.getAgentWithServiceDto().getCodeService());
					// si on ne trouve pas de gestionnaire alors on cherche le
					// gestionnaire global
					if (listIdAgentGestionnaire.size() == 0) {
						idAgentGestionnaire = sirhDao.getReferentRHGlobal();
					} else {
						idAgentGestionnaire = listIdAgentGestionnaire.get(0);
					}
				} else {
					// on cherche le gestionnaire global
					idAgentGestionnaire = sirhDao.getReferentRHGlobal();
				}

				// on envoi un mail au gestionnaire de carriere
				if (idAgentGestionnaire != null) {
					sendEmailInformationCongeUnique(idAgentGestionnaire, helper.getNomatr(Integer.valueOf(helper
							.getEmployeeNumber(demande.getAgentWithServiceDto().getIdAgent()))), demande
							.getAgentWithServiceDto().getNom(), demande.getAgentWithServiceDto().getPrenom());

					logger.info("Finished sending today's CongeUniqueEmailInformation...");
				} else {
					logger.error("Aucun gestionnaire trouvé pour l'agent {}", demande.getAgentWithServiceDto()
							.getIdAgent());
				}
			} else {
				logger.error("Une erreur technique est survenue lors du traitement de cette demande.",
						idDemandeCongeUnique);
			}
		}
	}

	protected void sendEmailInformationCongeUnique(Integer idAgentGestionnaire, String nomatrAgentCongeUnique,
			String nomAgentCongeUnique, String prenomAgentCongeUnique) {
		String stringSubject = "[KIOSQUE RH] Demande de congé unique à l'état pris.";
		logger.info("Sending CongeUniqueEmailInformation a {} to idAgent {}...", stringSubject, idAgentGestionnaire);
		int nbErrors = 0;
		boolean succeeded = false;

		while (nbErrors < numberOfTries && !succeeded) {

			try {
				sendEmailInformation(idAgentGestionnaire, helper.getCurrentDate(), stringSubject,
						nomatrAgentCongeUnique, nomAgentCongeUnique, prenomAgentCongeUnique);
				succeeded = true;
			} catch (Exception ex) {
				logger.warn("An error occured while trying to send CongeUniqueEmailInformation to idAgent {}.",
						new Object[] { idAgentGestionnaire });
				logger.warn("Here follows the exception : ", ex);
				incidentLoggerService.logIncident("AbsencePriseJob", ex.getMessage(), ex);
				nbErrors++;
			}

			if (nbErrors >= numberOfTries) {
				logger.error(
						"Stopped sending CongeUniqueEmailInformation a {} to idAgent {} because exceeded the maximum authorized number of tries: {}.",
						stringSubject, idAgentGestionnaire, numberOfTries);
			}
		}
	}

	protected void sendEmailInformation(final Integer idAgent, final Date theDate, final String stringSubject,
			final String nomatrAgentCongeUnique, final String nomAgentCongeUnique, final String prenomAgentCongeUnique)
			throws Exception {

		logger.debug("Sending CongeUniqueEmailInformation to idAgent {}", new Object[] { idAgent });

		// Get the assignee email address for To
		LightUser user = radiWSConsumer.retrieveAgentFromLdapFromMatricule(helper.getEmployeeNumber(idAgent));

		// Send the email
		sendEmail(user, theDate, stringSubject, nomatrAgentCongeUnique, nomAgentCongeUnique, prenomAgentCongeUnique);
	}

	protected void sendEmail(final LightUser user, final Date theDate, final String stringSubject,
			final String nomatrAgentCongeUnique, final String nomAgentCongeUnique, final String prenomAgentCongeUnique)
			throws Exception {

		MimeMessagePreparator preparator = new MimeMessagePreparator() {

			@SuppressWarnings({ "rawtypes", "unchecked" })
			public void prepare(MimeMessage mimeMessage) throws Exception {
				MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");

				// Set the To
				message.setTo(user.getMail());

				// Set the body with velocity
				Map model = new HashMap();
				model.put("nom", nomAgentCongeUnique);
				model.put("prenom", prenomAgentCongeUnique);
				model.put("nomatr", nomatrAgentCongeUnique);

				// Set the body with velocity
				String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine,
						"templates/sirhCongeUniqueEmailInformationTemplate.vm", "UTF-8", model);
				message.setText(text, true);

				// Set the subject
				message.setSubject(stringSubject);
			}
		};

		// Actually send the email
		mailSender.send(preparator);
	}

	/**
	 * 
	 * @return la liste des groupes qui sont a valider par SIRH pour passer a l
	 *         etat PRIS
	 */
	private List<Integer> getTypeGroupeAbsenceFromValideToPrise() {
		List<Integer> listTypeAbsASA = new ArrayList<>();
		listTypeAbsASA.add(RefTypeGroupeAbsenceEnum.AS.getValue());
		listTypeAbsASA.add(RefTypeGroupeAbsenceEnum.CONGES_EXCEP.getValue());
		return listTypeAbsASA;
	}

	/**
	 * 
	 * @return la liste des groupes qui sont a approuver par SIRH pour passer a
	 *         l etat PRIS
	 */
	private List<Integer> getTypeGroupeAbsenceFromApprouveToPrise() {
		List<Integer> listTypeAbsRetRC = new ArrayList<>();
		listTypeAbsRetRC.add(RefTypeGroupeAbsenceEnum.RECUP.getValue());
		listTypeAbsRetRC.add(RefTypeGroupeAbsenceEnum.REPOS_COMP.getValue());
		return listTypeAbsRetRC;
	}
}
