package nc.noumea.mairie.sirh.job;

import java.util.Date;
import java.util.List;

import javax.mail.internet.MimeMessage;

import nc.noumea.mairie.ldap.dao.IAgentLdapDao;
import nc.noumea.mairie.ldap.domain.AgentLdap;
import nc.noumea.mairie.sirh.tools.Helper;
import nc.noumea.mairie.sirh.tools.IIncidentLoggerService;
import nc.noumea.mairie.sirh.ws.IAbsWSConsumer;
import nc.noumea.mairie.sirh.ws.dto.EmailInfoDto;

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
	private IAgentLdapDao agentLdapDao;
	
	@Autowired
	private JavaMailSender mailSender;
	
	@Autowired
	private VelocityEngine velocityEngine;
	
	@Autowired
	@Qualifier("numberOfTriesEmailInformation")
	private Integer numberOfTries;
	
	@Autowired
	private IIncidentLoggerService incidentLoggerService;
	
	@Override
	public void executeInternal(JobExecutionContext jobContext) throws JobExecutionException {

		// Run the job
		try {
			sendEmailsInformation();
		} catch (AbsEmailsInformationException e) {
			throw new JobExecutionException(e);
		}
	}
	
	protected void sendEmailsInformation() throws AbsEmailsInformationException {

		Date today = helper.getCurrentDate();
		
		EmailInfoDto emailInfoDto = absWSConsumer.getListIdDestinatairesEmailInfo();

		logger.info("There are {} approbateurs for AbsEmailInformation to send...", emailInfoDto.getListApprobateurs().size());
		logger.info("There are {} viseurs for AbsEmailInformation to send...", emailInfoDto.getListViseurs().size());
		
		if (emailInfoDto.getListApprobateurs().isEmpty() 
				&& emailInfoDto.getListViseurs().isEmpty())
			return;
		
		sendEmailsInformationOneByOne(emailInfoDto.getListApprobateurs(), today, "approuver");
		sendEmailsInformationOneByOne(emailInfoDto.getListViseurs(), today, "viser");
		
		logger.info("Finished sending today's AbsEmailInformation...");
	}

	protected void sendEmailsInformationOneByOne(List<Integer> listAgent, final Date today, String stringSubject) throws AbsEmailsInformationException {
		
		for(Integer idAgent : listAgent) {
			
			logger.info("Sending AbsEmailInformation a {} with idAgent {}...", stringSubject, idAgent);
			int nbErrors = 0;
			boolean succeeded = false;
			
			while(nbErrors < numberOfTries && !succeeded) {
				
				try {
					sendEmailInformation(idAgent, today, stringSubject);
					succeeded = true;
				} catch(Exception ex) {
					logger.warn("An error occured while trying to send AbsEmailInformation with idAgent {}.", new Object[] {idAgent});
					logger.warn("Here follows the exception : ", ex);
					incidentLoggerService.logIncident("EmailsInformationDemandeJob", ex.getMessage(), ex);
					nbErrors++;
				}
				
				if (nbErrors >= numberOfTries) {
					logger.error("Stopped sending AbsEmailInformation a {} with idAgent {} because exceeded the maximum authorized number of tries: {}.", 
							stringSubject, idAgent, numberOfTries);
				}
			}
		}
	}
	
	protected void sendEmailInformation(final Integer idAgent, final Date theDate, final String stringSubject) throws Exception {
		
		logger.debug("Sending AbsEmailInformation with idAgent {}", new Object[] { idAgent });
		
		// Get the assignee email address for To
		String idAgentConverted = helper.convertIdAgentToADId(idAgent);
		AgentLdap agentTo = agentLdapDao.retrieveAgentFromLdapFromMatricule(idAgentConverted);
		agentTo.setMail("johann.REBOULLEAU@ville-noumea.nc");
		// Send the email
		sendEmail(agentTo, theDate, stringSubject);
	}
	
	protected void sendEmail(final AgentLdap agentLdap, final Date theDate, final String stringSubject) throws Exception {
		
		MimeMessagePreparator preparator = new MimeMessagePreparator() { 
			
			public void prepare(MimeMessage mimeMessage) throws Exception {
	            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
	            
	            // Set the To
	            message.setTo(agentLdap.getMail());
	            
	            // Set the body with velocity
	            String text = VelocityEngineUtils.mergeTemplateIntoString(
	               velocityEngine, "templates/sirhEmailInformationTemplate.vm", "UTF-8", null);
	            message.setText(text, true);
	            
	            // Set the subject
	            message.setSubject("[KIOSQUE RH] Demande d'absences à " + stringSubject);
	         }
	      };
	      
	      // Actually send the email
	      mailSender.send(preparator);
	}
}
