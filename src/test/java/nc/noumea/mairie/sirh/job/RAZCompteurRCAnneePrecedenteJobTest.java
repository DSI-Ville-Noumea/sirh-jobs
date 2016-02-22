package nc.noumea.mairie.sirh.job;

import java.util.ArrayList;
import java.util.List;

import nc.noumea.mairie.sirh.tools.IIncidentLoggerService;
import nc.noumea.mairie.sirh.ws.IAbsWSConsumer;

import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.springframework.test.util.ReflectionTestUtils;

public class RAZCompteurRCAnneePrecedenteJobTest {

	@Test
	public void testRAZCompteurRCAnneePrecedenteJob_noRAZ() throws Exception {
		
		List<Integer> listIdCompteur = new ArrayList<Integer>();
		
		IAbsWSConsumer absWSConsumer = Mockito.mock(IAbsWSConsumer.class);
			Mockito.when(absWSConsumer.getListeCompteurAnneePrecedente()).thenReturn(listIdCompteur);
		
		RAZCompteurRCAnneePrecedenteJob job = new RAZCompteurRCAnneePrecedenteJob();
			ReflectionTestUtils.setField(job, "absWSConsumer", absWSConsumer);
		
		job.executeInternal(null);
		
		Mockito.verify(absWSConsumer, Mockito.times(0)).resetCompteurAnneePrecedente(Mockito.anyInt());
	}
	
	@Test
	public void testRAZCompteurRCAnneePrecedenteJob_1RAZ() throws Exception {
		
		List<Integer> listIdCompteur = new ArrayList<Integer>();
			listIdCompteur.add(1);
		
		IAbsWSConsumer absWSConsumer = Mockito.mock(IAbsWSConsumer.class);
			Mockito.when(absWSConsumer.getListeCompteurAnneePrecedente()).thenReturn(listIdCompteur);
		
		RAZCompteurRCAnneePrecedenteJob job = new RAZCompteurRCAnneePrecedenteJob();
			ReflectionTestUtils.setField(job, "absWSConsumer", absWSConsumer);
		
		job.executeInternal(null);
		
		Mockito.verify(absWSConsumer, Mockito.times(1)).resetCompteurAnneePrecedente(Mockito.anyInt());
	}
	
	@Test
	public void testRAZCompteurRCAnneePrecedenteJob_exception() throws Exception {
		
		List<Integer> listIdCompteur = new ArrayList<Integer>();
			listIdCompteur.add(1);
			
		Logger logger = Mockito.mock(Logger.class);
		Mockito.doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) { 
				return true;
			}
		}).when(logger).error(Mockito.anyString(), Mockito.any(Exception.class));
		
		IIncidentLoggerService incidentLoggerService = Mockito.mock(IIncidentLoggerService.class);
		Mockito.doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) { 
				return true;
			}
		}).when(incidentLoggerService).logIncident(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.any(Exception.class));
		
		IAbsWSConsumer absWSConsumer = Mockito.mock(IAbsWSConsumer.class);
		Mockito.doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) throws Exception { 
				Exception e = new Exception("erreur");
				return e;
			}
		}).when(absWSConsumer).getListeCompteurAnneePrecedente();
		
		RAZCompteurRCAnneePrecedenteJob job = new RAZCompteurRCAnneePrecedenteJob();
			ReflectionTestUtils.setField(job, "logger", logger);
			ReflectionTestUtils.setField(job, "incidentLoggerService", incidentLoggerService);
			ReflectionTestUtils.setField(job, "absWSConsumer", absWSConsumer);
		
		job.executeInternal(null);
		
		Mockito.verify(incidentLoggerService, Mockito.times(1)).logIncident(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.any(Exception.class));
	}
}
