package nc.noumea.mairie.sirh.tools;

public interface IIncidentLoggerService {

	/**
	 * Cree un incident Redmine dans le projet SIRH-JOBS
	 * 
	 * @param jobName String Nom du JOB
	 * @param incidentRedmine VoRedmineIncidentLogger
	 */
	void logIncident(String jobName, VoRedmineIncidentLogger incidentRedmine);

	/**
	 * Cree un incident Redmine dans le projet SIRH-JOBS
	 * 
	 * @param jobName String Nom du JOB
	 * @param titreIncident Titre de l incident Redmine
	 * @param messageCustom Message perso ajoute a la description de l incident (Null accepte)
	 * @param ex l exception
	 */
	void logIncident(String jobName, String titreIncident,
			String messageCustom, Throwable ex);
	
}
