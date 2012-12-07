package nc.noumea.mairie.sirh.eae.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import nc.noumea.mairie.sirh.eae.dao.EaeCampagneActionDao;
import nc.noumea.mairie.sirh.eae.domain.EaeCampagneAction;

import org.joda.time.DateTime;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

public class EaeCampagneActionDaoTest {

	private Date today = new DateTime(2012, 12, 21, 0, 0, 0, 0).toDate();
	
	@Test
	@SuppressWarnings("unchecked")
	public void testGetNextEaeCampagneActionToSend_NothingActionToSend_ReturnNull() {
		// Given
		List<EaeCampagneAction> list = new ArrayList<EaeCampagneAction>();
		
		TypedQuery<EaeCampagneAction> queryMock = (TypedQuery<EaeCampagneAction>) Mockito.mock(TypedQuery.class);
		when(queryMock.getResultList()).thenReturn(list);
		
		EntityManager eManagerMock = Mockito.mock(EntityManager.class);
		when(eManagerMock.createNamedQuery("EaeCampagneAction.getNextTodayNotification", EaeCampagneAction.class)).thenReturn(queryMock);
		
		EaeCampagneActionDao dao = new EaeCampagneActionDao();
		ReflectionTestUtils.setField(dao, "eaeEntityManager", eManagerMock);
		
		// When
		EaeCampagneAction result = dao.getNextEaeCampagneActionToSend(today);
		
		// Then
		assertNull(result);
		
		verify(queryMock).setParameter("todayDate", today);
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void testGetEaeCampagneActionToSend_1NotificationsToSend_ReturnItem() {
		// Given
		List<EaeCampagneAction> list = new ArrayList<EaeCampagneAction>();
		list.add(new EaeCampagneAction());
		
		TypedQuery<EaeCampagneAction> queryMock = (TypedQuery<EaeCampagneAction>) Mockito.mock(TypedQuery.class);
		when(queryMock.getResultList()).thenReturn(list);
		
		EntityManager eManagerMock = Mockito.mock(EntityManager.class);
		when(eManagerMock.createNamedQuery("EaeCampagneAction.getNextTodayNotification", EaeCampagneAction.class)).thenReturn(queryMock);
		
		EaeCampagneActionDao dao = new EaeCampagneActionDao();
		ReflectionTestUtils.setField(dao, "eaeEntityManager", eManagerMock);
		
		// When
		EaeCampagneAction result = dao.getNextEaeCampagneActionToSend(today);
				
		// Then
		assertNotNull(result);
		assertEquals(list.get(0), result);
		
		verify(queryMock).setParameter("todayDate", today);
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void testCountEaeCampagneActionToSend_2NotificationsToSend_Return2() {
		// Given
		Long result = 2l;
		
		TypedQuery<Long> queryMock = (TypedQuery<Long>) Mockito.mock(TypedQuery.class);
		when(queryMock.getSingleResult()).thenReturn(result);
		
		EntityManager eManagerMock = Mockito.mock(EntityManager.class);
		when(eManagerMock.createNamedQuery("EaeCampagneAction.countTodayNotifications", Long.class)).thenReturn(queryMock);
		
		EaeCampagneActionDao dao = new EaeCampagneActionDao();
		ReflectionTestUtils.setField(dao, "eaeEntityManager", eManagerMock);
		
		// When
		long actualResult = dao.countEaeCampagneActionToSend(today);
				
		// Then
		assertEquals(result, new Long(actualResult));
		
		verify(queryMock).setParameter("todayDate", today);
	}
	
}
