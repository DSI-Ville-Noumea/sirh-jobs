package nc.noumea.mairie.sirh.job;

import java.util.HashMap;
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
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Service;
import org.springframework.ui.velocity.VelocityEngineUtils;

import nc.noumea.mairie.sirh.ws.ISirhWSConsumer;

@Service
@DisallowConcurrentExecution
public class DeclarationSalairesPourMDFJob extends QuartzJobBean {

	private Logger logger = LoggerFactory.getLogger(DeclarationSalairesPourMDFJob.class);
	
	@Autowired
	private ISirhWSConsumer sirhWsConsumer;

	@Autowired
	private VelocityEngine velocityEngine;

	@Autowired
	@Qualifier("typeEnvironnement")
	private String typeEnvironnement;

	@Autowired
	private JavaMailSender mailSender;
	
	/**
	 * Adresse mail en destination pour l'envoi du bordereau récapitulatif.
	 * Renseigner cette adresse en dur n'est pas la solution définitive. 
	 * C'est la solution adoptée provisoirement, avant de créer un interface de visualisation et modification de cette adresse mail.
	 */
	private final static String RECIPIENT = "liste-scr@ville-noumea.nc";

	@Override
	public void executeInternal(JobExecutionContext arg0) throws JobExecutionException {
		logger.debug("Enter in DeclarationSalairesPourMDFJob.");
		byte[] result = null;
		try {
			result = sirhWsConsumer.getBordereauRecap();
		} catch (Exception e) {
			logger.error("Une erreur est survenue lors de la récupération du bordereau récapitulatif.");
			try {
				sendErrorMail();
			} catch (Exception e1) {
				logger.error("Impossible d'envoyer le mail d'erreur.");
			}
			return;
		}
		
		try {
			sendEmail(result);
		} catch (Exception e) {
			logger.error("Une erreur est survenue lors de l'envoi du bordereau récapitulatif par mail.");
		}
	}

	protected void sendEmail(byte[] result) throws Exception {

		final byte[] test = result;
		MimeMessagePreparator preparator = new MimeMessagePreparator() {

			@SuppressWarnings({ "rawtypes", "unchecked" })
			public void prepare(MimeMessage mimeMessage) throws Exception {
				MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");

				// Set the To
				message.setTo(RECIPIENT);

				// Set the body with velocity
				Map model = new HashMap();
				String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "templates/sirhEmailBordereauRecap.vm", "UTF-8", model);
				message.setText(text, true);

				// Set the subject
				String sujetMail = "[Mutuelle des Fonctionnaires] Bordereau récapitulatif";
				if (!typeEnvironnement.equals("PROD")) {
					sujetMail = "[TEST] " + sujetMail;
				}
				message.setSubject(sujetMail);
				message.addAttachment("test.pdf", new ByteArrayResource(test));
			}
		};

		// Actually send the email
		mailSender.send(preparator);
		logger.info("Le bordereau récapitulatif a bien été envoyé par mail.");
	}

	protected void sendErrorMail() throws Exception {

		MimeMessagePreparator preparator = new MimeMessagePreparator() {

			@SuppressWarnings({ "rawtypes", "unchecked" })
			public void prepare(MimeMessage mimeMessage) throws Exception {
				MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");

				// Set the To
				message.setTo("theophile.bodin@ville-noumea.nc");

				// Set the body with velocity
				Map model = new HashMap();
				String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "templates/sirhEmailBordereauRecapError.vm", "UTF-8", model);
				message.setText(text, true);

				// Set the subject
				String sujetMail = "[MDF] Erreur de génération du bordereau récapitulatif";
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
