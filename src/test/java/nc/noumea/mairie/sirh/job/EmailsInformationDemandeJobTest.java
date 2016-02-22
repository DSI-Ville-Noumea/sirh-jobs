package nc.noumea.mairie.sirh.job;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nc.noumea.mairie.sirh.eae.dao.DaoException;
import nc.noumea.mairie.sirh.tools.Helper;
import nc.noumea.mairie.sirh.tools.IIncidentLoggerService;
import nc.noumea.mairie.sirh.ws.IAbsWSConsumer;
import nc.noumea.mairie.sirh.ws.IRadiWSConsumer;
import nc.noumea.mairie.sirh.ws.dto.EmailInfoDto;
import nc.noumea.mairie.sirh.ws.dto.LightUser;

import org.joda.time.DateTime;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.test.util.ReflectionTestUtils;

public class EmailsInformationDemandeJobTest {

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
	public void EmailsInformationDemandeJob_NoEmailToSend_noDestinataire() throws AbsEmailsInformationException {

		EmailInfoDto dto = new EmailInfoDto();
		dto.setListApprobateurs(new ArrayList<Integer>());
		dto.setListViseurs(new ArrayList<Integer>());

		// Given
		IAbsWSConsumer absWSConsumer = Mockito.mock(IAbsWSConsumer.class);
		when(absWSConsumer.getListIdDestinatairesEmailInfo()).thenReturn(dto);

		JavaMailSender mailSender = Mockito.mock(JavaMailSender.class);
		Mockito.doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) {
				return true;
			}
		}).when(mailSender).send(Mockito.isA(MimeMessagePreparator.class));

		EmailsInformationDemandeJob service = new EmailsInformationDemandeJob();
		ReflectionTestUtils.setField(service, "helper", helperMock);
		ReflectionTestUtils.setField(service, "absWSConsumer", absWSConsumer);
		ReflectionTestUtils.setField(service, "mailSender", mailSender);

		// When
		service.sendEmailsInformation();

		// Then
		verify(mailSender, times(0)).send(Mockito.isA(MimeMessagePreparator.class));
	}

	@Test
	public void EmailsInformationDemandeJob_NoEmailToSend_oneApprobateur_noExistintLDAP()
			throws AbsEmailsInformationException, DaoException {

		List<Integer> listApprobateur = new ArrayList<Integer>();
		listApprobateur.add(9);

		EmailInfoDto dto = new EmailInfoDto();
		dto.setListApprobateurs(listApprobateur);
		dto.setListViseurs(new ArrayList<Integer>());

		// Given
		IAbsWSConsumer absWSConsumer = Mockito.mock(IAbsWSConsumer.class);
		when(absWSConsumer.getListIdDestinatairesEmailInfo()).thenReturn(dto);

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

		EmailsInformationDemandeJob service = new EmailsInformationDemandeJob();
		ReflectionTestUtils.setField(service, "helper", helperMock);
		ReflectionTestUtils.setField(service, "absWSConsumer", absWSConsumer);
		ReflectionTestUtils.setField(service, "radiWSConsumer", radiWSConsumer);
		ReflectionTestUtils.setField(service, "numberOfTries", 2);
		ReflectionTestUtils.setField(service, "mailSender", mailSender);
		ReflectionTestUtils.setField(service, "incidentLoggerService", incidentLoggerService);

		service.sendEmailsInformation();

		// Then
		verify(mailSender, times(0)).send(Mockito.isA(MimeMessagePreparator.class));
	}

	@Test
	public void EmailsInformationDemandeJob_OneEmailToSend_OneApprobateur() throws AbsEmailsInformationException,
			DaoException {

		List<Integer> listApprobateur = new ArrayList<Integer>();
		listApprobateur.add(9);

		EmailInfoDto dto = new EmailInfoDto();
		dto.setListApprobateurs(listApprobateur);
		dto.setListViseurs(new ArrayList<Integer>());

		// Given
		IAbsWSConsumer absWSConsumer = Mockito.mock(IAbsWSConsumer.class);
		when(absWSConsumer.getListIdDestinatairesEmailInfo()).thenReturn(dto);

		JavaMailSender mailSender = Mockito.mock(JavaMailSender.class);
		Mockito.doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) {
				return true;
			}
		}).when(mailSender).send(Mockito.isA(MimeMessagePreparator.class));

		IRadiWSConsumer radiWSConsumer = Mockito.mock(IRadiWSConsumer.class);
		when(radiWSConsumer.retrieveAgentFromLdapFromMatricule("90")).thenReturn(new LightUser());

		EmailsInformationDemandeJob service = new EmailsInformationDemandeJob();
		ReflectionTestUtils.setField(service, "helper", helperMock);
		ReflectionTestUtils.setField(service, "absWSConsumer", absWSConsumer);
		ReflectionTestUtils.setField(service, "radiWSConsumer", radiWSConsumer);
		ReflectionTestUtils.setField(service, "numberOfTries", 2);
		ReflectionTestUtils.setField(service, "mailSender", mailSender);

		service.sendEmailsInformation();

		// Then
		verify(mailSender, times(1)).send(Mockito.isA(MimeMessagePreparator.class));
	}

	@Test
	public void EmailsInformationDemandeJob_OneEmailToSend_OneViseur() throws AbsEmailsInformationException,
			DaoException {

		List<Integer> listViseur = new ArrayList<Integer>();
		listViseur.add(9);

		EmailInfoDto dto = new EmailInfoDto();
		dto.setListApprobateurs(new ArrayList<Integer>());
		dto.setListViseurs(listViseur);

		// Given
		IAbsWSConsumer absWSConsumer = Mockito.mock(IAbsWSConsumer.class);
		when(absWSConsumer.getListIdDestinatairesEmailInfo()).thenReturn(dto);

		JavaMailSender mailSender = Mockito.mock(JavaMailSender.class);
		Mockito.doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) {
				return true;
			}
		}).when(mailSender).send(Mockito.isA(MimeMessagePreparator.class));

		IRadiWSConsumer radiWSConsumer = Mockito.mock(IRadiWSConsumer.class);
		when(radiWSConsumer.retrieveAgentFromLdapFromMatricule("90")).thenReturn(new LightUser());

		EmailsInformationDemandeJob service = new EmailsInformationDemandeJob();
		ReflectionTestUtils.setField(service, "helper", helperMock);
		ReflectionTestUtils.setField(service, "absWSConsumer", absWSConsumer);
		ReflectionTestUtils.setField(service, "radiWSConsumer", radiWSConsumer);
		ReflectionTestUtils.setField(service, "numberOfTries", 2);
		ReflectionTestUtils.setField(service, "mailSender", mailSender);

		service.sendEmailsInformation();

		// Then
		verify(mailSender, times(1)).send(Mockito.isA(MimeMessagePreparator.class));
	}

	@Test
	public void EmailsInformationDemandeJob_TwoEmailToSend() throws AbsEmailsInformationException, DaoException {

		List<Integer> listApprobateur = new ArrayList<Integer>();
		listApprobateur.add(9);
		List<Integer> listViseur = new ArrayList<Integer>();
		listViseur.add(9);

		EmailInfoDto dto = new EmailInfoDto();
		dto.setListApprobateurs(listApprobateur);
		dto.setListViseurs(listViseur);

		// Given
		IAbsWSConsumer absWSConsumer = Mockito.mock(IAbsWSConsumer.class);
		when(absWSConsumer.getListIdDestinatairesEmailInfo()).thenReturn(dto);

		JavaMailSender mailSender = Mockito.mock(JavaMailSender.class);
		Mockito.doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) {
				return true;
			}
		}).when(mailSender).send(Mockito.isA(MimeMessagePreparator.class));

		IRadiWSConsumer radiWSConsumer = Mockito.mock(IRadiWSConsumer.class);
		when(radiWSConsumer.retrieveAgentFromLdapFromMatricule("90")).thenReturn(new LightUser());

		EmailsInformationDemandeJob service = new EmailsInformationDemandeJob();
		ReflectionTestUtils.setField(service, "helper", helperMock);
		ReflectionTestUtils.setField(service, "absWSConsumer", absWSConsumer);
		ReflectionTestUtils.setField(service, "radiWSConsumer", radiWSConsumer);
		ReflectionTestUtils.setField(service, "numberOfTries", 2);
		ReflectionTestUtils.setField(service, "mailSender", mailSender);

		service.sendEmailsInformation();

		// Then
		verify(mailSender, times(2)).send(Mockito.isA(MimeMessagePreparator.class));
	}
}
