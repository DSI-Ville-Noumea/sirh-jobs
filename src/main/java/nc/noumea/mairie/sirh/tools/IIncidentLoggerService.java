package nc.noumea.mairie.sirh.tools;

public interface IIncidentLoggerService {

	void logIncident(String jobName, String message, Throwable ex);
	
}
