package nc.noumea.mairie.sirh.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PersistenceUnit;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

@Entity
@Table(name = "AVCT_CAP_PRINT_JOB")
@PersistenceUnit(unitName = "sirhPersistenceUnit")
public class AvctCapPrintJob {

	@Id
	@Column(name = "ID_AVCT_CAP_PRINT_JOB")
	private int idAvctCapPrintJob;
	
	@Column(name = "ID_AGENT")
	private int agentId;

	@Column(name = "LOGIN")
	private String login;

	@Column(name = "ID_CAP")
	private int idCap;
	
	@Column(name = "CODE_CAP")
	private String codeCap;
	
	@Column(name = "ID_CADRE_EMPLOI")
	private int idCadreEmploi;
	
	@Column(name = "LIB_CADRE_EMPLOI")
	private String libCadreEmploi;
	
	@Column(name = "IS_EAES")
	private boolean eaes;
	
	@Column(name = "DATE_SUBMISSION")
    @Temporal(TemporalType.TIMESTAMP)
	private Date submissionDate;
	
	@Column(name = "STATUT")
	private String status;
	
	@Column(name = "DATE_STATUT")
    @Temporal(TemporalType.TIMESTAMP)
	private Date statusDate;
	
	@Column(name = "JOB_ID")
	private String jobId;

	@Transient
	private List<String> filesToPrint = new ArrayList<String>();

	public int getIdAvctCapPrintJob() {
		return idAvctCapPrintJob;
	}

	public void setIdAvctCapPrintJob(int idAvctCapPrintJob) {
		this.idAvctCapPrintJob = idAvctCapPrintJob;
	}

	public int getAgentId() {
		return agentId;
	}

	public void setAgentId(int agentId) {
		this.agentId = agentId;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public int getIdCap() {
		return idCap;
	}

	public void setIdCap(int idCap) {
		this.idCap = idCap;
	}

	public String getCodeCap() {
		return codeCap;
	}

	public void setCodeCap(String codeCap) {
		this.codeCap = codeCap;
	}

	public int getIdCadreEmploi() {
		return idCadreEmploi;
	}

	public void setIdCadreEmploi(int idCadreEmploi) {
		this.idCadreEmploi = idCadreEmploi;
	}

	public String getLibCadreEmploi() {
		return libCadreEmploi;
	}

	public void setLibCadreEmploi(String libCadreEmploi) {
		this.libCadreEmploi = libCadreEmploi;
	}

	public boolean isEaes() {
		return eaes;
	}

	public void setEaes(boolean eaes) {
		this.eaes = eaes;
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

	public Date getStatusDate() {
		return statusDate;
	}

	public void setStatusDate(Date statusDate) {
		this.statusDate = statusDate;
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