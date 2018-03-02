package nc.noumea.mairie.sirh.job;

import java.text.SimpleDateFormat;
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

import nc.noumea.mairie.sirh.tools.Helper;
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
	private Helper helper;

	@Autowired
	@Qualifier("typeEnvironnement")
	private String typeEnvironnement;

	@Autowired
	private JavaMailSender mailSender;
	
	private SimpleDateFormat sdf = new SimpleDateFormat("MM-YYYY");
	
	/**
	 * Adresse mail en destination pour l'envoi du bordereau récapitulatif.
	 * Renseigner cette adresse en dur n'est pas la solution définitive. 
	 * C'est la solution adoptée provisoirement, avant de créer un interface de visualisation et modification de cette adresse mail.
	 * (Ou d'abandonner SIRH fin 2018 ...)
	 */
	private final static String RECIPIENT_VDN = "liste-src@ville-noumea.nc";
	private final static String[] LISTE_SRH_MAINTENANCE = {"liste-sirh-maintenance@ville-noumea.nc","support.easyvista@ville-noumea.nc"};

	@Override
	public void executeInternal(JobExecutionContext arg0) throws JobExecutionException {
		logger.debug("Enter in DeclarationSalairesPourMDFJob.");
		byte[] result = null;

		try {
			result = sirhWsConsumer.getBordereauRecap();
		} catch (Exception e) {
			logger.error("Une erreur est survenue lors de la récupération du bordereau récapitulatif de la ville de Nouméa");
			try {
				sendErrorMail();
			} catch (Exception e1) {
				logger.error("Impossible d'envoyer le mail d'erreur.");
			}
		}
		
		try {
			sendEmail(result);
		} catch (Exception e) {
			logger.error("Une erreur est survenue lors de l'envoi du bordereau récapitulatif de la ville de Nouméa par mail.");
		}
	}

	protected void sendEmail(byte[] result) throws Exception {

		final byte[] fResult = result;
		final String lastMonth = sdf.format(helper.getLastMonthDate());
		
		MimeMessagePreparator preparator = new MimeMessagePreparator() {

			@SuppressWarnings({ "rawtypes", "unchecked" })
			public void prepare(MimeMessage mimeMessage) throws Exception {
				MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
				
				String fileName = "bordereau-recap-VDN-" + lastMonth + ".pdf";
				message.setTo(RECIPIENT_VDN);

				// Set the body with velocity
				Map model = new HashMap();
				String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "templates/sirhEmailBordereauRecap.vm", "UTF-8", model);
				message.setText(text, true);

				// Set the subject
				String sujetMail = "[MDF Déclaration rémunérations] Bordereau récapitulatif - Ville de Nouméa";
				if (!typeEnvironnement.equals("PROD")) {
					sujetMail = "[TEST] " + sujetMail;
				}
				message.setSubject(sujetMail);
				message.addAttachment(fileName, new ByteArrayResource(fResult));
			}
		};

		// Actually send the email
		mailSender.send(preparator);
		logger.info("Le bordereau récapitulatif de la ville de Nouméa a bien été envoyé par mail.");
	}

	protected void sendErrorMail() throws Exception {

		MimeMessagePreparator preparator = new MimeMessagePreparator() {

			@SuppressWarnings({ "rawtypes", "unchecked" })
			public void prepare(MimeMessage mimeMessage) throws Exception {
				MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
				
				message.setTo(RECIPIENT_VDN);
				
				// On ajoute la maintenance SIRH en copie cachée
				message.setBcc(LISTE_SRH_MAINTENANCE);

				// Set the body with velocity
				Map model = new HashMap();
				String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "templates/sirhEmailBordereauRecapError.vm", "UTF-8", model);
				message.setText(text, true);

				// Set the subject
				String sujetMail = "[MDF Déclaration rémunérations] Erreur de génération du bordereau récapitulatif - Ville de Nouméa";
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
