package nc.noumea.mairie.sirh.tools;

import java.util.ArrayList;
import java.util.List;

public class VoRedmineIncidentLogger {
	
	private String jobName;
	private List<VoExceptionWithListAgents> listException;
	
	public VoRedmineIncidentLogger() {
		listException = new ArrayList<VoExceptionWithListAgents>();
	}
	
	public VoRedmineIncidentLogger(String pJobName) {
		this();
		this.jobName = pJobName;
	}
	
	public void addException(Throwable exception, Integer id) {
		
		// on cherche d abord si l exception existe deja
		for(VoExceptionWithListAgents ex : listException) {
			if(null != exception
					&& ex.getNameException().equals(exception.getClass().getName())) {
				ex.addId(id);
				return;
			}
		}
		
		listException.add(new VoExceptionWithListAgents(exception.getClass().getName(), exception.getMessage(),
				exception, id));
	}
	
	public void addException(String nameException, String messageException, Throwable exception, Integer id) {
		
		@SuppressWarnings("unused")
		boolean isExceptionExist = false;
		// on cherche d abord si l exception existe deja
		for(VoExceptionWithListAgents ex : listException) {
			if(null != nameException
					&& ex.getNameException().equals(nameException)) {
				ex.addId(id);
				return;
			}
		}
		
		listException.add(new VoExceptionWithListAgents(nameException, messageException,
				exception, id));
	}

	public List<VoExceptionWithListAgents> getListException() {
		return listException;
	}

	public void setListException(List<VoExceptionWithListAgents> listException) {
		this.listException = listException;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	
}
