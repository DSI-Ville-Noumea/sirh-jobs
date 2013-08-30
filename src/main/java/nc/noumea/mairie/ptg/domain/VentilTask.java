package nc.noumea.mairie.ptg.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PersistenceUnit;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "PTG_VENTIL_TASK")
@PersistenceUnit(unitName = "ptgPersistenceUnit")
@NamedQueries({
	@NamedQuery(
			name = "VentilTask.getNextVentilTask", 
			query = "SELECT vT from VentilTask vT WHERE vT.taskStatus is NULL AND vT.dateVentilation is NULL order by vT.idVentilTask asc)"
			)
})
public class VentilTask {

	@Id
	@Column(name = "ID_VENTIL_TASK")
	private Integer idVentilTask;
	
	@NotNull
	@Column(name = "ID_AGENT")
	private Integer idAgent;
	
	@NotNull
	@Column(name = "ID_AGENT_CREATION")
	private Integer idAgentCreation;
	
	@NotNull
	@Column(name = "DATE_CREATION")
	private Date dateCreation;
	
	@NotNull
	@Column(name = "TYPE_CHAINE_PAIE")
	private String  typeChainePaie;
	
	@Column(name = "ID_TYPE_POINTAGE")
	private Integer idRefTypePointage;
	
	@NotNull
	@Column(name = "ID_VENTIL_DATE_FROM")
    private Integer idVentilDateFrom;
	
	@NotNull
	@Column(name = "ID_VENTIL_DATE_TO")
	private Integer idVentilDateTo;

	@Column(name = "DATE_VENTILATION")
	private Date dateVentilation;
	
	@Column(name = "TASK_STATUS")
	private String taskStatus;
	
	public Integer getIdVentilTask() {
		return idVentilTask;
	}

	public void setIdVentilTask(Integer idVentilTask) {
		this.idVentilTask = idVentilTask;
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

	public String getTypeChainePaie() {
		return typeChainePaie;
	}

	public void setTypeChainePaie(String typeChainePaie) {
		this.typeChainePaie = typeChainePaie;
	}

	public Integer getIdRefTypePointage() {
		return idRefTypePointage;
	}

	public void setIdRefTypePointage(Integer idRefTypePointage) {
		this.idRefTypePointage = idRefTypePointage;
	}

	public Integer getIdVentilDateFrom() {
		return idVentilDateFrom;
	}

	public void setIdVentilDateFrom(Integer idVentilDateFrom) {
		this.idVentilDateFrom = idVentilDateFrom;
	}

	public Integer getIdVentilDateTo() {
		return idVentilDateTo;
	}

	public void setIdVentilDateTo(Integer idVentilDateTo) {
		this.idVentilDateTo = idVentilDateTo;
	}

	public Date getDateVentilation() {
		return dateVentilation;
	}

	public void setDateVentilation(Date dateVentilation) {
		this.dateVentilation = dateVentilation;
	}

	public String getTaskStatus() {
		return taskStatus;
	}

	public void setTaskStatus(String taskStatus) {
		this.taskStatus = taskStatus;
	}
}
