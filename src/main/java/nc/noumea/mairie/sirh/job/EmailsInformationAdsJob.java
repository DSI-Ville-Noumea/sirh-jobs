package nc.noumea.mairie.sirh.job;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.internet.MimeMessage;

import org.apache.velocity.app.VelocityEngine;
import org.joda.time.DateTime;
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

import nc.noumea.mairie.ads.ws.IAdsWSConsumer;
import nc.noumea.mairie.ads.ws.dto.EntiteDto;
import nc.noumea.mairie.ads.ws.dto.EntiteHistoDto;
import nc.noumea.mairie.ads.ws.dto.MailADSDto;
import nc.noumea.mairie.sirh.tools.Helper;
import nc.noumea.mairie.sirh.tools.IIncidentLoggerService;
import nc.noumea.mairie.sirh.tools.VoRedmineIncidentLogger;
import nc.noumea.mairie.sirh.ws.IRadiWSConsumer;

@Service
@DisallowConcurrentExecution
public class EmailsInformationAdsJob extends QuartzJobBean {

	private Logger							logger			= LoggerFactory.getLogger(EmailsInformationAdsJob.class);

	@Autowired
	private Helper							helper;

	@Autowired
	private IAdsWSConsumer					adsWSConsumer;

	@Autowired
	private JavaMailSender					mailSender;

	@Autowired
	private VelocityEngine					velocityEngine;

	@Autowired
	private IRadiWSConsumer					radiWSConsumer;

	@Autowired
	@Qualifier("numberOfTriesEmailInformation")
	private Integer							numberOfTries;

	@Autowired
	@Qualifier("typeEnvironnement")
	private String							typeEnvironnement;

	@Autowired
	private IIncidentLoggerService			incidentLoggerService;

	private static final SimpleDateFormat	sdf				= new SimpleDateFormat("dd/MM/yyyy");

	private VoRedmineIncidentLogger			incidentRedmine	= new VoRedmineIncidentLogger(this.getClass().getSimpleName());

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
		// #18865 : on enleve un jour car c'est les changements de la veille
		DateTime today = new DateTime(helper.getCurrentDate());
		today = today.minusDays(1);
		String todayToString = sdf.format(today.toDate());

		List<EntiteHistoDto> listeEntiteHistoDto = adsWSConsumer.getListeEntiteHistoChangementStatutVeille();
		MailADSDto dtoEnvoieMail = adsWSConsumer.getListeEmailInfo();

		if (CollectionUtils.isEmpty(listeEntiteHistoDto))
			return;

		for (EntiteHistoDto histo : listeEntiteHistoDto) {
			EntiteDto as400 = adsWSConsumer.getInfoSiservByIdEntite(histo.getIdEntite());
			if (as400 != null)
				histo.setCodeServiAS400(as400.getCodeServi());
		}

		sendEmailsInformationOneByOne(dtoEnvoieMail, listeEntiteHistoDto, today.toDate(),
				"Organigramme : Compte rendu des changements de statut du " + todayToString);

		if (!incidentRedmine.getListException().isEmpty()) {
			incidentLoggerService.logIncident(incidentRedmine);
		}

		logger.info("Finished sending today's AdsEmailInformation...");
	}

	protected void sendEmailsInformationOneByOne(MailADSDto dtoEnvoieMail, List<EntiteHistoDto> listeEntiteHistoDto, final Date today,
			String stringSubject) throws PointagesEmailsInformationException {
		if (dtoEnvoieMail != null) {

			logger.info("Sending AdsEmailInformation ");

			int nbErrors = 0;
			boolean succeeded = false;

			while (nbErrors < numberOfTries && !succeeded) {

				try {
					sendEmail(dtoEnvoieMail, listeEntiteHistoDto, today, stringSubject);
					succeeded = true;
				} catch (Exception ex) {
					logger.warn("An error occured while trying to send AdsEmailInformation.");
					logger.warn("Here follows the exception : ", ex);
					// #28787 ne pas boucler sur le logger redmine
					incidentRedmine.addException(ex, null);
					nbErrors++;
				}

				if (nbErrors >= numberOfTries) {
					logger.error("Stopped sending AdsEmailInformation a {} because exceeded the maximum authorized number of tries: {}.",
							stringSubject, numberOfTries);
				}
			}
		}
	}

	protected void sendEmail(final MailADSDto dtoEnvoieMail, final List<EntiteHistoDto> listeEntiteHistoDto, final Date theDate,
			final String stringSubject) throws Exception {

		MimeMessagePreparator preparator = new MimeMessagePreparator() {

			@SuppressWarnings({ "rawtypes", "unchecked" })
			public void prepare(MimeMessage mimeMessage) throws Exception {
				MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");

				String dest = "";
				String copie = "";
				String copieCachee = "";
				for (String d : dtoEnvoieMail.getListeDestinataire()) {
					dest = d + ";";
				}
				for (String d : dtoEnvoieMail.getListeCopie()) {
					copie = d + ";";
				}
				for (String d : dtoEnvoieMail.getListeCopieCachee()) {
					copieCachee = d + ";";
				}

				// destinataire
				message.setTo(dest);
				// copie
				message.setCc(copie);
				// copie cachée
				message.setBcc(copieCachee);

				// Set the body with velocity
				Map model = new HashMap();
				model.put("dateHistorique", sdf.format(theDate));
				model.put("listeEntiteHistoDto", listeEntiteHistoDto);

				// Set the body with velocity
				String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "templates/adsEmailInformationTemplate.vm", "UTF-8", model);
				message.setText(text, true);

				// Set the subject
				String sujetMail = stringSubject;
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
