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

import org.joda.time.DateTime;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import nc.noumea.mairie.sirh.eae.dao.EaeCampagneActionDao;
import nc.noumea.mairie.sirh.eae.domain.EaeCampagneAction;

public class EaeCampagneActionDaoTest {

	private Date today = new DateTime(2012, 12, 21, 0, 0, 0, 0).toDate();

	// @Test
	public void testGetNextEaeCampagneActionToSend_NothingActionToSend_ReturnNull() {
		// Given
		List<EaeCampagneAction> list = new ArrayList<EaeCampagneAction>();

		@SuppressWarnings("unchecked")
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

	// @Test
	public void testGetEaeCampagneActionToSend_1NotificationsToSend_ReturnItem() {
		// Given
		List<EaeCampagneAction> list = new ArrayList<EaeCampagneAction>();
		list.add(new EaeCampagneAction());

		@SuppressWarnings("unchecked")
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

	// @Test
	public void testGetEaeCampagneActionToSend_2NotificationsToSend_ReturnListOf2() {
		// Given
		List<EaeCampagneAction> result = new ArrayList<EaeCampagneAction>();
		result.add(new EaeCampagneAction());
		result.add(new EaeCampagneAction());

		@SuppressWarnings("unchecked")
		TypedQuery<EaeCampagneAction> queryMock = (TypedQuery<EaeCampagneAction>) Mockito.mock(TypedQuery.class);
		when(queryMock.getResultList()).thenReturn(result);

		EntityManager eManagerMock = Mockito.mock(EntityManager.class);
		when(eManagerMock.createNamedQuery("EaeCampagneAction.getTodayNotifications", EaeCampagneAction.class)).thenReturn(queryMock);

		EaeCampagneActionDao dao = new EaeCampagneActionDao();
		ReflectionTestUtils.setField(dao, "eaeEntityManager", eManagerMock);

		// When
		List<EaeCampagneAction> actualResult = dao.getEaeCampagneActionToSend(today);

		// Then
		assertEquals(result.size(), actualResult.size());

		verify(queryMock).setParameter("todayDate", today);
	}

}
