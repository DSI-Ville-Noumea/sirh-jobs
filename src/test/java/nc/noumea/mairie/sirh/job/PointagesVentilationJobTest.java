package nc.noumea.mairie.sirh.job;

import static org.junit.Assert.assertEquals;
import nc.noumea.mairie.ptg.dao.IPointagesDao;
import nc.noumea.mairie.ptg.domain.VentilTask;
import nc.noumea.mairie.sirh.service.IDownloadDocumentService;
import nc.noumea.mairie.sirh.tools.IIncidentLoggerService;

import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.test.util.ReflectionTestUtils;

public class PointagesVentilationJobTest {

	@Test
	public void executeInternal_OneVentilTask_success() throws Exception {
		
		// Given
		VentilTask t1 = new VentilTask();
		t1.setIdVentilTask(12);
		IPointagesDao pDao = Mockito.mock(IPointagesDao.class);
		Mockito.when(pDao.getNextVentilTask()).thenReturn(t1).thenReturn(null);
		
		IDownloadDocumentService ddS = Mockito.mock(IDownloadDocumentService.class);
		
		PointagesVentilationJob job = new PointagesVentilationJob();
		ReflectionTestUtils.setField(job, "pointagesDao", pDao);
		ReflectionTestUtils.setField(job, "SIRH_PTG_WS_Base_URL", "the");
		ReflectionTestUtils.setField(job, "SIRH_PTG_WS_VentilationTaskUrl", "Url");
		ReflectionTestUtils.setField(job, "downloadDocumentService", ddS);
		
		// When
		job.executeInternal(null);
		
		// Then
		assertEquals("OK", t1.getTaskStatus());
		Mockito.verify(pDao, Mockito.times(2)).beginTransaction();
		Mockito.verify(pDao, Mockito.times(1)).commitTransaction();
		Mockito.verify(pDao, Mockito.times(1)).rollBackTransaction();
		Mockito.verify(ddS, Mockito.times(1)).downloadDocumentAs(String.class, "theUrl12", null);
	}
	
	@Test
	public void executeInternal_OneVentilTask_failure() throws Exception {
		
		// Given
		VentilTask t1 = new VentilTask();
		t1.setIdVentilTask(12);
		IPointagesDao pDao = Mockito.mock(IPointagesDao.class);
		Mockito.when(pDao.getNextVentilTask()).thenReturn(t1).thenReturn(null);
		
		IDownloadDocumentService ddS = Mockito.mock(IDownloadDocumentService.class);
		Mockito.doThrow(new Exception("fake exception message")).when(ddS).downloadDocumentAs(String.class, "theUrl12", null);
		
		IIncidentLoggerService incidentLoggerService = Mockito.mock(IIncidentLoggerService.class);
			Mockito.doAnswer(new Answer<Object>() {
				public Object answer(InvocationOnMock invocation) { 
					return true;
				}
			}).when(incidentLoggerService).logIncident(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.any(Exception.class));
		
		PointagesVentilationJob job = new PointagesVentilationJob();
		ReflectionTestUtils.setField(job, "pointagesDao", pDao);
		ReflectionTestUtils.setField(job, "SIRH_PTG_WS_Base_URL", "the");
		ReflectionTestUtils.setField(job, "SIRH_PTG_WS_VentilationTaskUrl", "Url");
		ReflectionTestUtils.setField(job, "downloadDocumentService", ddS);
		ReflectionTestUtils.setField(job, "incidentLoggerService", incidentLoggerService);
		
		// When
		job.executeInternal(null);
		
		// Then
		assertEquals("Erreur: fake exception message", t1.getTaskStatus());
		Mockito.verify(pDao, Mockito.times(2)).beginTransaction();
		Mockito.verify(pDao, Mockito.times(1)).commitTransaction();
		Mockito.verify(pDao, Mockito.times(1)).rollBackTransaction();
		Mockito.verify(ddS, Mockito.times(1)).downloadDocumentAs(String.class, "theUrl12", null);
	}
}
