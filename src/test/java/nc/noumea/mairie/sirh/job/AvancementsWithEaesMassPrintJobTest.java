package nc.noumea.mairie.sirh.job;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import nc.noumea.mairie.sirh.dao.IAvctCapPrintJobDao;
import nc.noumea.mairie.sirh.domain.AvctCapPrintJob;
import nc.noumea.mairie.sirh.service.IDownloadDocumentService;
import nc.noumea.mairie.sirh.service.IReportingService;
import nc.noumea.mairie.sirh.service.PrinterHelper;
import nc.noumea.mairie.sirh.tools.AvancementsWithEaesMassPrintJobStatusEnum;
import nc.noumea.mairie.sirh.tools.Helper;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.VFS;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

public class AvancementsWithEaesMassPrintJobTest {

	private static Helper helperMock;
	private static Date theDate;
	
	@Before
	public void SetUp() {
		theDate = new DateTime(2013, 2, 22, 9, 6, 7).toDate();
		helperMock = Mockito.mock(Helper.class);
		when(helperMock.getCurrentDate()).thenReturn(theDate);
	}
	
	@Test
	public void testinitializePrintJob_GenerateId() {
		
		// Given
		AvctCapPrintJob pj = new AvctCapPrintJob();
		pj.setIdCap(11);
		pj.setIdCadreEmploi(87);
		pj.setAgentId(9005138);

		IAvctCapPrintJobDao daoMock = Mockito.mock(IAvctCapPrintJobDao.class);

		AvancementsWithEaesMassPrintJob job = new AvancementsWithEaesMassPrintJob();
		ReflectionTestUtils.setField(job, "helper", helperMock);
		ReflectionTestUtils.setField(job, "printJobDao", daoMock);
		
		// When
		job.initializePrintJob(pj);
		
		// Then
		assertEquals("SIRH_AVCT_20130222-090607_9005138_11_87", pj.getJobId());
		assertEquals(AvancementsWithEaesMassPrintJobStatusEnum.START.toString(), pj.getStatus());
		Mockito.verify(daoMock, Mockito.times(1)).updateAvctCapPrintJob(pj);
	}
	
	@Test
	public void testgenerateAvancementsReport_callReprortingServiceWithReportName() throws Exception {
		
		// Given
		AvctCapPrintJob pj = new AvctCapPrintJob();
		pj.setIdCap(11);
		pj.setLogin("login");
		pj.setEaes(true);
		pj.setCodeCap("CODE CAP");
		pj.setIdCadreEmploi(87);
		pj.setLibCadreEmploi("CADRE EMPLOI");
		pj.setSubmissionDate(new DateTime(2013, 02, 25, 10, 48, 0).toDate());
		pj.setJobId("SIRH_AVCT_20130222-090607_9005138_11_87");
		String avcstTempWorkspacePath = "/home/sirh/docs/";
		
		IAvctCapPrintJobDao daoMock = Mockito.mock(IAvctCapPrintJobDao.class);
		IReportingService reportingServiceMock = Mockito.mock(IReportingService.class);
		
		AvancementsWithEaesMassPrintJob job = new AvancementsWithEaesMassPrintJob();
		ReflectionTestUtils.setField(job, "printJobDao", daoMock);
		ReflectionTestUtils.setField(job, "reportingService", reportingServiceMock);
		ReflectionTestUtils.setField(job, "avcstTempWorkspacePath", avcstTempWorkspacePath);
		ReflectionTestUtils.setField(job, "helper", helperMock);

		String expectedOutputFileName = "/home/sirh/docs/SIRH_AVCT_20130222-090607_9005138_11_87_001_avct_table_report.pdf";
		String expectedFirstPageOutputFileName = "/home/sirh/docs/SIRH_AVCT_20130222-090607_9005138_11_87_000_firstPage.pdf";
		String expectedLastPageOutputFileName = "/home/sirh/docs/SIRH_AVCT_20130222-090607_9005138_11_87_999_lastPage.pdf";
				
		// When
		job.generateAvancementsReport(pj);
		
		// Then
		assertEquals(AvancementsWithEaesMassPrintJobStatusEnum.AVCT_REPORT.toString(), pj.getStatus());
		assertEquals(3, pj.getFilesToPrint().size());
		assertEquals(expectedOutputFileName, pj.getFilesToPrint().get(0));
		assertEquals(expectedFirstPageOutputFileName, pj.getFilesToPrint().get(1));
		assertEquals(expectedLastPageOutputFileName, pj.getFilesToPrint().get(2));
		
		Mockito.verify(daoMock, Mockito.times(1)).updateAvctCapPrintJob(pj);
		Mockito.verify(reportingServiceMock, Mockito.times(1)).getTableauAvancementsReportAndSaveItToFile(11,  87, expectedOutputFileName);
		Mockito.verify(reportingServiceMock, Mockito.times(1))
			.getAvctFirstLastPrintPage("SIRH_AVCT_20130222-090607_9005138_11_87", "login", "CODE CAP", "CADRE EMPLOI", new DateTime(2013, 02, 25, 10, 48, 0).toDate(), 
					true, true, expectedFirstPageOutputFileName);
		Mockito.verify(reportingServiceMock, Mockito.times(1))
			.getAvctFirstLastPrintPage("SIRH_AVCT_20130222-090607_9005138_11_87", "login", "CODE CAP", "CADRE EMPLOI", new DateTime(2013, 02, 25, 10, 48, 0).toDate(), 
					false, true, expectedLastPageOutputFileName);
	}
	
