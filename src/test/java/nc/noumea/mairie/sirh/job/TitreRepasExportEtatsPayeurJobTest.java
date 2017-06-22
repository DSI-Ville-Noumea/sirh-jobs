package nc.noumea.mairie.sirh.job;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.quartz.JobExecutionException;
import org.springframework.test.util.ReflectionTestUtils;

import com.sun.jersey.api.client.ClientResponse;

import nc.noumea.mairie.ptg.dao.IPointagesDao;
import nc.noumea.mairie.ptg.domain.TitreRepasExportEtatsPayeurTask;
import nc.noumea.mairie.sirh.service.IDownloadDocumentService;
import nc.noumea.mairie.sirh.tools.IIncidentLoggerService;
import nc.noumea.mairie.sirh.ws.ReturnMessageDto;

public class TitreRepasExportEtatsPayeurJobTest {

	@Test
	public void exportTitreRepasEtatsPayeurTask_NoTaskToExport_Return() throws JobExecutionException {

		// Given
		IPointagesDao pdao = Mockito.mock(IPointagesDao.class);
		Mockito.when(pdao.getNextTitreRepasExportEtatsPayeurTask()).thenReturn(null);

		TitreRepasExportEtatsPayeurJob job = new TitreRepasExportEtatsPayeurJob();
		ReflectionTestUtils.setField(job, "pointagesDao", pdao);

		// When
		job.executeInternal(null);

		// Then
		Mockito.verify(pdao, Mockito.times(1)).beginTransaction();
		Mockito.verify(pdao, Mockito.times(1)).rollBackTransaction();
		Mockito.verify(pdao, Mockito.never()).commitTransaction();

	}

	@Test
	public void exportTitreRepasEtatsPayeurTask_EverythingOK() throws Exception {
		ClientResponse res = null;

		// Given
		TitreRepasExportEtatsPayeurTask t = new TitreRepasExportEtatsPayeurTask();
		t.setIdTitreRepasExportEtatsPayeurTask(99);
		t.setIdAgent(9005138);
		IPointagesDao pdao = Mockito.mock(IPointagesDao.class);
		Mockito.when(pdao.getNextTitreRepasExportEtatsPayeurTask()).thenReturn(t);

		IDownloadDocumentService dd = Mockito.mock(IDownloadDocumentService.class);
		Mockito.doReturn(res).when(dd).createAndFireRequest( "basetitreRepas/genereEtatPayeur?idAgentConnecte=9005138",
				null);
		Mockito.doReturn(new ReturnMessageDto()).when(dd).readResponse(ReturnMessageDto.class, res,"basetitreRepas/genereEtatPayeur?idAgentConnecte=9005138"
				);

		IIncidentLoggerService incidentLoggerService = Mockito.mock(IIncidentLoggerService.class);

		TitreRepasExportEtatsPayeurJob job = new TitreRepasExportEtatsPayeurJob();
		ReflectionTestUtils.setField(job, "SIRH_PTG_WS_Base_URL", "base");
		ReflectionTestUtils.setField(job, "pointagesDao", pdao);
		ReflectionTestUtils.setField(job, "downloadDocumentService", dd);
		ReflectionTestUtils.setField(job, "incidentLoggerService", incidentLoggerService);

		// When
		job.executeInternal(null);

		// Then
		assertEquals("OK", t.getTaskStatus());
		assertNotNull(t.getDateExport());
		Mockito.verify(pdao, Mockito.times(1)).beginTransaction();
		Mockito.verify(pdao, Mockito.times(1)).commitTransaction();
		Mockito.verify(pdao, Mockito.never()).rollBackTransaction();
		Mockito.verify(dd, Mockito.times(1)).createAndFireRequest( "basetitreRepas/genereEtatPayeur?idAgentConnecte=9005138", null);
		Mockito.verify(dd, Mockito.times(1)).readResponse(ReturnMessageDto.class,res, "basetitreRepas/genereEtatPayeur?idAgentConnecte=9005138");
		verify(incidentLoggerService, never()).logIncident(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
				Mockito.any(Exception.class));
	}

