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
import nc.noumea.mairie.sirh.ws.IPtgWSConsumer;
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

public class EmailsInformationPointagesJobTest {

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
	public void EmailsInformationPointagesJob_NoEmailToSend_noDestinataire() throws PointagesEmailsInformationException {

		EmailInfoDto dto = new EmailInfoDto();
		dto.setListApprobateurs(new ArrayList<Integer>());
		// Given
		IPtgWSConsumer ptgWSConsumer = Mockito.mock(IPtgWSConsumer.class);
		when(ptgWSConsumer.getListIdDestinatairesEmailInfo()).thenReturn(dto);

		JavaMailSender mailSender = Mockito.mock(JavaMailSender.class);
		Mockito.doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) {
				return true;
			}
		}).when(mailSender).send(Mockito.isA(MimeMessagePreparator.class));

		EmailsInformationPointagesJob service = new EmailsInformationPointagesJob();
		ReflectionTestUtils.setField(service, "helper", helperMock);
		ReflectionTestUtils.setField(service, "ptgWSConsumer", ptgWSConsumer);
		ReflectionTestUtils.setField(service, "mailSender", mailSender);

		// When
		service.sendEmailsInformation();

		// Then
		verify(mailSender, times(0)).send(Mockito.isA(MimeMessagePreparator.class));
	}

	@Test
	public void EmailsInformationPointagesJob_NoEmailToSend_oneApprobateur_noExistintLDAP()
			throws PointagesEmailsInformationException, DaoException {

		List<Integer> listApprobateur = new ArrayList<Integer>();
		listApprobateur.add(9);

		EmailInfoDto dto = new EmailInfoDto();
		dto.setListApprobateurs(listApprobateur);

		// Given
		IPtgWSConsumer ptgWSConsumer = Mockito.mock(IPtgWSConsumer.class);
		when(ptgWSConsumer.getListIdDestinatairesEmailInfo()).thenReturn(dto);

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
				.logIncident(Mockito.anyString(), Mockito.anyString(), Mockito.any(Exception.class));

		EmailsInformationPointagesJob service = new EmailsInformationPointagesJob();
		ReflectionTestUtils.setField(service, "helper", helperMock);
		ReflectionTestUtils.setField(service, "ptgWSConsumer", ptgWSConsumer);
		ReflectionTestUtils.setField(service, "radiWSConsumer", radiWSConsumer);
		ReflectionTestUtils.setField(service, "numberOfTries", 2);
		ReflectionTestUtils.setField(service, "mailSender", mailSender);
		ReflectionTestUtils.setField(service, "incidentLoggerService", incidentLoggerService);

		service.sendEmailsInformation();

		// Then
		verify(mailSender, times(0)).send(Mockito.isA(MimeMessagePreparator.class));
	}

	@Test
	public void EmailsInformationPointagesJob_OneEmailToSend_OneApprobateur()
			throws PointagesEmailsInformationException, DaoException {

		List<Integer> listApprobateur = new ArrayList<Integer>();
		listApprobateur.add(9);

		EmailInfoDto dto = new EmailInfoDto();
		dto.setListApprobateurs(listApprobateur);

		// Given
		IPtgWSConsumer ptgWSConsumer = Mockito.mock(IPtgWSConsumer.class);
		when(ptgWSConsumer.getListIdDestinatairesEmailInfo()).thenReturn(dto);

		JavaMailSender mailSender = Mockito.mock(JavaMailSender.class);
		Mockito.doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) {
				return true;
			}
		}).when(mailSender).send(Mockito.isA(MimeMessagePreparator.class));

		IRadiWSConsumer radiWSConsumer = Mockito.mock(IRadiWSConsumer.class);
		when(radiWSConsumer.retrieveAgentFromLdapFromMatricule("90")).thenReturn(new LightUser());

		EmailsInformationPointagesJob service = new EmailsInformationPointagesJob();
		ReflectionTestUtils.setField(service, "helper", helperMock);
		ReflectionTestUtils.setField(service, "ptgWSConsumer", ptgWSConsumer);
		ReflectionTestUtils.setField(service, "radiWSConsumer", radiWSConsumer);
		ReflectionTestUtils.setField(service, "numberOfTries", 2);
		ReflectionTestUtils.setField(service, "mailSender", mailSender);

		service.sendEmailsInformation();

		// Then
		verify(mailSender, times(1)).send(Mockito.isA(MimeMessagePreparator.class));
	}

	@Test
	public void EmailsInformationPointagesJob_TwoEmailToSend() throws PointagesEmailsInformationException, DaoException {

		List<Integer> listApprobateur = new ArrayList<Integer>();
		listApprobateur.add(9);

		EmailInfoDto dto = new EmailInfoDto();
		dto.setListApprobateurs(listApprobateur);

		// Given
		IPtgWSConsumer ptgWSConsumer = Mockito.mock(IPtgWSConsumer.class);
		when(ptgWSConsumer.getListIdDestinatairesEmailInfo()).thenReturn(dto);

		JavaMailSender mailSender = Mockito.mock(JavaMailSender.class);
		Mockito.doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) {
				return true;
			}
		}).when(mailSender).send(Mockito.isA(MimeMessagePreparator.class));

		IRadiWSConsumer radiWSConsumer = Mockito.mock(IRadiWSConsumer.class);
		when(radiWSConsumer.retrieveAgentFromLdapFromMatricule("90")).thenReturn(new LightUser());

		EmailsInformationPointagesJob service = new EmailsInformationPointagesJob();
		ReflectionTestUtils.setField(service, "helper", helperMock);
		ReflectionTestUtils.setField(service, "ptgWSConsumer", ptgWSConsumer);
		ReflectionTestUtils.setField(service, "radiWSConsumer", radiWSConsumer);
		ReflectionTestUtils.setField(service, "numberOfTries", 2);
		ReflectionTestUtils.setField(service, "mailSender", mailSender);

		service.sendEmailsInformation();

		// Then
		verify(mailSender, times(1)).send(Mockito.isA(MimeMessagePreparator.class));
	}
}
