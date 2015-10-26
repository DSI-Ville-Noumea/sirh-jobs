package nc.noumea.mairie.sirh.job;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.internet.MimeMessage;

import nc.noumea.mairie.ads.ws.IAdsWSConsumer;
import nc.noumea.mairie.ads.ws.dto.EntiteDto;
import nc.noumea.mairie.ads.ws.dto.EntiteHistoDto;
import nc.noumea.mairie.sirh.tools.Helper;
import nc.noumea.mairie.sirh.tools.IIncidentLoggerService;
import nc.noumea.mairie.sirh.ws.IRadiWSConsumer;
import nc.noumea.mairie.sirh.ws.dto.LightUser;

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

@Service
@DisallowConcurrentExecution
public class EmailsInformationAdsJob extends QuartzJobBean {

	private Logger logger = LoggerFactory.getLogger(EmailsInformationAdsJob.class);

	@Autowired
	private Helper helper;

	@Autowired
	private IAdsWSConsumer adsWSConsumer;

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private VelocityEngine velocityEngine;

	@Autowired
	private IRadiWSConsumer radiWSConsumer;

	@Autowired
	@Qualifier("numberOfTriesEmailInformation")
	private Integer numberOfTries;

	@Autowired
	@Qualifier("typeEnvironnement")
	private String typeEnvironnement;

	@Autowired
	private IIncidentLoggerService incidentLoggerService;

	private static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

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
		List<Integer> listeIdAgentDestinataire = adsWSConsumer.getListeIdAgentEmailInfo();

		if (CollectionUtils.isEmpty(listeEntiteHistoDto) || CollectionUtils.isEmpty(listeIdAgentDestinataire))
			return;

		for (EntiteHistoDto histo : listeEntiteHistoDto) {
			EntiteDto as400 = adsWSConsumer.getInfoSiservByIdEntite(histo.getIdEntite());
			if (as400 != null)
				histo.setCodeServiAS400(as400.getCodeServi());
		}

		sendEmailsInformationOneByOne(listeIdAgentDestinataire, listeEntiteHistoDto, today.toDate(), "Organigramme : Compte rendu des changements de statut du " + todayToString);

		logger.info("Finished sending today's AdsEmailInformation...");
	}

	protected void sendEmailsInformationOneByOne(List<Integer> listeIdAgentDestinataire, List<EntiteHistoDto> listeEntiteHistoDto, final Date today, String stringSubject)
			throws PointagesEmailsInformationException {

		for (Integer idAgent : listeIdAgentDestinataire) {

			logger.info("Sending AdsEmailInformation with idAgent {}", idAgent);

			int nbErrors = 0;
			boolean succeeded = false;

			while (nbErrors < numberOfTries && !succeeded) {

				try {
					sendEmailInformation(idAgent, listeEntiteHistoDto, today, stringSubject);
					succeeded = true;
				} catch (Exception ex) {
					logger.warn("An error occured while trying to send AdsEmailInformation.");
					logger.warn("Here follows the exception : ", ex);
					incidentLoggerService.logIncident("AdsEmailInformation", ex.getMessage(), ex);
					nbErrors++;
				}

				if (nbErrors >= numberOfTries) {
					logger.error("Stopped sending AdsEmailInformation a {} because exceeded the maximum authorized number of tries: {}.", stringSubject, numberOfTries);
				}
			}
		}
	}

	protected void sendEmailInformation(final Integer idAgent, final List<EntiteHistoDto> listeEntiteHistoDto, final Date theDate, final String stringSubject) throws Exception {

		logger.debug("Sending AdsEmailInformation with idAgent {}", new Object[] { idAgent });

		// Get the assignee email address for To
		LightUser user = radiWSConsumer.retrieveAgentFromLdapFromMatricule(helper.getEmployeeNumber(idAgent));

		// Send the email
		sendEmail(user, listeEntiteHistoDto, theDate, stringSubject);
	}

	protected void sendEmail(final LightUser user, final List<EntiteHistoDto> listeEntiteHistoDto, final Date theDate, final String stringSubject) throws Exception {

		MimeMessagePreparator preparator = new MimeMessagePreparator() {

			@SuppressWarnings({ "rawtypes", "unchecked" })
			public void prepare(MimeMessage mimeMessage) throws Exception {
				MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");

				// Set the To
				message.setTo(user.getMail());

				// Set the body with velocity
				Map model = new HashMap();
				model.put("dateHistorique", sdf.format(theDate));
				model.put("listeEntiteHistoDto", listeEntiteHistoDto);
				model.put("typeEnvironnement", typeEnvironnement);

				// Set the body with velocity
				String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "templates/adsEmailInformationTemplate.vm", "UTF-8", model);
				message.setText(text, true);

				// Set the subject
				message.setSubject(stringSubject);
			}
		};

		// Actually send the email
		mailSender.send(preparator);
	}
}
