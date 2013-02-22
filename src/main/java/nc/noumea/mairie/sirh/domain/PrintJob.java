package nc.noumea.mairie.sirh.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PrintJob {

	private int idCap;
	
	private int idCadreEmploi;
	
	private String agentId;
	
	private Date submissionDate;
	
	private String status;
	
	private String jobId;

	private List<String> filesToPrint = new ArrayList<String>();
	
	public int getIdCap() {
		return idCap;
	}

	public void setIdCap(int idCap) {
		this.idCap = idCap;
	}

	public int getIdCadreEmploi() {
		return idCadreEmploi;
	}

	public void setIdCadreEmploi(int idCadreEmploi) {
		this.idCadreEmploi = idCadreEmploi;
	}

	public String getAgentId() {
		return agentId;
	}

	public void setAgentId(String agentId) {
		this.agentId = agentId;
	}

	public Date getSubmissionDate() {
		return submissionDate;
	}

	public void setSubmissionDate(Date submissionDate) {
		this.submissionDate = submissionDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getJobId() {
		return jobId;
	}

	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

	public List<String> getFilesToPrint() {
		return filesToPrint;
	}

	public void setFilesToPrint(List<String> filesToPrint) {
		this.filesToPrint = filesToPrint;
	}
}