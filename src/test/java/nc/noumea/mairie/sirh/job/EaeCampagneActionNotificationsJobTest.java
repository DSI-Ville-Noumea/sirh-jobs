package nc.noumea.mairie.sirh.job;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nc.noumea.mairie.ldap.dao.IAgentLdapDao;
import nc.noumea.mairie.ldap.domain.AgentLdap;
import nc.noumea.mairie.sirh.eae.dao.IEaeCampagneActionDao;
import nc.noumea.mairie.sirh.eae.domain.EaeCampagne;
import nc.noumea.mairie.sirh.eae.domain.EaeCampagneAction;
import nc.noumea.mairie.sirh.tools.Helper;

import org.apache.commons.vfs2.FileSystemException;
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
		when(helperMock.convertIdAgentToADId(9)).thenReturn("90");
	}
	
	@Test
	public void testSendNotificationsOneByOne_NoNotificationsToSend() throws EaeCampagneActionNotificationsException, FileSystemException {
		// Given
		IEaeCampagneActionDao eaeCampagneActionDaoMock = Mockito.mock(IEaeCampagneActionDao.class);
		when(eaeCampagneActionDaoMock.getEaeCampagneActionToSend(theDate)).thenReturn(new ArrayList<EaeCampagneAction>());

		EaeCampagneActionNotificationsJob service = new EaeCampagneActionNotificationsJob();
		ReflectionTestUtils.setField(service, "helper", helperMock);
		ReflectionTestUtils.setField(service, "eaeCampagneActionDao", eaeCampagneActionDaoMock);
		
		// When
		service.sendNotificationsOneByOne();
		
		// Then
		verify(eaeCampagneActionDaoMock, times(0)).getNextEaeCampagneActionToSend(theDate);
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void testSendNotificationsOneByOne_1NotificationsToSend() throws Exception {
		// Given
		List<EaeCampagneAction> notificationsToSend = new ArrayList<EaeCampagneAction>();
		EaeCampagneAction campagneAction = new EaeCampagneAction();
		campagneAction.setIdAgent(9);
		notificationsToSend.add(campagneAction);
		
		IEaeCampagneActionDao eaeCampagneActionDaoMock = Mockito.mock(IEaeCampagneActionDao.class);
		when(eaeCampagneActionDaoMock.getEaeCampagneActionToSend(theDate)).thenReturn(notificationsToSend);

		AgentLdap agentLdap = new AgentLdap();
		IAgentLdapDao ldapMock = Mockito.mock(IAgentLdapDao.class);
		when(ldapMock.retrieveAgentFromLdapFromMatricule("90")).thenReturn(agentLdap);
		
		EaeCampagneActionNotificationsJob service = new	 EaeCampagneActionNotificationsJob();
		ReflectionTestUtils.setField(service, "helper", helperMock);
		ReflectionTestUtils.setField(service, "eaeCampagneActionDao", eaeCampagneActionDaoMock);
		ReflectionTestUtils.setField(service, "numberOfTries", 3);
		ReflectionTestUtils.setField(service, "agentLdapDao", ldapMock);
		
		EaeCampagneActionNotificationsJob serviceSpy = spy(service);
		doNothing().when(serviceSpy).sendEmail(eq(agentLdap), any(List.class), eq(campagneAction), eq(theDate));
		
		// When
		serviceSpy.sendNotificationsOneByOne();
		
		// Then
		//verify(eaeCampagneActionDaoMock, times(1)).setDateMailEnvoye(campagneAction, theDate);
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void testSendNotificationsOneByOne_1NotificationsToSend_ErrorOccursTwice_StopLoopWithoutSending() throws Exception {
		// Given
		EaeCampagne campagne = new EaeCampagne();
		campagne.setIdEaeCampagne(8);
		campagne.setAnnee(2014);
		List<EaeCampagneAction> notificationsToSend = new ArrayList<EaeCampagneAction>();
		EaeCampagneAction campagneAction = new EaeCampagneAction();
		campagneAction.setIdAgent(9);
		campagneAction.setEaeCampagne(campagne);
		notificationsToSend.add(campagneAction);
		
		IEaeCampagneActionDao eaeCampagneActionDaoMock = Mockito.mock(IEaeCampagneActionDao.class);
		when(eaeCampagneActionDaoMock.getEaeCampagneActionToSend(theDate)).thenReturn(notificationsToSend);

		AgentLdap agentLdap = new AgentLdap();
		IAgentLdapDao ldapMock = Mockito.mock(IAgentLdapDao.class);
		when(ldapMock.retrieveAgentFromLdapFromMatricule("90")).thenReturn(agentLdap);
		
		EaeCampagneActionNotificationsJob service = new	 EaeCampagneActionNotificationsJob();
		ReflectionTestUtils.setField(service, "helper", helperMock);
		ReflectionTestUtils.setField(service, "eaeCampagneActionDao", eaeCampagneActionDaoMock);
		ReflectionTestUtils.setField(service, "numberOfTries", 2);
		ReflectionTestUtils.setField(service, "agentLdapDao", ldapMock);
		
		EaeCampagneActionNotificationsJob serviceSpy = spy(service);
		doThrow(new Exception()).when(serviceSpy).sendEmail(eq(agentLdap), any(List.class), eq(campagneAction), eq(theDate));
		
		// When
		serviceSpy.sendNotificationsOneByOne();
		
		// Then
		//verify(eaeCampagneActionDaoMock, Mockito.never()).setDateMailEnvoye(campagneAction, theDate);
	}
	
}
