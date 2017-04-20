package nc.noumea.mairie.sirh.job;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.internet.MimeMessage;

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
import org.springframework.util.CollectionUtils;

import nc.noumea.mairie.sirh.tools.Helper;
import nc.noumea.mairie.sirh.tools.IIncidentLoggerService;
import nc.noumea.mairie.sirh.tools.VoRedmineIncidentLogger;
import nc.noumea.mairie.sirh.ws.IAbsWSConsumer;
import nc.noumea.mairie.sirh.ws.IRadiWSConsumer;
import nc.noumea.mairie.sirh.ws.dto.ActeursDto;
import nc.noumea.mairie.sirh.ws.dto.AgentDto;
import nc.noumea.mairie.sirh.ws.dto.ApprobateurDto;
import nc.noumea.mairie.sirh.ws.dto.DemandeDto;
import nc.noumea.mairie.sirh.ws.dto.EmailInfoDto;
import nc.noumea.mairie.sirh.ws.dto.LightUser;

@Service
@DisallowConcurrentExecution
public class EmailsInformationDemandeRejetDRHJob extends QuartzJobBean {

	private Logger					logger			= LoggerFactory.getLogger(EmailsInformationDemandeRejetDRHJob.class);

	@Autowired
	private Helper					helper;

	@Autowired
	private IAbsWSConsumer			absWSConsumer;

	@Autowired
	private IRadiWSConsumer			radiWSConsumer;

	@Autowired
	private JavaMailSender			mailSender;

	@Autowired
	private VelocityEngine			velocityEngine;

	@Autowired
	@Qualifier("numberOfTriesEmailInformation")
	private Integer					numberOfTries;

	@Autowired
	private IIncidentLoggerService	incidentLoggerService;

	private VoRedmineIncidentLogger	incidentRedmine	= new VoRedmineIncidentLogger(this.getClass().getSimpleName());

	@Override
	public void executeInternal(JobExecutionContext jobContext) throws JobExecutionException {

		// Run the job
		try {
			sendEmailsInformationRejetDRH();
		} catch (AbsEmailsInformationException e) {
			throw new JobExecutionException(e);
		}
	}

	protected void sendEmailsInformationRejetDRH() throws AbsEmailsInformationException {

		List<DemandeDto> listeDemandeRejet = absWSConsumer.getListDemandeAbsenceRejetDRHVeille();

		if (CollectionUtils.isEmpty(listeDemandeRejet))
			return;

		// on modifie les dates en format texte
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		for (DemandeDto d : listeDemandeRejet) {
			d.setDateEnString(sdf.format(d.getDateDebut()));
		}

		Date today = helper.getCurrentDate();

		sendEmailsInformationOneByOne(today, listeDemandeRejet);

		if (!incidentRedmine.getListException().isEmpty()) {
			incidentLoggerService.logIncident(incidentRedmine);
		}

		logger.info("Finished sending today's AbsEmailInformationRejetDRH...");
	}

	protected void sendEmailsInformationOneByOne(final Date today, List<DemandeDto> listeDemandeDto) throws AbsEmailsInformationException {

		for (DemandeDto demande : listeDemandeDto) {

			// pour chaque demande, on recupere les approbateurs/operateurs
			ActeursDto acteurDto = absWSConsumer.getListIdActeursByAgent(demande.getAgentWithServiceDto().getIdAgent().toString());

			logger.info("There are {} approbateurs for AbsEmailInformationRejetDRH to send...", acteurDto.getListApprobateurs().size());
			logger.info("There are {} operateurs for AbsEmailInformationRejetDRH to send...", acteurDto.getListOperateurs().size());

			if (acteurDto.getListApprobateurs().isEmpty() && acteurDto.getListOperateurs().isEmpty())
				continue;

			logger.info("Sending AbsEmailInformationRejetDRH  for demande {}...", demande.getIdDemande());

			for (ApprobateurDto approbateur : acteurDto.getListApprobateurs()) {
				int nbErrors = 0;
				boolean succeeded = false;

				while (nbErrors < numberOfTries && !succeeded) {

					try {
						sendEmailInformation(approbateur.getApprobateur().getIdAgent(), today, listeDemandeDto);
						succeeded = true;
					} catch (Exception ex) {
						logger.warn("An error occured while trying to send AbsEmailInformationRejetDRH for demande {}.",
								new Object[] { demande.getIdDemande() });
						logger.warn("Here follows the exception : ", ex);
						// #28786 ne pas boucler sur le logger redmine
						incidentRedmine.addException(ex, demande.getIdDemande());
						nbErrors++;
					}

					if (nbErrors >= numberOfTries) {
						logger.error(
								"Stopped sending AbsEmailInformationRejetDRH  for demande {} because exceeded the maximum authorized number of tries: {}.",
								demande.getIdDemande(), numberOfTries);
					}
				}
			}
			for (AgentDto operateur : acteurDto.getListOperateurs()) {
				int nbErrors = 0;
				boolean succeeded = false;

				while (nbErrors < numberOfTries && !succeeded) {

					try {
						sendEmailInformation(operateur.getIdAgent(), today, listeDemandeDto);
						succeeded = true;
					} catch (Exception ex) {
						logger.warn("An error occured while trying to send AbsEmailInformationRejetDRH for demande {}.",
								new Object[] { demande.getIdDemande() });
						logger.warn("Here follows the exception : ", ex);
						// #28786 ne pas boucler sur le logger redmine
						incidentRedmine.addException(ex, demande.getIdDemande());
						nbErrors++;
					}

					if (nbErrors >= numberOfTries) {
						logger.error(
								"Stopped sending AbsEmailInformationRejetDRH  for demande {} because exceeded the maximum authorized number of tries: {}.",
								demande.getIdDemande(), numberOfTries);
					}
				}
			}

		}
	}

	protected void sendEmailInformation(final Integer idAgent, final Date theDate, List<DemandeDto> listeDemandeDto) throws Exception {

		logger.debug("Sending AbsEmailInformationRejetDRH with idAgent {}", new Object[] { idAgent });

		// Get the assignee email address for To
		LightUser user = radiWSConsumer.retrieveAgentFromLdapFromMatricule(helper.getEmployeeNumber(idAgent));

		// Send the email
		sendEmail(user, theDate, listeDemandeDto);
	}

	protected void sendEmail(final LightUser user, final Date theDate, final List<DemandeDto> listeDemandeDto) throws Exception {

		MimeMessagePreparator preparator = new MimeMessagePreparator() {

			@SuppressWarnings({ "rawtypes", "unchecked" })
			public void prepare(MimeMessage mimeMessage) throws Exception {
				MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");

				// Set the To
				 message.setTo(user.getMail());

				// Set the body with velocity
				Map model = new HashMap();
				model.put("listeDemandeDto", listeDemandeDto);

				// Set the body with velocity
				String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "templates/sirhEmailInformationRejetDRHTemplate.vm",
						"UTF-8", model);
				message.setText(text, true);

				// Set the subject
				message.setSubject("[KIOSQUE RH] Demande d'absences qui ont été rejetées par la DRH");
			}
		};

		// Actually send the email
		mailSender.send(preparator);
	}
}