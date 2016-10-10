package nc.noumea.mairie.sirh.job;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import nc.noumea.mairie.ptg.dao.IPointagesDao;
import nc.noumea.mairie.ptg.domain.ReposCompTask;
import nc.noumea.mairie.sirh.service.IDownloadDocumentService;
import nc.noumea.mairie.sirh.tools.Helper;
import nc.noumea.mairie.sirh.tools.IIncidentLoggerService;

import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.test.util.ReflectionTestUtils;

public class PointagesReposCompJobTest {

	@Test
	public void executeInternal_NoTasks_return() throws Exception {
	
		// Given
		IPointagesDao pDao = Mockito.mock(IPointagesDao.class);
		Mockito.when(pDao.getNextReposCompTask()).thenReturn(null);
		
		PointagesReposCompJob job = new PointagesReposCompJob();
		ReflectionTestUtils.setField(job, "pointagesDao", pDao);
		
		// When
		job.executeInternal(null);
		
		// Then
		Mockito.verify(pDao, Mockito.times(1)).beginTransaction();
		Mockito.verify(pDao, Mockito.times(1)).rollBackTransaction();
		Mockito.verify(pDao, Mockito.never()).commitTransaction();
	}
	
	@Test
	public void executeInternal_1Task_ProcessIt() throws Exception {
	
		// Given
		ReposCompTask t = new ReposCompTask();
		t.setIdRcTask(456);
		t.setIdAgent(9004880);
		
		Date curDate = new Date();
		
		IPointagesDao pDao = Mockito.mock(IPointagesDao.class);
		Mockito.when(pDao.getNextReposCompTask()).thenReturn(t).thenReturn(null);
		
		IDownloadDocumentService dS = Mockito.mock(IDownloadDocumentService.class);
		
		Helper h = Mockito.mock(Helper.class);
		Mockito.when(h.getCurrentDate()).thenReturn(curDate);
		
		PointagesReposCompJob job = new PointagesReposCompJob();
		ReflectionTestUtils.setField(job, "pointagesDao", pDao);
		ReflectionTestUtils.setField(job, "downloadDocumentService", dS);
		ReflectionTestUtils.setField(job, "helper", h);
		ReflectionTestUtils.setField(job, "SIRH_PTG_WS_Base_URL", "U-");
		
		// When
		job.executeInternal(null);
		
		// Then
		Mockito.verify(pDao, Mockito.times(2)).beginTransaction();
		Mockito.verify(pDao, Mockito.times(1)).rollBackTransaction();
		Mockito.verify(pDao, Mockito.times(1)).commitTransaction();
		
		Mockito.verify(dS, Mockito.times(1)).downloadDocumentAs(String.class, "U-reposcomp/startReposCompTask?idReposCompTask=456", null);
		
		assertEquals(curDate, t.getDateCalcul());
		assertEquals("OK", t.getTaskStatus());
	}
	
	@Test
	public void executeInternal_1Task_Fails_LogError() throws Exception {
	
		// Given
		ReposCompTask t = new ReposCompTask();
		t.setIdRcTask(456);
		t.setIdAgent(9004880);
		
		Date curDate = new Date();
		
		IPointagesDao pDao = Mockito.mock(IPointagesDao.class);
		Mockito.when(pDao.getNextReposCompTask()).thenReturn(t).thenReturn(null);
		
		IDownloadDocumentService dS = Mockito.mock(IDownloadDocumentService.class);
		Mockito.doThrow(new Exception("message")).when(dS).downloadDocumentAs(String.class, "U-reposcomp/startReposCompTask?idReposCompTask=456", null);
		
		Helper h = Mockito.mock(Helper.class);
		Mockito.when(h.getCurrentDate()).thenReturn(curDate);
		
		IIncidentLoggerService incidentLoggerService = Mockito.mock(IIncidentLoggerService.class);
			Mockito.doAnswer(new Answer<Object>() {
				public Object answer(InvocationOnMock invocation) { 
					return true;
				}
			}).when(incidentLoggerService).logIncident(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.any(Exception.class));
		
		PointagesReposCompJob job = new PointagesReposCompJob();
		ReflectionTestUtils.setField(job, "pointagesDao", pDao);
		ReflectionTestUtils.setField(job, "downloadDocumentService", dS);
		ReflectionTestUtils.setField(job, "helper", h);
		ReflectionTestUtils.setField(job, "SIRH_PTG_WS_Base_URL", "U-");
		ReflectionTestUtils.setField(job, "incidentLoggerService", incidentLoggerService);
		
		// When
		job.executeInternal(null);
		
		// Then
		Mockito.verify(pDao, Mockito.times(2)).beginTransaction();
		Mockito.verify(pDao, Mockito.times(1)).rollBackTransaction();
		Mockito.verify(pDao, Mockito.times(1)).commitTransaction();
		
		Mockito.verify(dS, Mockito.times(1)).downloadDocumentAs(String.class, "U-reposcomp/startReposCompTask?idReposCompTask=456", null);
		
		assertEquals(curDate, t.getDateCalcul());
		assertEquals("Erreur: message", t.getTaskStatus());
	}
}
