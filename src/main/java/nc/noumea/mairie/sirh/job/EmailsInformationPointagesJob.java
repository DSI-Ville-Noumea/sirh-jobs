package nc.noumea.mairie.sirh.job;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.internet.MimeMessage;

import nc.noumea.mairie.sirh.tools.Helper;
import nc.noumea.mairie.sirh.tools.IIncidentLoggerService;
import nc.noumea.mairie.sirh.tools.VoRedmineIncidentLogger;
import nc.noumea.mairie.sirh.ws.IPtgWSConsumer;
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
public class EmailsInformationPointagesJob extends QuartzJobBean {

	private Logger logger = LoggerFactory.getLogger(EmailsInformationPointagesJob.class);

	@Autowired
	private Helper helper;

	@Autowired
	private IPtgWSConsumer ptgWSConsumer;

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
	@Qualifier("typeEnvironnement")
	private String							typeEnvironnement;

	@Autowired
	@Qualifier("adresseKiosqueRH")
	private String adresseKiosque;

	@Autowired
	private IIncidentLoggerService incidentLoggerService;
	
	private VoRedmineIncidentLogger incidentRedmine = new VoRedmineIncidentLogger(this.getClass().getSimpleName());

	@Override
	public void executeInternal(JobExecutionContext jobContext) throws JobExecutionException {

		// Run the job
		try {
			sendEmailsInformation();
		} catch (PointagesEmailsInformationException e) {
			throw new JobExecutionException(e);
		}
	}

	protected void sendEmailsInformation() throws PointagesEmailsInformationException {

		Date today = helper.getCurrentDate();

		EmailInfoDto emailInfoDto = ptgWSConsumer.getListIdDestinatairesEmailInfo();

		logger.info("There are {} approbateurs for PtgEmailInformation to send...", emailInfoDto.getListApprobateurs()
				.size());

		if (emailInfoDto.getListApprobateurs().isEmpty())
			return;

		sendEmailsInformationOneByOne(emailInfoDto.getListApprobateurs(), today, "approuver");

		if(!incidentRedmine.getListException().isEmpty()) {
			incidentLoggerService.logIncident(incidentRedmine);
		}
		
		logger.info("Finished sending today's PtgEmailInformation...");
	}

	protected void sendEmailsInformationOneByOne(List<Integer> listAgent, final Date today, String stringSubject)
			throws PointagesEmailsInformationException {

		for (Integer idAgent : listAgent) {

			logger.info("Sending PtgEmailInformation a {} with idAgent {}...", stringSubject, idAgent);
			int nbErrors = 0;
			boolean succeeded = false;

			while (nbErrors < numberOfTries && !succeeded) {

				try {
					sendEmailInformation(idAgent, today, stringSubject);
					succeeded = true;
				} catch (Exception ex) {
					logger.warn("An error occured while trying to send PtgEmailInformation with idAgent {}.",
							new Object[] { idAgent });
					logger.warn("Here follows the exception : ", ex);
					// #28786 ne pas boucler sur le logger redmine
					incidentRedmine.addException(ex, idAgent);
					nbErrors++;
				}

				if (nbErrors >= numberOfTries) {
					logger.error(
							"Stopped sending PtgEmailInformation a {} with idAgent {} because exceeded the maximum authorized number of tries: {}.",
							stringSubject, idAgent, numberOfTries);
				}
			}
		}
	}

	protected void sendEmailInformation(final Integer idAgent, final Date theDate, final String stringSubject)
			throws Exception {

		logger.debug("Sending PtgEmailInformation with idAgent {}", new Object[] { idAgent });

		// Get the assignee email address for To
		LightUser user = radiWSConsumer.retrieveAgentFromLdapFromMatricule(helper.getEmployeeNumber(idAgent));

		// Send the email
		sendEmail(user, theDate, stringSubject);
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
						"templates/sirhEmailInformationPointagesTemplate.vm", "UTF-8", model);
				message.setText(text, true);

				// Set the subject
				String sujetMail = "[KIOSQUE RH] Pointages à " + stringSubject;
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
