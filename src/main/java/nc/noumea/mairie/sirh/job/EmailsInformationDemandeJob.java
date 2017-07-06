package nc.noumea.mairie.sirh.job;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.internet.MimeMessage;

import nc.noumea.mairie.sirh.eae.dao.DaoException;
import nc.noumea.mairie.sirh.tools.Helper;
import nc.noumea.mairie.sirh.tools.IIncidentLoggerService;
import nc.noumea.mairie.sirh.tools.VoRedmineIncidentLogger;
import nc.noumea.mairie.sirh.ws.IAbsWSConsumer;
import nc.noumea.mairie.sirh.ws.IRadiWSConsumer;
import nc.noumea.mairie.sirh.ws.dto.EmailInfoDto;
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
public class EmailsInformationDemandeJob extends QuartzJobBean {

	private Logger logger = LoggerFactory.getLogger(EmailsInformationDemandeJob.class);

	@Autowired
	private Helper helper;

	@Autowired
	private IAbsWSConsumer absWSConsumer;

	@Autowired
	private IRadiWSConsumer radiWSConsumer;

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private VelocityEngine velocityEngine;

	@Autowired
	@Qualifier("numberOfTriesEmailInformation")
	private Integer numberOfTries;

	@Autowired
	@Qualifier("adresseKiosqueRH")
	private String adresseKiosque;

	@Autowired
	private IIncidentLoggerService incidentLoggerService;

	@Autowired
	@Qualifier("typeEnvironnement")
	private String							typeEnvironnement;
	
	private VoRedmineIncidentLogger incidentRedmine = new VoRedmineIncidentLogger(this.getClass().getSimpleName());

	@Override
	public void executeInternal(JobExecutionContext jobContext) throws JobExecutionException {

		// Run the jobs
		try {
			sendEmailsInformation();
			sendEmailsMaladies();
		} catch (AbsEmailsInformationException e) {
			throw new JobExecutionException(e);
		}
	}

	protected void sendEmailsInformation() throws AbsEmailsInformationException {

		Date today = helper.getCurrentDate();

		EmailInfoDto emailInfoDto = absWSConsumer.getListIdDestinatairesEmailInfo();

		logger.info("There are {} approbateurs for AbsEmailInformation to send...", emailInfoDto.getListApprobateurs().size());
		logger.info("There are {} viseurs for AbsEmailInformation to send...", emailInfoDto.getListViseurs().size());

		if (emailInfoDto.getListApprobateurs().isEmpty() && emailInfoDto.getListViseurs().isEmpty())
			return;

		sendEmailsInformationOneByOne(emailInfoDto.getListApprobateurs(), today, "approuver");
		sendEmailsInformationOneByOne(emailInfoDto.getListViseurs(), today, "viser");
		
		if(!incidentRedmine.getListException().isEmpty()) {
			incidentLoggerService.logIncident(incidentRedmine);
		}

		logger.info("Finished sending today's AbsEmailInformation...");
	}

	protected void sendEmailsInformationOneByOne(List<Integer> listAgent, final Date today, String stringSubject)
			throws AbsEmailsInformationException {

		for (Integer idAgent : listAgent) {

			logger.info("Sending AbsEmailInformation a {} with idAgent {}...", stringSubject, idAgent);
			int nbErrors = 0;
			boolean succeeded = false;

			while (nbErrors < numberOfTries && !succeeded) {

				try {
					sendEmailInformation(idAgent, today, stringSubject);
					succeeded = true;
				} catch (Exception ex) {
					logger.warn("An error occured while trying to send AbsEmailInformation with idAgent {}.",
							new Object[] { idAgent });
					logger.warn("Here follows the exception : ", ex);
					// #28786 ne pas boucler sur le logger redmine
					incidentRedmine.addException(ex, idAgent);
					nbErrors++;
				}

				if (nbErrors >= numberOfTries) {
					logger.error(
							"Stopped sending AbsEmailInformation a {} with idAgent {} because exceeded the maximum authorized number of tries: {}.",
							stringSubject, idAgent, numberOfTries);
				}
			}
		}
	}

	protected void sendEmailInformation(final Integer idAgent, final Date theDate, final String stringSubject)
			throws Exception {

		logger.debug("Sending AbsEmailInformation with idAgent {}", new Object[] { idAgent });

		// Get the assignee email address for To
		// #38736 : on ne gere plus de tickets pour ces cas là
		try {
			LightUser user = radiWSConsumer.retrieveAgentFromLdapFromMatricule(helper.getEmployeeNumber(idAgent));

			// Send the email
			sendEmail(user, theDate, stringSubject);
		} catch (DaoException e) {
			// on ne fait rien
		}
	}

