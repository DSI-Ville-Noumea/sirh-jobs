package nc.noumea.mairie.sirh.job;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.internet.MimeMessage;

import nc.noumea.mairie.ldap.dao.IAgentLdapDao;
import nc.noumea.mairie.ldap.domain.AgentLdap;
import nc.noumea.mairie.sirh.dao.ISirhDocumentDao;
import nc.noumea.mairie.sirh.domain.DocumentAssocie;
import nc.noumea.mairie.sirh.eae.dao.IEaeCampagneActionDao;
import nc.noumea.mairie.sirh.eae.domain.EaeCampagneActeur;
import nc.noumea.mairie.sirh.eae.domain.EaeCampagneAction;
import nc.noumea.mairie.sirh.eae.domain.EaeDocument;
import nc.noumea.mairie.sirh.tools.Helper;
import nc.noumea.mairie.sirh.tools.VfsInputStreamSource;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;
import org.apache.velocity.app.VelocityEngine;
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
public class EaeCampagneActionNotificationsJob extends QuartzJobBean implements IEaeCampagneActionNotificationsJob{

	private Logger logger = LoggerFactory.getLogger(EaeCampagneActionNotificationsJob.class);
	
	@Autowired
	private Helper helper;
	
	@Autowired
	private IEaeCampagneActionDao eaeCampagneActionDao;
	
	@Autowired
	private ISirhDocumentDao sirhDocumentDao;
	
	@Autowired
	private IAgentLdapDao agentLdapDao;
	
	@Autowired
	private JavaMailSender mailSender;
	
	@Autowired
	private VelocityEngine velocityEngine;
	
	@Autowired
	@Qualifier("numberOfTries")
	private Integer numberOfTries;
	
	@Autowired
	@Qualifier("baseSirhDocumentsUrl")
	private String baseSirhDocumentsUrl;
		
	private static FileSystemManager fsManager;
	
	public EaeCampagneActionNotificationsJob() throws FileSystemException {
		fsManager = VFS.getManager();
	}
	
	@Override
	protected void executeInternal(JobExecutionContext jobContext) throws JobExecutionException {

		// Run the job
		try {
			sendNotificationsOneByOne();
		} catch (EaeCampagneActionNotificationsException e) {
			throw new JobExecutionException(e);
		}
	}

	public void sendNotificationsOneByOne() throws EaeCampagneActionNotificationsException {
		
		Date today = helper.getCurrentDate();
		
		List<EaeCampagneAction> listOfNotifications = eaeCampagneActionDao.getEaeCampagneActionToSend(today);
		
		logger.info("There are {} EaeCampagneAction notifications to send...", listOfNotifications.size());
		
		if (listOfNotifications.size() == 0)
			return;
		
		for(EaeCampagneAction eA : listOfNotifications) {
			
			logger.info("Sending notification id #{}...", eA.getIdCampagneAction());
			int nbErrors = 0;
			boolean succeeded = false;
			
			while(nbErrors < numberOfTries && !succeeded) {
				
				try {
					sendNotification(eA, today);
					succeeded = true;
				} catch(Exception ex) {
					logger.warn("An error occured while trying to send notification for EaeCampagneAction id #{} with action name '{}' for EaeCampagne id #{} of year {}.",
							new Object[] {eA.getIdCampagneAction(), eA.getNomAction(), eA.getEaeCampagne().getIdEaeCampagne(), eA.getEaeCampagne().getAnnee()});
					logger.warn("Here follows the exception : ", ex);
					nbErrors++;
				}
				
				if (nbErrors >= numberOfTries) {
					logger.error("Stopped sending notifications because exceeded the maximum authorized number of tries: {}.", numberOfTries);
				}
			}
		}
		
		logger.info("Finished sending today's notifications...");
	}
	
	public void sendNotification(final EaeCampagneAction eaeCampagneAction, final Date theDate) throws Exception {
		
		logger.debug("Sending notification for action id #{} : {} due on {}", new Object[] { eaeCampagneAction.getIdCampagneAction(), eaeCampagneAction.getNomAction(), eaeCampagneAction.getDateAfaire()});
		
		// Get the assignee email address for To
		String idAgent = helper.convertIdAgentToADId(eaeCampagneAction.getIdAgent());
		AgentLdap agentTo = agentLdapDao.retrieveAgentFromLdapFromMatricule(idAgent);
		
		// Get the actors email address for Cc
		List<AgentLdap> agentsCc = new ArrayList<AgentLdap>();
		for (EaeCampagneActeur acteur : eaeCampagneAction.getEaeCampagneActeurs()) {
			idAgent = helper.convertIdAgentToADId(acteur.getIdAgent());
			agentsCc.add(agentLdapDao.retrieveAgentFromLdapFromMatricule(idAgent));
		}
		
		// Send the email
		sendEmail(agentTo, agentsCc, eaeCampagneAction, theDate);
		
		// Set the action as notified in the database
		// Settings this before actually sending the email is ok because we're in a transaction and anything failing later on
		// will result in a rollback action thus rewinding this write action
		eaeCampagneActionDao.setDateMailEnvoye(eaeCampagneAction, theDate);
	}
	
	protected void sendEmail(final AgentLdap agentLdap, final List<AgentLdap> agentsCc, final EaeCampagneAction eaeCampagneAction, final Date theDate) throws Exception {
		
		MimeMessagePreparator preparator = new MimeMessagePreparator() {
	        @SuppressWarnings({ "rawtypes", "unchecked" })
			public void prepare(MimeMessage mimeMessage) throws Exception {
	            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
	            
	            // Set the To
	            message.setTo(agentLdap.getMail());
	            
	            // Set the Cc
	            List<String> ccs = new ArrayList<String>();
	            for (AgentLdap agentCc : agentsCc) {
	            	ccs.add(agentCc.getMail());
	            }
	            message.setCc(ccs.toArray(new String[ccs.size()]));
	            
	            // Set the body with velocity
	            Map model = new HashMap();
	            model.put("eaeCampagneAction", eaeCampagneAction);
	            String text = VelocityEngineUtils.mergeTemplateIntoString(
	               velocityEngine, "templates/sirhNotificationTemplate.vm", "UTF-8", model);
	            message.setText(text, true);
	            
	            // Set the subject
	            message.setSubject(String.format("%s à faire pour le %s", eaeCampagneAction.getNomAction(), eaeCampagneAction.getFormattedDateAfaire()));
	            
	            // Set the attached documents
	            for (EaeDocument doc : eaeCampagneAction.getEaeDocuments()) {
	            	DocumentAssocie docA = sirhDocumentDao.getDocumentAssocie(doc.getSirhIdDocument());
	            	FileObject attachedFileVfs = fsManager.resolveFile(String.format("%s%s", baseSirhDocumentsUrl, docA.getLienDocument()));
	            	logger.debug("Adding file '{}' [Exists {}] as attachment...", attachedFileVfs.getURL(), attachedFileVfs.exists());
	            	VfsInputStreamSource res = new VfsInputStreamSource(attachedFileVfs);
	            	message.addAttachment(docA.getNomDocument(), res);
	            }
	         }
	      };
	      
	      // Actually send the email
	      mailSender.send(preparator);
	}
}
