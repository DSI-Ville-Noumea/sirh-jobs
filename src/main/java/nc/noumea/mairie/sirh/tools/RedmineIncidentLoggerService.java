package nc.noumea.mairie.sirh.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.bean.Issue;

@Service
public class RedmineIncidentLoggerService implements IIncidentLoggerService {

	private Logger logger = LoggerFactory.getLogger(RedmineIncidentLoggerService.class);
	
	private static String redmineHost = "https://www.hostedredmine.com";
    private static String apiAccessKey = "a3221bfcef5750219bd0a2df69519416dba17fc9";
    private static String projectKey = "taskconnector-test";
    private static Integer queryId = null; // any
    
	@Override
	public void logIncident(String jobName, String message, Throwable ex) {

		logger.info("Logging into redmine {}, {}, {}...", jobName, message, ex);
		
		RedmineManager mgr = new RedmineManager(redmineHost, apiAccessKey);
		
		Issue issueToCreate = new Issue();
	    issueToCreate.setSubject("This is the summary line 123");
	    try {
			Issue newIssue = mgr.createIssue("SIRH-JOBS", issueToCreate);
		} catch (RedmineException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	}
	
}
