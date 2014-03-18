package nc.noumea.mairie.sirh.tools;

import org.springframework.test.util.ReflectionTestUtils;

public class RedmineIncidentLoggerServiceTest {

	/**
	 * This tests needs to remain deactivated. It is only here 
	 * as a way to test the redmine api and code do work properly
	 */
//	@Test
	public void logIncident_IntegrationTest() {
		
		// Given
		String jobName = "theFakeJobName";
		String message = "The title of the incident";
		
		RedmineIncidentLoggerService logger = new RedmineIncidentLoggerService();
		ReflectionTestUtils.setField(logger, "redmineHost", "https://redmine.ville-noumea.nc");
		ReflectionTestUtils.setField(logger, "apiAccessKey", "af15c5156e05b5a1e7145a3913414873c5c2e8f3"); //rayni84
		ReflectionTestUtils.setField(logger, "projectKey", "sirh-jobs");
		ReflectionTestUtils.setField(logger, "environnment", "TEST");
		ReflectionTestUtils.setField(logger, "incidentTrackerName", "Incident");
		ReflectionTestUtils.setField(logger, "customFieldEnvironmentId", 38);
		ReflectionTestUtils.setField(logger, "customFieldEnvironmentName", "Env");
		ReflectionTestUtils.setField(logger, "customFieldJobNameId", 37);
		ReflectionTestUtils.setField(logger, "customFieldJobNameName", "Job");
		
		// When
		logger.logIncident(jobName, message, new Exception("this is the fake exception that needs to be logged in Redmine under Incident."));
		
		// Then
		// Verify in Redmine the creation of the above incident
	}
}
