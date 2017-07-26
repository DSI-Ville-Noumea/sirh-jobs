package nc.noumea.mairie.sirh.job;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.test.util.ReflectionTestUtils;

import nc.noumea.mairie.abs.dao.IAbsencesDao;
import nc.noumea.mairie.sirh.dao.ISirhDao;
import nc.noumea.mairie.sirh.eae.dao.DaoException;
import nc.noumea.mairie.sirh.tools.Helper;
import nc.noumea.mairie.sirh.tools.IIncidentLoggerService;
import nc.noumea.mairie.sirh.ws.IAbsWSConsumer;
import nc.noumea.mairie.sirh.ws.IRadiWSConsumer;
import nc.noumea.mairie.sirh.ws.dto.AgentWithServiceDto;
import nc.noumea.mairie.sirh.ws.dto.DemandeDto;
import nc.noumea.mairie.sirh.ws.dto.EmailInfoDto;
import nc.noumea.mairie.sirh.ws.dto.LightUser;

public class AbsencePriseJobTest {

	private static Helper helperMock;
	private static Date theDate;

	@BeforeClass
	public static void SetUp() {
		theDate = new DateTime(2012, 12, 21, 0, 0, 0, 0).toDate();
		helperMock = Mockito.mock(Helper.class);
		when(helperMock.getCurrentDate()).thenReturn(theDate);
		when(helperMock.getEmployeeNumber(9)).thenReturn("90");
	}