	@Test
	public void exportTitreRepasEtatsPayeurTask_WithReturnMessageDto() throws Exception {
		ClientResponse res = null;
		// Given
		ReturnMessageDto rmd = new ReturnMessageDto();
		rmd.getErrors().add("Tests NN");
		rmd.getErrors().add("Tests NN");
		rmd.getErrors().add("BRAVO");
		TitreRepasExportEtatsPayeurTask t = new TitreRepasExportEtatsPayeurTask();
		t.setIdTitreRepasExportEtatsPayeurTask(99);
		t.setIdAgent(9005138);
		IPointagesDao pdao = Mockito.mock(IPointagesDao.class);
		Mockito.when(pdao.getNextTitreRepasExportEtatsPayeurTask()).thenReturn(t);

		IDownloadDocumentService dd = Mockito.mock(IDownloadDocumentService.class);
		Mockito.doReturn(res).when(dd).createAndFireRequest( "basetitreRepas/genereEtatPayeur?idAgentConnecte=9005138",
				null);
		Mockito.doReturn(rmd).when(dd).readResponse(ReturnMessageDto.class, res,"basetitreRepas/genereEtatPayeur?idAgentConnecte=9005138"
				);

		IIncidentLoggerService incidentLoggerService = Mockito.mock(IIncidentLoggerService.class);

		TitreRepasExportEtatsPayeurJob job = new TitreRepasExportEtatsPayeurJob();
		ReflectionTestUtils.setField(job, "SIRH_PTG_WS_Base_URL", "base");
		ReflectionTestUtils.setField(job, "pointagesDao", pdao);
		ReflectionTestUtils.setField(job, "downloadDocumentService", dd);
		ReflectionTestUtils.setField(job, "incidentLoggerService", incidentLoggerService);

		// When
		job.executeInternal(null);

		// Then
		assertEquals("Erreur : Tests NN.BRAVO", t.getTaskStatus());
		assertNotNull(t.getDateExport());
		Mockito.verify(pdao, Mockito.times(1)).beginTransaction();
		Mockito.verify(pdao, Mockito.times(1)).commitTransaction();
		Mockito.verify(pdao, Mockito.never()).rollBackTransaction();
		Mockito.verify(dd, Mockito.times(1)).createAndFireRequest( "basetitreRepas/genereEtatPayeur?idAgentConnecte=9005138", null);
		Mockito.verify(dd, Mockito.times(1)).readResponse(ReturnMessageDto.class,res, "basetitreRepas/genereEtatPayeur?idAgentConnecte=9005138");
		verify(incidentLoggerService, never()).logIncident(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
				Mockito.any(Exception.class));
	}

	@Test
	public void exportTitreRepasEtatsPayeurTask_WSCallfails_SetMessage() throws Exception {
		ClientResponse res = null;
		// Given
		TitreRepasExportEtatsPayeurTask t = new TitreRepasExportEtatsPayeurTask();
		t.setIdTitreRepasExportEtatsPayeurTask(99);
		t.setIdAgent(9005138);
		IPointagesDao pdao = Mockito.mock(IPointagesDao.class);
		Mockito.when(pdao.getNextTitreRepasExportEtatsPayeurTask()).thenReturn(t);

		IDownloadDocumentService dd = Mockito.mock(IDownloadDocumentService.class);
		Mockito.doReturn(res).when(dd).createAndFireRequest( "basetitreRepas/genereEtatPayeur?idAgentConnecte=9005138",
				null);
		Mockito.doThrow(new Exception("MSG")).when(dd).readResponse(ReturnMessageDto.class, res,"basetitreRepas/genereEtatPayeur?idAgentConnecte=9005138"
				);

		IIncidentLoggerService incidentLoggerService = Mockito.mock(IIncidentLoggerService.class);
		Mockito.doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) {
				return true;
			}
		}).when(incidentLoggerService).logIncident(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.any(Exception.class));

		TitreRepasExportEtatsPayeurJob job = new TitreRepasExportEtatsPayeurJob();
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
		verify(incidentLoggerService, times(1)).logIncident(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
				Mockito.any(Exception.class));
		Mockito.verify(dd, Mockito.times(1)).createAndFireRequest( "basetitreRepas/genereEtatPayeur?idAgentConnecte=9005138", null);
		Mockito.verify(dd, Mockito.times(1)).readResponse(ReturnMessageDto.class,res, "basetitreRepas/genereEtatPayeur?idAgentConnecte=9005138");
	}
}
