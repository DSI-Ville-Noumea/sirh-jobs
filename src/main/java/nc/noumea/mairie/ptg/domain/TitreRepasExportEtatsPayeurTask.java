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
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "PTG_TR_EXPORT_ETATS_PAYEUR_TASK")
@PersistenceUnit(unitName = "ptgPersistenceUnit")
@NamedQueries({
		@NamedQuery(name = "TitreRepasExportEtatsPayeurTask.getNextTitreRepasExportEtatsPayeurTask", query = "SELECT eT from TitreRepasExportEtatsPayeurTask eT WHERE eT.taskStatus is NULL AND eT.dateExport is NULL order by eT.idTitreRepasExportEtatsPayeurTask asc)") })
public class TitreRepasExportEtatsPayeurTask {

	@Id
	@Column(name = "ID_TR_EXPORT_ETATS_PAYEUR_TASK")
	private Integer	idTitreRepasExportEtatsPayeurTask;

	@Column(name = "ID_AGENT")
	private Integer	idAgent;

	@NotNull
	@Column(name = "DATE_MONTH", nullable = false)
	@Temporal(TemporalType.DATE)
	private Date dateMonth;

	@Column(name = "DATE_CREATION")
	@Temporal(TemporalType.TIMESTAMP)
	private Date	dateCreation;

	@Column(name = "DATE_EXPORT")
	@Temporal(TemporalType.TIMESTAMP)
	private Date	dateExport;

	@Column(name = "TASK_STATUS")
	private String	taskStatus;

	public Integer getIdTitreRepasExportEtatsPayeurTask() {
		return idTitreRepasExportEtatsPayeurTask;
	}

	public void setIdTitreRepasExportEtatsPayeurTask(Integer idTitreRepasExportEtatsPayeurTask) {
		this.idTitreRepasExportEtatsPayeurTask = idTitreRepasExportEtatsPayeurTask;
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

	public String getTaskStatus() {
		return taskStatus;
	}

	public void setTaskStatus(String taskStatus) {
		this.taskStatus = taskStatus;
	}

	public Date getDateMonth() {
		return dateMonth;
	}

	public void setDateMonth(Date dateMonth) {
		this.dateMonth = dateMonth;
	}
}
