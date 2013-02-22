package nc.noumea.mairie.sirh.job;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import nc.noumea.mairie.sirh.dao.IPrintJobDao;
import nc.noumea.mairie.sirh.domain.PrintJob;
import nc.noumea.mairie.sirh.service.IDownloadDocumentService;
import nc.noumea.mairie.sirh.service.IReportingService;
import nc.noumea.mairie.sirh.service.PrinterHelper;
import nc.noumea.mairie.sirh.tools.AvancementsWithEaesMassPrintJobStatusEnum;
import nc.noumea.mairie.sirh.tools.Helper;

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
		PrintJob pj = new PrintJob();
		pj.setIdCap(11);
		pj.setIdCadreEmploi(87);
		pj.setAgentId("9005138");

		IPrintJobDao daoMock = Mockito.mock(IPrintJobDao.class);

		AvancementsWithEaesMassPrintJob job = new AvancementsWithEaesMassPrintJob();
		ReflectionTestUtils.setField(job, "helper", helperMock);
		ReflectionTestUtils.setField(job, "printJobDao", daoMock);
		
		// When
		job.initializePrintJob(pj);
		
		// Then
		assertEquals("20130222-090607_9005138_11_87", pj.getJobId());
		assertEquals(AvancementsWithEaesMassPrintJobStatusEnum.START.toString(), pj.getStatus());
		Mockito.verify(daoMock, Mockito.times(1)).updateJobIdAndStatus(pj);
	}
	
	@Test
	public void testgenerateAvancementsReport_callReprortingServiceWithReportName() throws Exception {
		
		// Given
		PrintJob pj = new PrintJob();
		pj.setIdCap(11);
		pj.setIdCadreEmploi(87);
		pj.setJobId("20130222-090607_9005138_11_87");
		String avcstTempWorkspacePath = "/home/sirh/docs/";
		
		IPrintJobDao daoMock = Mockito.mock(IPrintJobDao.class);
		IReportingService reportingServiceMock = Mockito.mock(IReportingService.class);
		
		AvancementsWithEaesMassPrintJob job = new AvancementsWithEaesMassPrintJob();
		ReflectionTestUtils.setField(job, "printJobDao", daoMock);
		ReflectionTestUtils.setField(job, "reportingService", reportingServiceMock);
		ReflectionTestUtils.setField(job, "avcstTempWorkspacePath", avcstTempWorkspacePath);

		String expectedOutputFileName = "/home/sirh/docs/20130222-090607_9005138_11_87_001_avct_table_report.pdf";
				
		// When
		job.generateAvancementsReport(pj);
		
		// Then
		assertEquals(AvancementsWithEaesMassPrintJobStatusEnum.AVCT_REPORT.toString(), pj.getStatus());
		assertEquals(1, pj.getFilesToPrint().size());
		assertEquals(expectedOutputFileName, pj.getFilesToPrint().get(0));
		
		Mockito.verify(daoMock, Mockito.times(1)).updateStatus(pj);
		Mockito.verify(reportingServiceMock, Mockito.times(1)).getTableauAvancementsReportAndSaveItToFile(11,  87, expectedOutputFileName);
	}
	
	@Test
	public void testdownloadRelatedEaes_2Eaes() throws Exception {
		
		// Given
		PrintJob pj = new PrintJob();
		pj.setIdCap(11);
		pj.setIdCadreEmploi(87);
		pj.setJobId("20130222-090607_9005138_11_87");
		String avcstTempWorkspacePath = "/home/sirh/docs/";
		String sharepointEaeDocBaseUrl = "http://sharepointEaeDocBaseUrl/";
		String sirhWsEndpointUrl = "http://sirhWsEndpointUrl/";
		IPrintJobDao daoMock = Mockito.mock(IPrintJobDao.class);
		Map<String, String> sirhUrlParameters = new HashMap<String, String>();
		sirhUrlParameters.put("idCap", "11");
		sirhUrlParameters.put("idCadreEmploi", "87");
		
		IDownloadDocumentService downloadDocumentServiceMock = Mockito.mock(IDownloadDocumentService.class);
		Mockito.when(downloadDocumentServiceMock.downloadJsonDocumentAs(String.class, sirhWsEndpointUrl, sirhUrlParameters)).thenReturn(Arrays.asList("eae1", "eae2"));
		
		AvancementsWithEaesMassPrintJob job = new AvancementsWithEaesMassPrintJob();
		ReflectionTestUtils.setField(job, "printJobDao", daoMock);
		ReflectionTestUtils.setField(job, "avcstTempWorkspacePath", avcstTempWorkspacePath);
		ReflectionTestUtils.setField(job, "sharepointEaeDocBaseUrl", sharepointEaeDocBaseUrl);
		ReflectionTestUtils.setField(job, "sirhWsEndpointUrl", sirhWsEndpointUrl);
		ReflectionTestUtils.setField(job, "downloadDocumentService", downloadDocumentServiceMock);

		String expectedsharepointEaeDocBaseUrl = "http://sharepointEaeDocBaseUrl/";
		Map<String, String> param1 = new HashMap<String, String>();
		param1.put("eaeId", "eae1");
		Map<String, String> param2 = new HashMap<String, String>();
		param2.put("eaeId", "eae2");
		String expectedOutputFileName1 = "/home/sirh/docs/20130222-090607_9005138_11_87_000_eae1.pdf";
		String expectedOutputFileName2 = "/home/sirh/docs/20130222-090607_9005138_11_87_001_eae2.pdf";
				
		// When
		job.downloadRelatedEaes(pj);
		
		// Then
		assertEquals(AvancementsWithEaesMassPrintJobStatusEnum.EAE_DOWNLOAD.toString(), pj.getStatus());
		assertEquals(2, pj.getFilesToPrint().size());
		assertEquals(expectedOutputFileName1, pj.getFilesToPrint().get(0));
		assertEquals(expectedOutputFileName2, pj.getFilesToPrint().get(1));
		
		Mockito.verify(daoMock, Mockito.times(1)).updateStatus(pj);
		Mockito.verify(downloadDocumentServiceMock, Mockito.times(1)).downloadDocumentToLocalPath(expectedsharepointEaeDocBaseUrl, param1, expectedOutputFileName1);
		Mockito.verify(downloadDocumentServiceMock, Mockito.times(1)).downloadDocumentToLocalPath(expectedsharepointEaeDocBaseUrl, param2, expectedOutputFileName2);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testprintAllDocuments_sendAllDocumentsToPrinter() throws Exception {
		
		// Given
		PrintJob pj = new PrintJob();
		pj.getFilesToPrint().add("doc1.pdf");
		pj.getFilesToPrint().add("doc2.pdf");
		
		IPrintJobDao daoMock = Mockito.mock(IPrintJobDao.class);
		PrinterHelper pHMock = Mockito.mock(PrinterHelper.class);
		
		AvancementsWithEaesMassPrintJob job = new AvancementsWithEaesMassPrintJob();
		ReflectionTestUtils.setField(job, "printJobDao", daoMock);
		
		// When
		job.printAllDocuments(pj, pHMock);
		
		// Then
		assertEquals(AvancementsWithEaesMassPrintJobStatusEnum.QUEUE_PRINT.toString(), pj.getStatus());
		Mockito.verify(daoMock, Mockito.times(1)).updateStatus(pj);
		Mockito.verify(pHMock, Mockito.times(1)).printDocument(Mockito.eq("doc1.pdf"), Mockito.anyMap());
		Mockito.verify(pHMock, Mockito.times(1)).printDocument(Mockito.eq("doc2.pdf"), Mockito.anyMap());
	}
}
