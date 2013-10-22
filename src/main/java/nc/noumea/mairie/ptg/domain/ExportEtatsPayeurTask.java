package nc.noumea.mairie.ptg.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PersistenceUnit;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "PTG_EXPORT_ETATS_PAYEUR_TASK")
@PersistenceUnit(unitName = "ptgPersistenceUnit")
@NamedQueries({
	@NamedQuery(
			name = "ExportEtatsPayeurTask.getNextExportEtatsPayeurTask", 
			query = "SELECT eT from ExportEtatsPayeurTask eT WHERE eT.taskStatus is NULL AND eT.dateExport is NULL order by eT.idExportEtatsPayeurTask asc)"
			)
})
public class ExportEtatsPayeurTask {

	@Id
	@Column(name = "ID_EXPORT_ETATS_PAYEUR_TASK")
	private Integer idExportEtatsPayeurTask;

	@Column(name = "ID_AGENT")
	private Integer idAgent;

	@Column(name = "DATE_CREATION")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateCreation;

	@Column(name = "DATE_EXPORT")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateExport;

	@Column(name = "TYPE_CHAINE_PAIE")
	private String typeChainePaie;

	@Column(name = "ID_VENTIL_DATE")
	private Integer idVentilDate;

	@Column(name = "TASK_STATUS")
	private String taskStatus;

	public Integer getIdExportEtatsPayeurTask() {
		return idExportEtatsPayeurTask;
	}

	public void setIdExportEtatsPayeurTask(Integer idExportEtatsPayeurTask) {
		this.idExportEtatsPayeurTask = idExportEtatsPayeurTask;
	}

	public Integer getIdAgent() {
		return idAgent;
	}

	public void setIdAgent(Integer idAgent) {
		this.idAgent = idAgent;
	}

	public Date getDateCreation() {
		return dateCreation;
	}

	public void setDateCreation(Date dateCreation) {
		this.dateCreation = dateCreation;
	}

	public Date getDateExport() {
		return dateExport;
	}

	public void setDateExport(Date dateExport) {
		this.dateExport = dateExport;
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

	public String getTaskStatus() {
		return taskStatus;
	}

	public void setTaskStatus(String taskStatus) {
		this.taskStatus = taskStatus;
	}
}