	@Test
	public void testdownloadRelatedEaes_2Eaes() throws Exception {
		
		// Given
		AvctCapPrintJob pj = new AvctCapPrintJob();
		pj.setIdCap(11);
		pj.setIdCadreEmploi(87);
		pj.setJobId("SIRH_AVCT_20130222-090607_9005138_11_87");
		String sirhWsEndpointUrl = "http://sirhWsEndpointUrl/";
		IAvctCapPrintJobDao daoMock = Mockito.mock(IAvctCapPrintJobDao.class);
		Map<String, String> sirhUrlParameters = new HashMap<String, String>();
		sirhUrlParameters.put("idCap", "11");
		sirhUrlParameters.put("idCadreEmploi", "87");
		
		IDownloadDocumentService downloadDocumentServiceMock = Mockito.mock(IDownloadDocumentService.class);
		Mockito.when(downloadDocumentServiceMock.downloadJsonDocumentAsList(String.class, sirhWsEndpointUrl, sirhUrlParameters))
			.thenReturn(Arrays.asList("eae1", "eae2"));
		
		AvancementsWithEaesMassPrintJob job = Mockito.spy(new AvancementsWithEaesMassPrintJob());
		ReflectionTestUtils.setField(job, "printJobDao", daoMock);
		ReflectionTestUtils.setField(job, "sirhWsAvctEaesEndpointUrl", sirhWsEndpointUrl);
		ReflectionTestUtils.setField(job, "downloadDocumentService", downloadDocumentServiceMock);
		ReflectionTestUtils.setField(job, "helper", helperMock);

	    Mockito.doReturn("eaeFileName1.pdf").when(job).copyEaeToLocalPath(pj, "eae1", 1);
	    Mockito.doReturn("eaeFileName2.pdf").when(job).copyEaeToLocalPath(pj, "eae2", 2);
		
		// When
		job.downloadRelatedEaes(pj);
		
		// Then
		assertEquals(AvancementsWithEaesMassPrintJobStatusEnum.EAE_DOWNLOAD.toString(), pj.getStatus());
		assertEquals(2, pj.getFilesToPrint().size());
		assertEquals("eaeFileName1.pdf", pj.getFilesToPrint().get(0));
		assertEquals("eaeFileName2.pdf", pj.getFilesToPrint().get(1));
		
		Mockito.verify(daoMock, Mockito.times(1)).updateAvctCapPrintJob(pj);
	}
	
	@Test
	public void testprintAllDocuments_sendAllDocumentsToPrinter() throws Exception {
		
		// Given
		AvctCapPrintJob pj = new AvctCapPrintJob();
		pj.setLogin("login");
		pj.getFilesToPrint().add("doc1.pdf");
		pj.getFilesToPrint().add("doc2.pdf");
		
		IAvctCapPrintJobDao daoMock = Mockito.mock(IAvctCapPrintJobDao.class);
		PrinterHelper pHMock = Mockito.mock(PrinterHelper.class);
		
		AvancementsWithEaesMassPrintJob job = new AvancementsWithEaesMassPrintJob();
		ReflectionTestUtils.setField(job, "printJobDao", daoMock);
		ReflectionTestUtils.setField(job, "helper", helperMock);
		
		// When
		job.printAllDocuments(pj, pHMock);
		
		// Then
		assertEquals(AvancementsWithEaesMassPrintJobStatusEnum.QUEUE_PRINT.toString(), pj.getStatus());
		Mockito.verify(daoMock, Mockito.times(1)).updateAvctCapPrintJob(pj);
		Mockito.verify(pHMock, Mockito.times(1)).printDocument("doc1.pdf", "login");
		Mockito.verify(pHMock, Mockito.times(1)).printDocument("doc2.pdf", "login");
	}

	@Test
	public void testwipeJobDocuments() throws FileSystemException, AvancementsWithEaesMassPrintException {
		
		// Given
		String tempFilePath = "ram:///temp/folder/";
		FileObject fo = VFS.getManager().resolveFile(tempFilePath);
		fo.resolveFile("doc1.pdf").createFile();
		fo.resolveFile("doc2.pdf").createFile();
		AvctCapPrintJob pj = new AvctCapPrintJob();
		
		AvancementsWithEaesMassPrintJob job = new AvancementsWithEaesMassPrintJob();
		ReflectionTestUtils.setField(job, "avcstTempWorkspacePath", tempFilePath);
		
		// When
		job.wipeJobDocuments(pj);
		
		// Then
		assertEquals(0, fo.getChildren().length);
	}
}
