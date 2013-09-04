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
@Table(name = "PTG_EXPORT_PAIE_TASK")
@PersistenceUnit(unitName = "ptgPersistenceUnit")
@NamedQueries({
	@NamedQuery(
			name = "ExportPaieTask.getNextExportPaieTask", 
			query = "SELECT eT from ExportPaieTask eT WHERE eT.taskStatus is NULL AND eT.dateExport is NULL order by eT.idExportPaieTask asc)"
			)
})
public class ExportPaieTask {

	@Id
	@Column(name = "ID_EXPORT_PAIE_TASK")
	private Integer idExportPaieTask;
	
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
	
	@NotNull
	@Column(name = "ID_VENTIL_DATE")
	private Integer idVentilDate;

	@Column(name = "DATE_EXPORT")
	private Date dateExport;
	
	@Column(name = "TASK_STATUS")
	private String taskStatus;

	public Integer getIdExportPaieTask() {
		return idExportPaieTask;
	}

	public void setIdExportPaieTask(Integer idExportPaieTask) {
		this.idExportPaieTask = idExportPaieTask;
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

	public Integer getIdVentilDate() {
		return idVentilDate;
	}

	public void setIdVentilDate(Integer idVentilDate) {
		this.idVentilDate = idVentilDate;
	}

	public Date getDateExport() {
		return dateExport;
	}

	public void setDateExport(Date dateExport) {
		this.dateExport = dateExport;
	}

	public String getTaskStatus() {
		return taskStatus;
	}

	public void setTaskStatus(String taskStatus) {
		this.taskStatus = taskStatus;
	}
}
