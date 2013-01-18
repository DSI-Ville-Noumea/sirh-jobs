package nc.noumea.mairie.sirh.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import javax.persistence.EntityManager;

import nc.noumea.mairie.sirh.domain.DocumentAssocie;

import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

public class SirhDocumentDaoTest {

//	@Test
	public void testGetDocumentAssocie_DocumentDoesNotExist_returnNull() {
		// Given
		DocumentAssocie doc = null;
		
		EntityManager sirhEntityManagerMock = Mockito.mock(EntityManager.class);
		Mockito.when(sirhEntityManagerMock.find(DocumentAssocie.class, 1)).thenReturn(doc);
		
		SirhDocumentDao dao = new SirhDocumentDao();
		ReflectionTestUtils.setField(dao, "sirhEntityManager", sirhEntityManagerMock);
		
		// When
		DocumentAssocie result = dao.getDocumentAssocie(1);
		
		// Then
		assertNull(result);
	}
	
//	@Test
	public void testGetDocumentAssocie_DocumentDoesExists_returnIt() {
		// Given
		DocumentAssocie doc = new DocumentAssocie();
		
		EntityManager sirhEntityManagerMock = Mockito.mock(EntityManager.class);
		Mockito.when(sirhEntityManagerMock.find(DocumentAssocie.class, 1)).thenReturn(doc);
		
		SirhDocumentDao dao = new SirhDocumentDao();
		ReflectionTestUtils.setField(dao, "sirhEntityManager", sirhEntityManagerMock);
		
		// When
		DocumentAssocie result = dao.getDocumentAssocie(1);
		
		// Then
		assertEquals(doc, result);
	}
}
