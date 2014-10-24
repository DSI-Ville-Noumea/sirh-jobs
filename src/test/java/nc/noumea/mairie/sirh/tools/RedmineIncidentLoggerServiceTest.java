package nc.noumea.mairie.sirh.tools;

import static org.junit.Assert.fail;

import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

public class RedmineIncidentLoggerServiceTest {

	/**
	 * This tests needs to remain deactivated. It is only here as a way to test
	 * the redmine api and code do work properly
	 */
//	@Test
	public void logIncident_IntegrationTest() {

		// Given
		String jobName = "theFakeJobName";
		String message = "The title of the incident";
		
		RedmineIncidentLoggerService logger = new RedmineIncidentLoggerService();
		ReflectionTestUtils.setField(logger, "redmineHost", "https://redmine.ville-noumea.nc");
		ReflectionTestUtils.setField(logger, "apiAccessKey", "f5fc338b2899e2ec2ad2e6d1e8a419be31c2b7c6"); // rayni84
		
		ReflectionTestUtils.setField(logger, "projectKey", "SIRH-JOBS");
		ReflectionTestUtils.setField(logger, "environnment", "dev");
		ReflectionTestUtils.setField(logger, "incidentTrackerName", "Incident");
		ReflectionTestUtils.setField(logger, "customFieldEnvironmentId", 38);
		ReflectionTestUtils.setField(logger, "customFieldEnvironmentName", "Env");
		ReflectionTestUtils.setField(logger, "customFieldJobNameId", 37);
		ReflectionTestUtils.setField(logger, "customFieldJobNameName", "Job");
		ReflectionTestUtils.setField(logger, "backlogVersionId", 56);

		// When
		try {
		logger.logIncident(jobName, message, new Exception(
				"this is the fake exception that needs to be logged in Redmine under Incident."));
		} catch(Exception e) {
			fail("probleme logger redmine : " + e.getMessage());
		}
		// Then
		// Verify in Redmine the creation of the above incident
	}
}
