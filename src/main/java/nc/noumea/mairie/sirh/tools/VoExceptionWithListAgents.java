package nc.noumea.mairie.sirh.tools;

import java.util.ArrayList;
import java.util.List;

public class VoExceptionWithListAgents {

	private String nameException;
	private String messageException;
	private Throwable exception;
	private List<Integer> listIds;
	
	public VoExceptionWithListAgents() {
		listIds = new ArrayList<Integer>();
	}
	
	public VoExceptionWithListAgents(String pNameException, String pMessageException, 
			Throwable pException, Integer id) {
		this();
		nameException = pNameException;
		messageException = pMessageException;
		exception = pException;
		addId(id);
	}
	
	public void addId(Integer id) {
		this.listIds.add(id);
	}
	
	public String getNameException() {
		return nameException;
	}
	
	public void setNameException(String nameException) {
		this.nameException = nameException;
	}
	
	public String getMessageException() {
		return messageException;
	}
	
	public void setMessageException(String messageException) {
		this.messageException = messageException;
	}

	public List<Integer> getListIds() {
		return listIds;
	}

	public void setListIds(List<Integer> listIds) {
		this.listIds = listIds;
	}

	public Throwable getException() {
		return exception;
	}

	public void setException(Throwable exception) {
		this.exception = exception;
	}
}
