package nc.noumea.mairie.sirh.job;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
	@Qualifier("typeEnvironnement")
	private String							typeEnvironnement;

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
		
		HashMap<Integer, Set<DemandeDto>> demandesBySupervisor = new HashMap<>();

		for (DemandeDto demande : listeDemandeDto) {

			// pour chaque demande, on recupere les approbateurs/operateurs
			ActeursDto acteurDto = absWSConsumer.getListIdActeursByAgent(demande.getAgentWithServiceDto().getIdAgent().toString());

			logger.info("There are {} approbateurs for AbsEmailInformationRejetDRH to send for demande id {}...", acteurDto.getListApprobateurs().size(), demande.getIdDemande());
			logger.info("There are {} operateurs for AbsEmailInformationRejetDRH to send for demande id {}...", acteurDto.getListOperateurs().size(), demande.getIdDemande());
			logger.info("There are {} viseurs for AbsEmailInformationRejetDRH to send for demande id {}...", acteurDto.getListViseurs().size(), demande.getIdDemande());

			if (acteurDto.getListApprobateurs().isEmpty() && acteurDto.getListOperateurs().isEmpty())
				continue;

			logger.info("Sending AbsEmailInformationRejetDRH  for demande {}...", demande.getIdDemande());

			// Alimentation de la Map, avec une liste des demandes pour chaque agent superviseur.
			for (ApprobateurDto approbateur : acteurDto.getListApprobateurs()) {
				AgentDto appro = approbateur.getApprobateur();
				if (demandesBySupervisor.containsKey(appro.getIdAgent())) {
					demandesBySupervisor.get(appro.getIdAgent()).add(demande);
				} else {
					Set<DemandeDto> set = new HashSet();
					set.add(demande);
					demandesBySupervisor.put(appro.getIdAgent(), set);
				}
			}
			for (AgentDto ope : acteurDto.getListOperateurs()) {
				if (demandesBySupervisor.containsKey(ope.getIdAgent())) {
					demandesBySupervisor.get(ope.getIdAgent()).add(demande);
				} else {
					Set<DemandeDto> set = new HashSet();
					set.add(demande);
					demandesBySupervisor.put(ope.getIdAgent(), set);
				}
			}
			for (AgentDto viseur : acteurDto.getListViseurs()) {
				if (demandesBySupervisor.containsKey(viseur.getIdAgent())) {
					demandesBySupervisor.get(viseur.getIdAgent()).add(demande);
				} else {
					Set<DemandeDto> set = new HashSet();
					set.add(demande);
					demandesBySupervisor.put(viseur.getIdAgent(), set);
				}
			}
		}
		
		// On envoie le mail avec la liste des demandes pour chaque superviseur
	    Iterator it = demandesBySupervisor.entrySet().iterator();
	    while (it.hasNext()) {
			int nbErrors = 0;
			boolean succeeded = false;
	        Map.Entry<Integer, Set<DemandeDto>> pair = (Map.Entry) it.next();
			while (nbErrors < numberOfTries && !succeeded) {
		        try {
		        	List<DemandeDto> listDemandes = new ArrayList<>();
		        	listDemandes.addAll(pair.getValue());
					sendEmailInformation(pair.getKey(), today, listDemandes);
					succeeded = true;
				} catch (Exception ex) {
					logger.warn("An error occured while trying to send AbsEmailInformationRejetDRH to agent id {}.", pair.getKey());
					logger.warn("Here follows the exception : ", ex);
					// #28786 ne pas boucler sur le logger redmine
					incidentRedmine.addException(ex, pair.getKey());
					nbErrors++;
				} finally {
			        it.remove(); // avoids a ConcurrentModificationException
				}
				if (nbErrors >= numberOfTries) {
					logger.error("Stopped sending AbsEmailInformationRejetDRH for agent id {} because exceeded the maximum authorized number of tries: {}.",
						 pair.getKey(), numberOfTries);
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
				String sujetMail = "[KIOSQUE RH] Demande d'absences qui ont été rejetées par la DRH";
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
