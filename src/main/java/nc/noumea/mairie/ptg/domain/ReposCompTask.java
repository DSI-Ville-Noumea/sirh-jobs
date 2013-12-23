package nc.noumea.mairie.ptg.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PersistenceUnit;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "PTG_RC_TASK")
@PersistenceUnit(unitName = "ptgPersistenceUnit")
@NamedQueries({
	@NamedQuery(
			name = "ReposCompTask.getNextReposCompTask", 
			query = "select rcT from ReposCompTask rcT where rcT.taskStatus is NULL AND rcT.dateCalcul is NULL order by rcT.idRcTask asc)"
			)
})
public class ReposCompTask {

	@Id 
	@Column(name = "ID_RC_TASK")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer idRcTask;
	
	@NotNull
	@Column(name = "ID_AGENT")
	private Integer idAgent;
	
	@NotNull
	@Column(name = "ID_AGENT_CREATION")
	private Integer idAgentCreation;
	
	@NotNull
	@Column(name = "DATE_CREATION")
	private Date dateCreation;
	
	@Column(name = "ID_VENTIL_DATE")
	private Integer idVentilDate;
	
	@Column(name = "DATE_CALCUL")
	private Date dateCalcul;
	
	@Column(name = "TASK_STATUS")
	private String taskStatus;

	public Integer getIdRcTask() {
		return idRcTask;
	}

	public void setIdRcTask(Integer idRcTask) {
		this.idRcTask = idRcTask;
	}

	public Integer getIdAgent() {
		return idAgent;
	}

	public void setIdAgent(Integer idAgent) {
		this.idAgent = idAgent;
	}

	public Integer getIdAgentCreation() {
		return idAgentCreation;
	}

	public void setIdAgentCreation(Integer idAgentCreation) {
		this.idAgentCreation = idAgentCreation;
	}

	public Date getDateCreation() {
		return dateCreation;
	}

	public void setDateCreation(Date dateCreation) {
		this.dateCreation = dateCreation;
	}

	public Integer getIdVentilDate() {
		return idVentilDate;
	}

	public void setIdVentilDate(Integer idVentilDate) {
		this.idVentilDate = idVentilDate;
	}

	public Date getDateCalcul() {
		return dateCalcul;
	}

	public void setDateCalcul(Date dateCalcul) {
		this.dateCalcul = dateCalcul;
	}

	public String getTaskStatus() {
		return taskStatus;
	}

	public void setTaskStatus(String taskStatus) {
		this.taskStatus = taskStatus;
	}
}
