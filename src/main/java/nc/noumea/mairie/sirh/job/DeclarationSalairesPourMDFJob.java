package nc.noumea.mairie.sirh.job;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.mail.internet.MimeMessage;

import org.apache.velocity.app.VelocityEngine;
import org.postgresql.translation.messages_bg;
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
	
	// Liste des entités pour la génération du bordereau récapitulatif.
	private static final String VDN = "VDN";
	private static final String PERS = "PERS";
	private static final String ADM = "ADM";
	private static final String[] ENTITES = {VDN, PERS, ADM};
	
	private SimpleDateFormat sdf = new SimpleDateFormat("MM-YYYY");
	
	/**
	 * Adresse mail en destination pour l'envoi du bordereau récapitulatif.
	 * Renseigner cette adresse en dur n'est pas la solution définitive. 
	 * C'est la solution adoptée provisoirement, avant de créer un interface de visualisation et modification de cette adresse mail.
	 */
	private final static String RECIPIENT_VDN = "liste-scr@ville-noumea.nc";
	private final static String RECIPIENT_CDE = "jerome.kartodiwirjo@ville-noumea.nc";

	@Override
	public void executeInternal(JobExecutionContext arg0) throws JobExecutionException {
		logger.debug("Enter in DeclarationSalairesPourMDFJob.");
		byte[] result = null;
		for (String entite : ENTITES) {
			try {
				result = sirhWsConsumer.getBordereauRecap(entite);
			} catch (Exception e) {
				logger.error("Une erreur est survenue lors de la récupération du bordereau récapitulatif de " + entite);
				try {
					sendErrorMail(entite);
				} catch (Exception e1) {
					logger.error("Impossible d'envoyer le mail d'erreur.");
				}
				return;
			}
			
			try {
				sendEmail(result, entite);
			} catch (Exception e) {
				logger.error("Une erreur est survenue lors de l'envoi du bordereau récapitulatif " + entite + " par mail.");
			}
		}
	}

	protected void sendEmail(byte[] result, String entite) throws Exception {

		final byte[] test = result;
		final String fEntite = entite;
		final String lastMonth = sdf.format(helper.getLastMonthDate());
		
		MimeMessagePreparator preparator = new MimeMessagePreparator() {

			@SuppressWarnings({ "rawtypes", "unchecked" })
			public void prepare(MimeMessage mimeMessage) throws Exception {
				MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
				
				String label = "";
				String fileName = "";
				String title = "";
				
				if (fEntite.equals(VDN)) {
					label = "de la ville de Nouméa";
					fileName = "bordereau-recap-VDN-" + lastMonth + ".pdf";
					title = "Ville de Nouméa";
					message.setTo(RECIPIENT_VDN);
				}
				else if (fEntite.equals(PERS)) {
					label = "de la caisse des écoles";
					fileName = "bordereau-recap-CDE-PERS-" + lastMonth + ".pdf";
					title = "Caisse des écoles (PERS)";
					message.setTo(RECIPIENT_CDE);
				}
				else if (fEntite.equals(ADM)) {
					label = "administratif de la caisse des écoles";
					fileName = "bordereau-recap-CDE-ADM-" + lastMonth + ".pdf";
					title = "Caisse des écoles (ADM)";
					message.setTo(RECIPIENT_CDE);
				}

				// Set the body with velocity
				Map model = new HashMap();
				model.put("labelEntite", label);
				String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "templates/sirhEmailBordereauRecap.vm", "UTF-8", model);
				message.setText(text, true);

				// Set the subject
				String sujetMail = "[Mutuelle des Fonctionnaires] Bordereau récapitulatif - " + title;
				if (!typeEnvironnement.equals("PROD")) {
					sujetMail = "[TEST] " + sujetMail;
				}
				message.setSubject(sujetMail);
				message.addAttachment(fileName, new ByteArrayResource(test));
			}
		};

		// Actually send the email
		mailSender.send(preparator);
		logger.info("Le bordereau récapitulatif " + entite + " a bien été envoyé par mail.");
	}

	protected void sendErrorMail(String entite) throws Exception {

		final String fEntite = entite;
		MimeMessagePreparator preparator = new MimeMessagePreparator() {

			@SuppressWarnings({ "rawtypes", "unchecked" })
			public void prepare(MimeMessage mimeMessage) throws Exception {
				MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
				
				String label = "";
				String title = "";
				
				if (fEntite.equals(VDN)) {
					label = "de la ville de Nouméa.";
					title = "Ville de Nouméa";
					message.setTo(RECIPIENT_VDN);
				}
				else if (fEntite.equals(PERS)) {
					label = "de la caisse des écoles.";
					title = "Caisse des écoles (PERS)";
					message.setTo(RECIPIENT_CDE);
				}
				else if (fEntite.equals(ADM)) {
					label = "administratif de la caisse des écoles.";
					title = "Caisse des écoles (ADM)";
					message.setTo(RECIPIENT_CDE);
				}

				// Set the body with velocity
				Map model = new HashMap();
				model.put("labelEntite", label);
				String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "templates/sirhEmailBordereauRecapError.vm", "UTF-8", model);
				message.setText(text, true);

				// Set the subject
				String sujetMail = "[MDF] Erreur de génération du bordereau récapitulatif - " + title;
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
