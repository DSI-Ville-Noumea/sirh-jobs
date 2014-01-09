package nc.noumea.mairie.sirh.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class RedmineIncidentLoggerService implements IIncidentLoggerService {

	private Logger logger = LoggerFactory.getLogger(RedmineIncidentLoggerService.class);
	
	@Override
	public void logIncident(String jobName, String message, Throwable ex) {

		logger.info("Logging into redmine {}, {}, {}...", jobName, message, ex);
		
	}
	
}
