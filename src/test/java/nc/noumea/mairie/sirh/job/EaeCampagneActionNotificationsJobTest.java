package nc.noumea.mairie.sirh.job;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;

import nc.noumea.mairie.sirh.eae.dao.DaoException;
import nc.noumea.mairie.sirh.eae.dao.EaeCampagneActionDao;
import nc.noumea.mairie.sirh.eae.domain.EaeCampagne;
import nc.noumea.mairie.sirh.eae.domain.EaeCampagneAction;
import nc.noumea.mairie.sirh.tools.Helper;

import org.joda.time.DateTime;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

public class EaeCampagneActionNotificationsJobTest {

	private static Helper helperMock;
	private static Date theDate;
	
	@BeforeClass
	public static void SetUp() {
		theDate = new DateTime(2012, 12, 21, 0, 0, 0, 0).toDate();
		helperMock = Mockito.mock(Helper.class);
		when(helperMock.getCurrentDate()).thenReturn(theDate);
	}
	
	@Test
	public void testSendNotificationsOneByOne_NoNotificationsToSend() throws EaeCampagneActionNotificationsException {
		// Given
		EaeCampagneActionDao eaeCampagneActionDaoMock = Mockito.mock(EaeCampagneActionDao.class);
		when(eaeCampagneActionDaoMock.countEaeCampagneActionToSend(theDate)).thenReturn(0l);

		EaeCampagneActionNotificationsJob service = new EaeCampagneActionNotificationsJob();
		ReflectionTestUtils.setField(service, "helper", helperMock);
		ReflectionTestUtils.setField(service, "eaeCampagneActionDao", eaeCampagneActionDaoMock);
		
		// When
		service.sendNotificationsOneByOne();
		
		// Then
		verify(eaeCampagneActionDaoMock, times(0)).getNextEaeCampagneActionToSend(theDate);
	}
	
	@Test
	public void testSendNotificationsOneByOne_1NotificationsToSend() throws EaeCampagneActionNotificationsException {
		// Given
		EaeCampagneActionDao eaeCampagneActionDaoMock = Mockito.mock(EaeCampagneActionDao.class);
		when(eaeCampagneActionDaoMock.countEaeCampagneActionToSend(theDate)).thenReturn(1l);

		EaeCampagneActionNotificationsJob service = new EaeCampagneActionNotificationsJob();
		ReflectionTestUtils.setField(service, "helper", helperMock);
		ReflectionTestUtils.setField(service, "eaeCampagneActionDao", eaeCampagneActionDaoMock);
		
		// When
		service.sendNotificationsOneByOne();
		
		// Then
		verify(eaeCampagneActionDaoMock, times(1)).getNextEaeCampagneActionToSend(theDate);
	}
	
	@Test
	public void testSendNotificationsOneByOne_4NotificationsToSend_3Errors_ThrowExceptionAfter3() throws DaoException {
		
		// Given
		EaeCampagne c = new EaeCampagne();
		EaeCampagneAction a1 = new EaeCampagneAction();
		a1.setEaeCampagne(c);
		a1.setIdCampagneAction(1);
		EaeCampagneAction a2 = new EaeCampagneAction();
		a2.setEaeCampagne(c);
		a2.setIdCampagneAction(2);
		EaeCampagneAction a3 = new EaeCampagneAction();
		a3.setEaeCampagne(c);
		a3.setIdCampagneAction(3);
		EaeCampagneAction a4 = new EaeCampagneAction();
		a4.setEaeCampagne(c);
		a4.setIdCampagneAction(4);
		
		EaeCampagneActionDao eaeCampagneActionDaoMock = Mockito.mock(EaeCampagneActionDao.class);
		when(eaeCampagneActionDaoMock.countEaeCampagneActionToSend(theDate)).thenReturn(4l);
		when(eaeCampagneActionDaoMock.getNextEaeCampagneActionToSend(theDate))
			.thenReturn(a1)
			.thenReturn(a2)
			.thenReturn(a3)
			.thenReturn(a4);
		
		when(eaeCampagneActionDaoMock.setDateMailEnvoye(a1, theDate)).thenThrow(new DaoException());
		when(eaeCampagneActionDaoMock.setDateMailEnvoye(a2, theDate)).thenThrow(new DaoException());
		when(eaeCampagneActionDaoMock.setDateMailEnvoye(a3, theDate)).thenThrow(new DaoException());
		
		EaeCampagneActionNotificationsJob service = new EaeCampagneActionNotificationsJob();
		ReflectionTestUtils.setField(service, "helper", helperMock);
		ReflectionTestUtils.setField(service, "eaeCampagneActionDao", eaeCampagneActionDaoMock);
		
		try {
			// When
			service.sendNotificationsOneByOne();
		} catch (EaeCampagneActionNotificationsException e) {
			// Then
			assertEquals("Stopped sending notifications because exceeded the maximum authorized number of tries.", e.getMessage());
			verify(eaeCampagneActionDaoMock, times(3)).getNextEaeCampagneActionToSend(any(Date.class));
		}
	}
}
