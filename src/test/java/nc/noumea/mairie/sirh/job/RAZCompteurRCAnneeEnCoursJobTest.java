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

public class RAZCompteurRCAnneeEnCoursJobTest {

	@Test
	public void testRAZCompteurRCAnneeEnCoursJob_noRAZ() throws Exception {
		
		List<Integer> listIdCompteur = new ArrayList<Integer>();
		
		IAbsWSConsumer absWSConsumer = Mockito.mock(IAbsWSConsumer.class);
			Mockito.when(absWSConsumer.getListeCompteurAnneeEnCours()).thenReturn(listIdCompteur);
		
		RAZCompteurRCAnneeEnCoursJob job = new RAZCompteurRCAnneeEnCoursJob();
			ReflectionTestUtils.setField(job, "absWSConsumer", absWSConsumer);
		
		job.executeInternal(null);
		
		Mockito.verify(absWSConsumer, Mockito.times(0)).resetCompteurAnneeEnCours(Mockito.anyInt());
	}
	
	@Test
	public void testRAZCompteurRCAnneeEnCoursJob_1RAZ() throws Exception {
		
		List<Integer> listIdCompteur = new ArrayList<Integer>();
			listIdCompteur.add(1);
		
		IAbsWSConsumer absWSConsumer = Mockito.mock(IAbsWSConsumer.class);
			Mockito.when(absWSConsumer.getListeCompteurAnneeEnCours()).thenReturn(listIdCompteur);
		
		RAZCompteurRCAnneeEnCoursJob job = new RAZCompteurRCAnneeEnCoursJob();
			ReflectionTestUtils.setField(job, "absWSConsumer", absWSConsumer);
		
		job.executeInternal(null);
		
		Mockito.verify(absWSConsumer, Mockito.times(1)).resetCompteurAnneeEnCours(Mockito.anyInt());
	}
	
	@Test
	public void testRAZCompteurRCAnneeEnCoursJob_exception() throws Exception {
		
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
		}).when(incidentLoggerService).logIncident(Mockito.anyString(), Mockito.anyString(), Mockito.any(Exception.class));
		
		IAbsWSConsumer absWSConsumer = Mockito.mock(IAbsWSConsumer.class);
		Mockito.doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) throws Exception { 
				Exception e = new Exception("erreur");
				return e;
			}
		}).when(absWSConsumer).getListeCompteurAnneeEnCours();
		
		RAZCompteurRCAnneeEnCoursJob job = new RAZCompteurRCAnneeEnCoursJob();
			ReflectionTestUtils.setField(job, "logger", logger);
			ReflectionTestUtils.setField(job, "incidentLoggerService", incidentLoggerService);
			ReflectionTestUtils.setField(job, "absWSConsumer", absWSConsumer);
		
		job.executeInternal(null);
		
		Mockito.verify(incidentLoggerService, Mockito.times(1)).logIncident(Mockito.anyString(), Mockito.anyString(), Mockito.any(Exception.class));
	}
}
