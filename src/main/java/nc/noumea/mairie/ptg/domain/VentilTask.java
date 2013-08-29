package nc.noumea.mairie.ptg.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PersistenceUnit;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "PTG_VENTIL_TASK")
@PersistenceUnit(unitName = "ptgPersistenceUnit")
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
	@Column(name = "AGENT_STATUT")
	private String  statut;
	
	@Column(name = "ID_TYPE_POINTAGE")
	private Integer idRefTypePointage;
	
	@NotNull
	@Column(name = "ID_VENTIL_DATE_FROM")
    private Integer idVentilDateFrom;
	
	@NotNull
	@Column(name = "ID_VENTIL_DATE_TO")
	private Integer idVentilDateTo;

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

	public String getStatut() {
		return statut;
	}

	public void setStatut(String statut) {
		this.statut = statut;
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
}
