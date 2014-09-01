package nc.noumea.mairie.sirh.tools;

import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.bean.CustomField;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.Tracker;
import com.taskadapter.redmineapi.bean.Version;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class RedmineIncidentLoggerService implements IIncidentLoggerService {

	private Logger logger = LoggerFactory.getLogger(RedmineIncidentLoggerService.class);

	@Autowired
	@Qualifier("SIRH_JOBS_REDMINE_API_URL")
	private String redmineHost;

	@Autowired
	@Qualifier("SIRH_JOBS_REDMINE_API_KEY")
	private String apiAccessKey;

	@Autowired
	@Qualifier("SIRH_JOBS_REDMINE_PROJECT_KEY")
	private String projectKey;

	@Autowired
	@Qualifier("SIRH_JOBS_REDMINE_ENV")
	private String environnment;

	@Autowired
	@Qualifier("SIRH_JOBS_REDMINE_INCIDENT_TRACKER_NAME")
	private String incidentTrackerName;
	
	@Autowired
	@Qualifier("SIRH_JOBS_REDMINE_CF_ENV_FIELD_ID")
	private Integer customFieldEnvironmentId;
	
	@Autowired
	@Qualifier("SIRH_JOBS_REDMINE_CF_ENV_FIELD_NAME")
	private String customFieldEnvironmentName;
	
	@Autowired
	@Qualifier("SIRH_JOBS_REDMINE_CF_JOBNAME_FIELD_ID")
	private Integer customFieldJobNameId;
	
	@Autowired
	@Qualifier("SIRH_JOBS_REDMINE_CF_JOBNAME_FIELD_NAME")
	private String customFieldJobNameName;
	
	@Autowired
	@Qualifier("SIRH_JOBS_REDMINE_VERSION_BACKLOG_ID")
	private Integer backlogVersionId;
	
	@Override
	public void logIncident(String jobName, String message, Throwable ex) {

		logger.info("Logging into redmine {}, {}, {}...", jobName, message, ex);

		if (StringUtils.isBlank(environnment)) {
			logger.info("Environment variable is not properly set: SIRH-JOBS will not create the redmine issue.");
			return;
		}
		
		if (StringUtils.isBlank(jobName)) {
			logger.info("JobName parameter is not properly set: SIRH-JOBS will not create the redmine issue.");
			return;
		}

		RedmineManager mgr = new RedmineManager(redmineHost, apiAccessKey);

		try {
			Tracker incidentTracker = mgr.getProjectByKey(projectKey).getTrackerByName(incidentTrackerName);
			CustomField envField = new CustomField(customFieldEnvironmentId, customFieldEnvironmentName, environnment);
			CustomField jobNameField = new CustomField(customFieldJobNameId, customFieldJobNameName, jobName);
			Version version = mgr.getVersionById(backlogVersionId) ;

			Issue issueToCreate = new Issue();
			issueToCreate.setTracker(incidentTracker);
			issueToCreate.setSubject(message);
			issueToCreate.setTargetVersion(version);
			
			issueToCreate.setDescription(String.format("**%s**\r\n<pre>%s</pre>", ex.getMessage(), ExceptionUtils.getStackTrace(ex)));
			issueToCreate.getCustomFields().add(envField);
			issueToCreate.getCustomFields().add(jobNameField);
			
			Issue createdIssue = mgr.createIssue(projectKey, issueToCreate);
			
			logger.info("Succesfully created issue #{}", createdIssue.getId());

		} catch (RedmineException e) {
			logger.error(String.format("An error occured while trying to save the exception and message for job name [%s]", jobName), e);
		}

	}

}
