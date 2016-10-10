package nc.noumea.mairie.sirh.job;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import nc.noumea.mairie.ptg.dao.IPointagesDao;
import nc.noumea.mairie.ptg.domain.ExportEtatsPayeurTask;
import nc.noumea.mairie.sirh.service.IDownloadDocumentService;
import nc.noumea.mairie.sirh.tools.IIncidentLoggerService;

import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.quartz.JobExecutionException;
import org.springframework.test.util.ReflectionTestUtils;

public class PointagesExportEtatsPayeurJobTest {

	@Test
	public void exportEtatsPayeurTask_NoTaskToExport_Return() throws JobExecutionException {
		
		// Given
		IPointagesDao pdao = Mockito.mock(IPointagesDao.class);
		Mockito.when(pdao.getNextExportEtatsPayeurTask()).thenReturn(null);
		
		PointagesExportEtatsPayeurJob job = new PointagesExportEtatsPayeurJob();
		ReflectionTestUtils.setField(job, "pointagesDao", pdao);
		
		// When
		job.executeInternal(null);
		
		// Then
		Mockito.verify(pdao, Mockito.times(1)).beginTransaction();
		Mockito.verify(pdao, Mockito.times(1)).rollBackTransaction();
		Mockito.verify(pdao, Mockito.never()).commitTransaction();
		
	}
	
	@Test
	public void exportEtatsPayeurTask_EverythingOK() throws Exception {
		
		// Given
		ExportEtatsPayeurTask t = new ExportEtatsPayeurTask();
		t.setIdExportEtatsPayeurTask(99);
		IPointagesDao pdao = Mockito.mock(IPointagesDao.class);
		Mockito.when(pdao.getNextExportEtatsPayeurTask()).thenReturn(t);
		
		IDownloadDocumentService dd = Mockito.mock(IDownloadDocumentService.class);
		
		PointagesExportEtatsPayeurJob job = new PointagesExportEtatsPayeurJob();
		ReflectionTestUtils.setField(job, "SIRH_PTG_WS_Base_URL", "base");
		ReflectionTestUtils.setField(job, "pointagesDao", pdao);
		ReflectionTestUtils.setField(job, "downloadDocumentService", dd);
		
		// When
		job.executeInternal(null);
		
		// Then
		assertEquals("OK", t.getTaskStatus());
		assertNotNull(t.getDateExport());
		Mockito.verify(pdao, Mockito.times(1)).beginTransaction();
		Mockito.verify(pdao, Mockito.times(1)).commitTransaction();
		Mockito.verify(pdao, Mockito.never()).rollBackTransaction();
		Mockito.verify(dd, Mockito.times(1)).downloadDocumentAs(String.class, "baseetatsPayeur/startExportTask?idExportEtatsPayeurTask=99", null);
		Mockito.verify(dd, Mockito.times(1)).downloadDocumentAs(String.class, "baseetatsPayeur/finishExportTask?idExportEtatsPayeurTask=99", null);
	}
	
	@Test
	public void exportEtatsPayeurTask_WSCallfails_SetMessage() throws Exception {
		
		// Given
		ExportEtatsPayeurTask t = new ExportEtatsPayeurTask();
		t.setIdExportEtatsPayeurTask(99);
		IPointagesDao pdao = Mockito.mock(IPointagesDao.class);
		Mockito.when(pdao.getNextExportEtatsPayeurTask()).thenReturn(t);
		
		IDownloadDocumentService dd = Mockito.mock(IDownloadDocumentService.class);
		Mockito.doThrow(new Exception("MSG")).when(dd).downloadDocumentAs(String.class, "baseetatsPayeur/startExportTask?idExportEtatsPayeurTask=99", null);
		
		IIncidentLoggerService incidentLoggerService = Mockito.mock(IIncidentLoggerService.class);
			Mockito.doAnswer(new Answer<Object>() {
				public Object answer(InvocationOnMock invocation) { 
					return true;
				}
			}).when(incidentLoggerService).logIncident(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.any(Exception.class));
		
		PointagesExportEtatsPayeurJob job = new PointagesExportEtatsPayeurJob();
		ReflectionTestUtils.setField(job, "SIRH_PTG_WS_Base_URL", "base");
		ReflectionTestUtils.setField(job, "pointagesDao", pdao);
		ReflectionTestUtils.setField(job, "downloadDocumentService", dd);
		ReflectionTestUtils.setField(job, "incidentLoggerService", incidentLoggerService);
		
		// When
		job.executeInternal(null);
		
		// Then
		assertEquals("Erreur: MSG", t.getTaskStatus());
		assertNotNull(t.getDateExport());
		Mockito.verify(pdao, Mockito.times(1)).beginTransaction();
		Mockito.verify(pdao, Mockito.times(1)).commitTransaction();
		Mockito.verify(pdao, Mockito.never()).rollBackTransaction();
		Mockito.verify(dd, Mockito.times(1)).downloadDocumentAs(String.class, "baseetatsPayeur/startExportTask?idExportEtatsPayeurTask=99", null);
		Mockito.verify(dd, Mockito.never()).downloadDocumentAs(String.class, "baseetatsPayeur/finishExportTask?idExportEtatsPayeurTask=99", null);
	}
}
