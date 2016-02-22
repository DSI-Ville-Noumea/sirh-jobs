package nc.noumea.mairie.sirh.job;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nc.noumea.mairie.abs.dao.IAbsencesDao;
import nc.noumea.mairie.sirh.tools.Helper;
import nc.noumea.mairie.sirh.tools.IIncidentLoggerService;
import nc.noumea.mairie.sirh.tools.VoRedmineIncidentLogger;
import nc.noumea.mairie.sirh.ws.IAbsWSConsumer;
import nc.noumea.mairie.sirh.ws.ISirhWSConsumer;
import nc.noumea.mairie.sirh.ws.ReturnMessageDto;

import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.quartz.JobExecutionException;
import org.springframework.test.util.ReflectionTestUtils;

public class AbsCAAlimentationAutoCompteursJobTest {

	@Test
	public void AbsCAAlimentationAutoCompteursJobTest_erreurListAgents() throws JobExecutionException {

		ISirhWSConsumer sirhWSConsumer = Mockito.mock(ISirhWSConsumer.class);
		IAbsencesDao absencesDao = Mockito.mock(IAbsencesDao.class);

		Mockito.doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) {
				return new Exception();
			}
		}).when(sirhWSConsumer)
				.getListAgentPourAlimAutoCompteursCongesAnnuels(Mockito.any(Date.class), Mockito.any(Date.class));

		IIncidentLoggerService incidentLoggerService = Mockito.mock(IIncidentLoggerService.class);

		AbsCAAlimentationAutoCompteursJob job = new AbsCAAlimentationAutoCompteursJob();
		ReflectionTestUtils.setField(job, "sirhWSConsumer", sirhWSConsumer);
		ReflectionTestUtils.setField(job, "incidentLoggerService", incidentLoggerService);
		ReflectionTestUtils.setField(job, "absencesDao", absencesDao);

		job.executeInternal(null);

		verify(incidentLoggerService, times(1)).logIncident(Mockito.anyString(), Mockito.anyString(),
				 Mockito.anyString(), Mockito.any(Exception.class));
	}

	@Test
	public void AbsCAAlimentationAutoCompteursJobTest_erreurAlimAutoException() throws JobExecutionException {

		Helper helper = Mockito.mock(Helper.class);
		Mockito.when(helper.getFirstDayOfPreviousMonth()).thenReturn(new Date());
		Mockito.when(helper.getLastDayOfPreviousMonth()).thenReturn(new Date());
		Mockito.when(helper.getIdAgentWithNomatr(5138)).thenReturn("9005138");

		ISirhWSConsumer sirhWSConsumer = Mockito.mock(ISirhWSConsumer.class);
		Mockito.doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) {
				List<Integer> listAgent = new ArrayList<Integer>();
				listAgent.add(5138);
				return listAgent;
			}
		}).when(sirhWSConsumer)
				.getListAgentPourAlimAutoCompteursCongesAnnuels(Mockito.any(Date.class), Mockito.any(Date.class));

		IAbsWSConsumer absWSConsumer = Mockito.mock(IAbsWSConsumer.class);
		Mockito.doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) {
				return new Exception();
			}
		}).when(absWSConsumer)
				.alimentationAutoCongesAnnuels(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class));

		IAbsencesDao absencesDao = Mockito.mock(IAbsencesDao.class);
		IIncidentLoggerService incidentLoggerService = Mockito.mock(IIncidentLoggerService.class);

		AbsCAAlimentationAutoCompteursJob job = new AbsCAAlimentationAutoCompteursJob();
		ReflectionTestUtils.setField(job, "sirhWSConsumer", sirhWSConsumer);
		ReflectionTestUtils.setField(job, "incidentLoggerService", incidentLoggerService);
		ReflectionTestUtils.setField(job, "absWSConsumer", absWSConsumer);
		ReflectionTestUtils.setField(job, "helper", helper);
		ReflectionTestUtils.setField(job, "absencesDao", absencesDao);

		job.executeInternal(null);

		verify(incidentLoggerService, times(1)).logIncident(Mockito.isA(VoRedmineIncidentLogger.class));
	}

	@Test
	public void AbsCAAlimentationAutoCompteursJobTest_erreurReturnMessageDto() throws JobExecutionException {

		Helper helper = Mockito.mock(Helper.class);
		Mockito.when(helper.getFirstDayOfPreviousMonth()).thenReturn(new Date());
		Mockito.when(helper.getLastDayOfPreviousMonth()).thenReturn(new Date());
		Mockito.when(helper.getIdAgentWithNomatr(5138)).thenReturn("9005138");

		ISirhWSConsumer sirhWSConsumer = Mockito.mock(ISirhWSConsumer.class);
		Mockito.doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) {
				List<Integer> listAgent = new ArrayList<Integer>();
				listAgent.add(5138);
				return listAgent;
			}
		}).when(sirhWSConsumer)
				.getListAgentPourAlimAutoCompteursCongesAnnuels(Mockito.any(Date.class), Mockito.any(Date.class));

		IAbsWSConsumer absWSConsumer = Mockito.mock(IAbsWSConsumer.class);
		Mockito.doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) {
				ReturnMessageDto result = new ReturnMessageDto();
				result.getErrors().add("error");
				return result;
			}
		}).when(absWSConsumer)
				.alimentationAutoCongesAnnuels(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class));
		Mockito.doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) {
				ReturnMessageDto result = new ReturnMessageDto();
				return result;
			}
		}).when(absWSConsumer).miseAJourSpSoldAgent(Mockito.anyString());
		Mockito.doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) {
				ReturnMessageDto result = new ReturnMessageDto();
				return result;
			}
		}).when(absWSConsumer).miseAJourSpSorcAgent(Mockito.anyString());

		IIncidentLoggerService incidentLoggerService = Mockito.mock(IIncidentLoggerService.class);
		IAbsencesDao absencesDao = Mockito.mock(IAbsencesDao.class);

		AbsCAAlimentationAutoCompteursJob job = new AbsCAAlimentationAutoCompteursJob();
		ReflectionTestUtils.setField(job, "sirhWSConsumer", sirhWSConsumer);
		ReflectionTestUtils.setField(job, "incidentLoggerService", incidentLoggerService);
		ReflectionTestUtils.setField(job, "absWSConsumer", absWSConsumer);
		ReflectionTestUtils.setField(job, "absencesDao", absencesDao);
		ReflectionTestUtils.setField(job, "helper", helper);

		job.executeInternal(null);

		Mockito.verify(absencesDao, Mockito.times(1)).beginTransaction();
		verify(absencesDao, times(1)).commitTransaction();
		verify(incidentLoggerService, times(1)).logIncident(Mockito.isA(VoRedmineIncidentLogger.class));
	}

	@Test
	public void AbsCAAlimentationAutoCompteursJobTest_2erreursReturnMessageDto() throws JobExecutionException {

		Helper helper = Mockito.mock(Helper.class);
		Mockito.when(helper.getFirstDayOfPreviousMonth()).thenReturn(new Date());
		Mockito.when(helper.getLastDayOfPreviousMonth()).thenReturn(new Date());
		Mockito.when(helper.getIdAgentWithNomatr(5138)).thenReturn("9005138");
		Mockito.when(helper.getIdAgentWithNomatr(2990)).thenReturn("9002990");

		ISirhWSConsumer sirhWSConsumer = Mockito.mock(ISirhWSConsumer.class);
		Mockito.doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) {
				List<Integer> listAgent = new ArrayList<Integer>();
				listAgent.add(5138);
				listAgent.add(2990);
				return listAgent;
			}
		}).when(sirhWSConsumer)
				.getListAgentPourAlimAutoCompteursCongesAnnuels(Mockito.any(Date.class), Mockito.any(Date.class));

		IAbsWSConsumer absWSConsumer = Mockito.mock(IAbsWSConsumer.class);
		Mockito.doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) {
				ReturnMessageDto result = new ReturnMessageDto();
				result.getErrors().add("error");
				return result;
			}
		}).when(absWSConsumer)
				.alimentationAutoCongesAnnuels(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class));
		Mockito.doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) {
				ReturnMessageDto result = new ReturnMessageDto();
				return result;
			}
		}).when(absWSConsumer).miseAJourSpSoldAgent(Mockito.anyString());
		Mockito.doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) {
				ReturnMessageDto result = new ReturnMessageDto();
				return result;
			}
		}).when(absWSConsumer).miseAJourSpSorcAgent(Mockito.anyString());

		IIncidentLoggerService incidentLoggerService = Mockito.mock(IIncidentLoggerService.class);
		IAbsencesDao absencesDao = Mockito.mock(IAbsencesDao.class);

		AbsCAAlimentationAutoCompteursJob job = new AbsCAAlimentationAutoCompteursJob();
		ReflectionTestUtils.setField(job, "sirhWSConsumer", sirhWSConsumer);
		ReflectionTestUtils.setField(job, "incidentLoggerService", incidentLoggerService);
		ReflectionTestUtils.setField(job, "absWSConsumer", absWSConsumer);
		ReflectionTestUtils.setField(job, "absencesDao", absencesDao);
		ReflectionTestUtils.setField(job, "helper", helper);

		job.executeInternal(null);

		Mockito.verify(absencesDao, Mockito.times(2)).beginTransaction();
		verify(absencesDao, times(2)).commitTransaction();
		verify(incidentLoggerService, times(1)).logIncident(Mockito.isA(VoRedmineIncidentLogger.class));
	}

	@Test
	public void AbsCAAlimentationAutoCompteursJobTest_ok() throws JobExecutionException {

		Helper helper = Mockito.mock(Helper.class);
		Mockito.when(helper.getFirstDayOfPreviousMonth()).thenReturn(new Date());
		Mockito.when(helper.getLastDayOfPreviousMonth()).thenReturn(new Date());
		Mockito.when(helper.getIdAgentWithNomatr(5138)).thenReturn("9005138");
		Mockito.when(helper.getIdAgentWithNomatr(2990)).thenReturn("9002990");

		ISirhWSConsumer sirhWSConsumer = Mockito.mock(ISirhWSConsumer.class);
		Mockito.doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) {
				List<Integer> listAgent = new ArrayList<Integer>();
				listAgent.add(5138);
				listAgent.add(2990);
				return listAgent;
			}
		}).when(sirhWSConsumer)
				.getListAgentPourAlimAutoCompteursCongesAnnuels(Mockito.any(Date.class), Mockito.any(Date.class));

		IAbsWSConsumer absWSConsumer = Mockito.mock(IAbsWSConsumer.class);
		Mockito.doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) {
				ReturnMessageDto result = new ReturnMessageDto();
				return result;
			}
		}).when(absWSConsumer)
				.alimentationAutoCongesAnnuels(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class));
		Mockito.doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) {
				ReturnMessageDto result = new ReturnMessageDto();
				return result;
			}
		}).when(absWSConsumer).miseAJourSpSoldAgent(Mockito.anyString());
		Mockito.doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) {
				ReturnMessageDto result = new ReturnMessageDto();
				return result;
			}
		}).when(absWSConsumer).miseAJourSpSorcAgent(Mockito.anyString());

		IIncidentLoggerService incidentLoggerService = Mockito.mock(IIncidentLoggerService.class);
		IAbsencesDao absencesDao = Mockito.mock(IAbsencesDao.class);

		AbsCAAlimentationAutoCompteursJob job = new AbsCAAlimentationAutoCompteursJob();
		ReflectionTestUtils.setField(job, "sirhWSConsumer", sirhWSConsumer);
		ReflectionTestUtils.setField(job, "incidentLoggerService", incidentLoggerService);
		ReflectionTestUtils.setField(job, "absWSConsumer", absWSConsumer);
		ReflectionTestUtils.setField(job, "absencesDao", absencesDao);
		ReflectionTestUtils.setField(job, "helper", helper);

		job.executeInternal(null);

		Mockito.verify(absencesDao, Mockito.times(2)).beginTransaction();
		verify(absencesDao, times(2)).commitTransaction();
		verify(incidentLoggerService, times(0)).logIncident(Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString(), Mockito.any(Exception.class));
	}
}
