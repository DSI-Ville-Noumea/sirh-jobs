package nc.noumea.mairie.sirh.eae.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import nc.noumea.mairie.sirh.eae.domain.EaeCampagneAction;
import nc.noumea.mairie.sirh.tools.Helper;

import org.joda.time.DateTime;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

public class EaeCampagneActionServiceTest {

	private static Helper helperMock;
	
	@BeforeClass
	public static void SetUp() {
		helperMock = Mockito.mock(Helper.class);
		when(helperMock.getCurrentDate()).thenReturn(new DateTime(2012, 12, 21, 0, 0, 0, 0).toDate());
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void testGetEaeCampagneActionToSend_NothingActionToSend_ReturnEmptyList() {
		// Given
		List<EaeCampagneAction> list = new ArrayList<EaeCampagneAction>();
		
		TypedQuery<EaeCampagneAction> queryMock = (TypedQuery<EaeCampagneAction>) Mockito.mock(TypedQuery.class);
		when(queryMock.getResultList()).thenReturn(list);
		
		EntityManager eManagerMock = Mockito.mock(EntityManager.class);
		when(eManagerMock.createNamedQuery("EaeCampagneAction.getTodayNotifications", EaeCampagneAction.class)).thenReturn(queryMock);
		
		EaeCampagneActionService service = new EaeCampagneActionService();
		ReflectionTestUtils.setField(service, "eaeEntityManager", eManagerMock);
		ReflectionTestUtils.setField(service, "helper", helperMock);
		
		// When
		List<EaeCampagneAction> result = service.getEaeCampagneActionToSend();
		
		// Then
		assertEquals(0, result.size());
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void testGetEaeCampagneActionToSend_2NotificationsToSend_ReturnListOf2() {
		// Given
		List<EaeCampagneAction> list = new ArrayList<EaeCampagneAction>();
		list.add(new EaeCampagneAction());
		list.add(new EaeCampagneAction());
		
		TypedQuery<EaeCampagneAction> queryMock = (TypedQuery<EaeCampagneAction>) Mockito.mock(TypedQuery.class);
		when(queryMock.getResultList()).thenReturn(list);
		
		EntityManager eManagerMock = Mockito.mock(EntityManager.class);
		when(eManagerMock.createNamedQuery("EaeCampagneAction.getTodayNotifications", EaeCampagneAction.class)).thenReturn(queryMock);
		
		EaeCampagneActionService service = new EaeCampagneActionService();
		ReflectionTestUtils.setField(service, "eaeEntityManager", eManagerMock);
		ReflectionTestUtils.setField(service, "helper", helperMock);
		
		// When
		List<EaeCampagneAction> result = service.getEaeCampagneActionToSend();
		
		// Then
		assertEquals(2, result.size());
	}
	
}