	@Test
	public void AbsencePriseJob_NoEmailToSend_oneReferent_noExistintLDAP() throws AbsEmailsInformationException,
			DaoException {

		List<Integer> listApprobateur = new ArrayList<Integer>();
		listApprobateur.add(9);

		EmailInfoDto dto = new EmailInfoDto();
		dto.setListApprobateurs(listApprobateur);
		dto.setListViseurs(new ArrayList<Integer>());

		// Given
		JavaMailSender mailSender = Mockito.mock(JavaMailSender.class);
		Mockito.doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) {
				return true;
			}
		}).when(mailSender).send(Mockito.isA(MimeMessagePreparator.class));

		IRadiWSConsumer radiWSConsumer = Mockito.mock(IRadiWSConsumer.class);
		Mockito.doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) throws DaoException {
				throw new DaoException();
			}
		}).when(radiWSConsumer).retrieveAgentFromLdapFromMatricule("90");

		IIncidentLoggerService incidentLoggerService = Mockito.mock(IIncidentLoggerService.class);
		Mockito.doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) {
				return true;
			}
		}).when(incidentLoggerService)
				.logIncident(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.any(Exception.class));

		AbsencePriseJob service = new AbsencePriseJob();
		ReflectionTestUtils.setField(service, "helper", helperMock);
		ReflectionTestUtils.setField(service, "radiWSConsumer", radiWSConsumer);
		ReflectionTestUtils.setField(service, "numberOfTries", 2);
		ReflectionTestUtils.setField(service, "mailSender", mailSender);
		ReflectionTestUtils.setField(service, "incidentLoggerService", incidentLoggerService);

		service.sendEmailInformationCongeUnique(9, "5138", "CHARVET", "Tatiana");

		// Then
		verify(mailSender, times(0)).send(Mockito.isA(MimeMessagePreparator.class));
	}

	@Test
	public void AbsencePriseJob_OneEmailToSend_OneReferent() throws AbsEmailsInformationException, DaoException {

		List<Integer> listApprobateur = new ArrayList<Integer>();
		listApprobateur.add(9);

		EmailInfoDto dto = new EmailInfoDto();
		dto.setListApprobateurs(listApprobateur);
		dto.setListViseurs(new ArrayList<Integer>());

		// Given
		JavaMailSender mailSender = Mockito.mock(JavaMailSender.class);
		Mockito.doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) {
				return true;
			}
		}).when(mailSender).send(Mockito.isA(MimeMessagePreparator.class));

		IRadiWSConsumer radiWSConsumer = Mockito.mock(IRadiWSConsumer.class);
		when(radiWSConsumer.retrieveAgentFromLdapFromMatricule("90")).thenReturn(new LightUser());

		AbsencePriseJob service = new AbsencePriseJob();
		ReflectionTestUtils.setField(service, "helper", helperMock);
		ReflectionTestUtils.setField(service, "radiWSConsumer", radiWSConsumer);
		ReflectionTestUtils.setField(service, "numberOfTries", 2);
		ReflectionTestUtils.setField(service, "mailSender", mailSender);

		service.sendEmailInformationCongeUnique(9, "5138", "CHARVET", "Tatiana");

		// Then
		verify(mailSender, times(1)).send(Mockito.isA(MimeMessagePreparator.class));
	}

	@Test
	public void AbsencePriseJob_OneEmailToSend_NoReferent_OneReferentGlobal() throws AbsEmailsInformationException,
			DaoException {

		List<Integer> listViseur = new ArrayList<Integer>();
		listViseur.add(9);

		EmailInfoDto dto = new EmailInfoDto();
		dto.setListApprobateurs(new ArrayList<Integer>());
		dto.setListViseurs(listViseur);

		// Given
		JavaMailSender mailSender = Mockito.mock(JavaMailSender.class);
		Mockito.doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) {
				return true;
			}
		}).when(mailSender).send(Mockito.isA(MimeMessagePreparator.class));

		IRadiWSConsumer radiWSConsumer = Mockito.mock(IRadiWSConsumer.class);
		when(radiWSConsumer.retrieveAgentFromLdapFromMatricule("90")).thenReturn(new LightUser());

		AbsencePriseJob service = new AbsencePriseJob();
		ReflectionTestUtils.setField(service, "helper", helperMock);
		ReflectionTestUtils.setField(service, "radiWSConsumer", radiWSConsumer);
		ReflectionTestUtils.setField(service, "numberOfTries", 2);
		ReflectionTestUtils.setField(service, "mailSender", mailSender);

		service.sendEmailInformationCongeUnique(9, "5138", "CHARVET", "Tatiana");

		// Then
		verify(mailSender, times(1)).send(Mockito.isA(MimeMessagePreparator.class));
	}

	@Test
	public void AbsencePriseJob_traiteCongeUnique_NoReferent() throws DaoException {

		IAbsencesDao absencesDao = Mockito.mock(IAbsencesDao.class);
		List<Integer> list = new ArrayList<>();
		list.add(123);
		when(absencesDao.getListeCongeUnique()).thenReturn(list);
		
		AgentWithServiceDto agent = new AgentWithServiceDto();
		agent.setIdServiceADS(28);
		agent.setIdAgent(5460);
		
		DemandeDto demande = new DemandeDto();
		demande.setAgentWithServiceDto(agent);
		Date dateDebut = new Date();
		demande.setDateDebut(dateDebut);
		demande.setIdDemande(123);
		
		List<Integer> listReferent = new ArrayList<>();
		
		// Logger
		Logger logger = Mockito.mock(Logger.class);
		Mockito.doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) { 
				return true;
			}
		}).when(logger).error(Mockito.anyString(), Mockito.any(Exception.class));

		ISirhDao sirhDao = Mockito.mock(ISirhDao.class);
		when(sirhDao.getReferentRHService(28)).thenReturn(listReferent);
		when(sirhDao.getReferentRHGlobal()).thenReturn(null);

		IAbsWSConsumer absWSConsumer = Mockito.mock(IAbsWSConsumer.class);
		when(absWSConsumer.getDemandeAbsence(123)).thenReturn(demande);
		
		AbsencePriseJob job = new AbsencePriseJob();
		ReflectionTestUtils.setField(job, "helper", helperMock);
		ReflectionTestUtils.setField(job, "absWSConsumer", absWSConsumer);
		ReflectionTestUtils.setField(job, "logger", logger);
		ReflectionTestUtils.setField(job, "sirhDao", sirhDao);
		ReflectionTestUtils.setField(job, "absencesDao", absencesDao);

		job.traiteCongeUnique();

		Mockito.verify(logger, Mockito.times(1)).error("Aucun gestionnaire trouvé pour l'agent {}", 5460);
	}

	@Test
	public void AbsencePriseJob_traiteCongeUnique_AucuneAbsenceTrouvee() throws DaoException {

		IAbsencesDao absencesDao = Mockito.mock(IAbsencesDao.class);
		List<Integer> list = new ArrayList<>();
		list.add(150);
		when(absencesDao.getListeCongeUnique()).thenReturn(list);

		IAbsWSConsumer absWSConsumer = Mockito.mock(IAbsWSConsumer.class);
		when(absWSConsumer.getDemandeAbsence(150)).thenReturn(null);
		
		Logger logger = Mockito.mock(Logger.class);
		Mockito.doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) { 
				return true;
			}
		}).when(logger).error(Mockito.anyString(), Mockito.any(Exception.class));

		AbsencePriseJob job = new AbsencePriseJob();
		ReflectionTestUtils.setField(job, "absWSConsumer", absWSConsumer);
		ReflectionTestUtils.setField(job, "absencesDao", absencesDao);
		ReflectionTestUtils.setField(job, "logger", logger);

		job.traiteCongeUnique();

		Mockito.verify(logger, Mockito.times(1)).error("L'identifiant {} ne correspond à aucune absence.", 150);
	}

	@Test
	public void AbsencePriseJob_traiteCongeUnique_OK() throws DaoException {

		IAbsencesDao absencesDao = Mockito.mock(IAbsencesDao.class);
		List<Integer> list = new ArrayList<>();
		list.add(123);
		when(absencesDao.getListeCongeUnique()).thenReturn(list);
		
		AgentWithServiceDto agent = new AgentWithServiceDto();
		agent.setIdServiceADS(28);
		agent.setIdAgent(5460);
		agent.setNom("Nom");
		agent.setPrenom("Prenom");
		
		DemandeDto demande = new DemandeDto();
		demande.setAgentWithServiceDto(agent);
		Date dateDebut = new Date();
		demande.setDateDebut(dateDebut);
		demande.setIdDemande(123);
		
		List<Integer> listReferent = new ArrayList<>();
		listReferent.add(456);
		
		// Logger
		Logger logger = Mockito.mock(Logger.class);
		Mockito.doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) { 
				return true;
			}
		}).when(logger).error(Mockito.anyString(), Mockito.any(Exception.class));

		ISirhDao sirhDao = Mockito.mock(ISirhDao.class);
		when(sirhDao.getReferentRHService(28)).thenReturn(listReferent);

		IAbsWSConsumer absWSConsumer = Mockito.mock(IAbsWSConsumer.class);
		when(absWSConsumer.getDemandeAbsence(123)).thenReturn(demande);

		when(helperMock.getNomatr(Integer.valueOf("905460"))).thenReturn("9005460");
		when(helperMock.getEmployeeNumber(5460)).thenReturn("905460");

		// Given
		JavaMailSender mailSender = Mockito.mock(JavaMailSender.class);
		Mockito.doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) {
				return true;
			}
		}).when(mailSender).send(Mockito.isA(MimeMessagePreparator.class));

		IRadiWSConsumer radiWSConsumer = Mockito.mock(IRadiWSConsumer.class);
		when(radiWSConsumer.retrieveAgentFromLdapFromMatricule("905460")).thenReturn(new LightUser());
		
		AbsencePriseJob job = new AbsencePriseJob();
		ReflectionTestUtils.setField(job, "helper", helperMock);
		ReflectionTestUtils.setField(job, "absWSConsumer", absWSConsumer);
		ReflectionTestUtils.setField(job, "logger", logger);
		ReflectionTestUtils.setField(job, "sirhDao", sirhDao);
		ReflectionTestUtils.setField(job, "absencesDao", absencesDao);
		ReflectionTestUtils.setField(job, "numberOfTries", 2);
		ReflectionTestUtils.setField(job, "mailSender", mailSender);
		ReflectionTestUtils.setField(job, "radiWSConsumer", radiWSConsumer);

		job.traiteCongeUnique();

		Mockito.verify(logger, Mockito.times(1)).debug("Taille de la liste des congés uniques : 1 demande(s)");
		verify(mailSender, times(1)).send(Mockito.isA(MimeMessagePreparator.class));
	}
}