	protected void sendEmail(final LightUser user, final Date theDate, final String stringSubject) throws Exception {

		MimeMessagePreparator preparator = new MimeMessagePreparator() {

			@SuppressWarnings({ "rawtypes", "unchecked" })
			public void prepare(MimeMessage mimeMessage) throws Exception {
				MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");

				// Set the To
				message.setTo(user.getMail());

				// Set the body with velocity
				Map model = new HashMap();
				model.put("adresseKiosque", adresseKiosque);

				// Set the body with velocity
				String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine,
						"templates/sirhEmailInformationTemplate.vm", "UTF-8", model);
				message.setText(text, true);

				// Set the subject
				String sujetMail = "[KIOSQUE RH] Demande d'absences à " + stringSubject;
				if (!typeEnvironnement.equals("PROD")) {
					sujetMail = "[TEST] " + sujetMail;
				}
				message.setSubject(sujetMail);
			}
		};

		// Actually send the email
		mailSender.send(preparator);
	}
	
	/* ============== MALADIES ============== */

	public void sendEmailsMaladies() throws AbsEmailsInformationException {

		Date today = helper.getCurrentDate();

		EmailInfoDto emailMaladiesDto = absWSConsumer.getListIdApprobateursEmailMaladie();

		logger.info("There are {} approbateurs for AbsEmailMaladie to send...", emailMaladiesDto.getListApprobateurs().size());

		if (emailMaladiesDto.getListApprobateurs().isEmpty())
			return;

		sendEmailsMaladiesOneByOne(emailMaladiesDto.getListApprobateurs(), today);
		
		if(!incidentRedmine.getListException().isEmpty()) {
			incidentLoggerService.logIncident(incidentRedmine);
		}

		logger.info("Finished sending today's AbsEmailMaladies...");
	}

	protected void sendEmailsMaladiesOneByOne(List<Integer> listAgent, final Date today)
			throws AbsEmailsInformationException {

		for (Integer idAgent : listAgent) {

			int nbErrors = 0;
			boolean succeeded = false;

			while (nbErrors < numberOfTries && !succeeded) {

				try {
					sendEmailInformationMaladie(idAgent, today);
					succeeded = true;
				} catch (Exception ex) {
					logger.warn("An error occured while trying to send AbsEmailMaladies with idAgent {}.",
							new Object[] { idAgent });
					logger.warn("Here follows the exception : ", ex);
					// #28786 ne pas boucler sur le logger redmine
					incidentRedmine.addException(ex, idAgent);
					nbErrors++;
				}

				if (nbErrors >= numberOfTries) {
					logger.error(
							"Stopped sending AbsEmailMaladies with idAgent {} because exceeded the maximum authorized number of tries: {}.",
							idAgent, numberOfTries);
				}
			}
		}
	}

	protected void sendEmailInformationMaladie(final Integer idAgent, final Date theDate)
			throws Exception {

		logger.info("Sending AbsEmailMaladies with idAgent {}...", idAgent );

		// Get the assignee email address for To
		// #38736 : on ne gere plus de tickets pour ces cas là
		try {
			LightUser user = radiWSConsumer.retrieveAgentFromLdapFromMatricule(helper.getEmployeeNumber(idAgent));

			// Send the email
			sendEmailMaladie(user, theDate);
		} catch (DaoException e) {
			// on ne fait rien
		}
	}

	protected void sendEmailMaladie(final LightUser user, final Date theDate) throws Exception {
		
		logger.info("Sending AbsEmailMaladies to {} !", user.getMail() );

		MimeMessagePreparator preparator = new MimeMessagePreparator() {

			@SuppressWarnings({ "rawtypes", "unchecked" })
			public void prepare(MimeMessage mimeMessage) throws Exception {
				MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");

				// Set the To
				message.setTo(user.getMail());

				// Set the body with velocity
				Map model = new HashMap();
				model.put("adresseKiosque", adresseKiosque);

				// Set the body with velocity
				String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine,
						"templates/sirhEmailMaladiesTemplate.vm", "UTF-8", model);
				message.setText(text, true);

				// Set the subject
				String sujetMail = "[KIOSQUE RH] Congés maladie pour vos agents";
				if (!typeEnvironnement.equals("PROD")) {
					sujetMail = "[TEST] " + sujetMail;
				}
				message.setSubject(sujetMail);
			}
		};

		// Actually send the email
		mailSender.send(preparator);
	}
}
