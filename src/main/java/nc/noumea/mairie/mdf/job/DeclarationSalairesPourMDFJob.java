package nc.noumea.mairie.mdf.job;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.activation.DataHandler;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Service;
import org.springframework.ui.velocity.VelocityEngineUtils;

import nc.noumea.mairie.sirh.ws.ISirhWSConsumer;
import nc.noumea.mairie.sirh.ws.dto.LightUser;

@Service
@DisallowConcurrentExecution
public class DeclarationSalairesPourMDFJob extends QuartzJobBean {

	private Logger logger = LoggerFactory.getLogger(DeclarationSalairesPourMDFJob.class);
	
	@Autowired
	private ISirhWSConsumer sirhWsConsumer;

	@Autowired
	@Qualifier("typeEnvironnement")
	private String typeEnvironnement;

	@Autowired
	private JavaMailSender mailSender;

	@Override
	public void executeInternal(JobExecutionContext arg0) throws JobExecutionException {
		logger.debug("Enter in DeclarationSalairesPourMDFJob.");
		FileInputStream result = null;
		try {
			result = sirhWsConsumer.getBordereauRecap();
		} catch (Exception e) {
			logger.error("Une erreur est survenue lors de la récupération du bordereau récapitulatif.");
		}
		
		try {
			sendEmail(result);
		} catch (Exception e) {
			logger.error("Une erreur est survenue lors de l'envoi du bordereau récapitulatif par mail.");
		}
	}

	protected void sendEmail(FileInputStream result) throws Exception {

		final FileInputStream test = result;
		MimeMessagePreparator preparator = new MimeMessagePreparator() {

			@SuppressWarnings({ "rawtypes", "unchecked" })
			public void prepare(MimeMessage mimeMessage) throws Exception {
				MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");

				// Set the To
				message.setTo("theophile.bodin@ville-noumea.nc");

				// Set the body with velocity
				String text = "TEST";
				message.setText(text, true);

				// Set the subject
				String sujetMail = "[MDF] Bordereau récapitulatif";
				if (!typeEnvironnement.equals("PROD")) {
					sujetMail = "[TEST] " + sujetMail;
				}
				message.setSubject(sujetMail);
				message.addAttachment("test.pdf", new InputStreamResource(test));
			}
		};

		// Actually send the email
		mailSender.send(preparator);
		logger.info("Le bordereau récapitulatif a bien été envoyé par mail.");
	}
}
