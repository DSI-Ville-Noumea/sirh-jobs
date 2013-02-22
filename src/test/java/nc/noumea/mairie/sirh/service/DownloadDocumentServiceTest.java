package nc.noumea.mairie.sirh.service;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.mockito.Mockito;

import com.sun.jersey.api.client.ClientResponse;

public class DownloadDocumentServiceTest {

	@Test
	public void testreadJsonResponseAsList_ListOfString() throws Exception {
		// Given
		ClientResponse res = Mockito.mock(ClientResponse.class);
		Mockito.when(res.getEntity(String.class)).thenReturn("[ \"prout\", \"prout2\" ]");
		Mockito.when(res.getStatus()).thenReturn(200);
		
		DownloadDocumentService service = new DownloadDocumentService();
		
		// When
		List<String> result = service.readJsonResponseAsList(res, null, null);
		
		// Then
		assertEquals(2, result.size());
	}
	
	@Test
	public void testreadJsonResponseAsList_ListOfInteger() throws Exception {
		// Given
		ClientResponse res = Mockito.mock(ClientResponse.class);
		Mockito.when(res.getEntity(String.class)).thenReturn("[ 15, 16 ]");
		Mockito.when(res.getStatus()).thenReturn(200);
		
		DownloadDocumentService service = new DownloadDocumentService();
		
		// When
		List<Integer> result = service.readJsonResponseAsList(res, null, null);
		
		// Then
		assertEquals(2, result.size());
	}
}
