package nc.noumea.mairie.sirh.domain;

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
@Table(name = "ACTION_FDP_JOB")
@PersistenceUnit(unitName = "sirhPersistenceUnit")
@NamedQueries({
		@NamedQuery(name = "ActionFDPJob.getNextSuppressionFDPTask", query = "SELECT eT from ActionFDPJob eT WHERE eT.typeAction = 'SUPPRESSION' and eT.statut is NULL AND eT.dateStatut is NULL order by eT.idActionFdpJob asc)"),
		@NamedQuery(name = "ActionFDPJob.getNextDuplicationFDPTask", query = "SELECT eT from ActionFDPJob eT WHERE eT.typeAction = 'DUPLICATION' and eT.statut is NULL AND eT.dateStatut is NULL order by eT.idActionFdpJob asc)") })
public class ActionFDPJob {

	@Id
	@Column(name = "ID_ACTION_FDP_JOB")
	private Integer idActionFdpJob;

	@Column(name = "ID_AGENT")
	@NotNull
	private Integer idAgent;

	@Column(name = "ID_FICHE_POSTE")
	@NotNull
	private Integer idFichePoste;

	@Column(name = "ID_NEW_SERVICE_ADS")
	private Integer idNewServiceAds;

	@Column(name = "TYPE_ACTION")
	@NotNull
	private String typeAction;

	@Column(name = "STATUT")
	private String statut;

	@Column(name = "DATE_SUBMISSION")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateSubmission;

	@Column(name = "DATE_STATUT")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateStatut;

	public Integer getIdActionFdpJob() {
		return idActionFdpJob;
	}

	public void setIdActionFdpJob(Integer idActionFdpJob) {
		this.idActionFdpJob = idActionFdpJob;
	}

	public Integer getIdAgent() {
		return idAgent;
	}

	public void setIdAgent(Integer idAgent) {
		this.idAgent = idAgent;
	}

	public Integer getIdFichePoste() {
		return idFichePoste;
	}

	public void setIdFichePoste(Integer idFichePoste) {
		this.idFichePoste = idFichePoste;
	}

	public Integer getIdNewServiceAds() {
		return idNewServiceAds;
	}

	public void setIdNewServiceAds(Integer idNewServiceAds) {
		this.idNewServiceAds = idNewServiceAds;
	}

	public String getTypeAction() {
		return typeAction;
	}

	public void setTypeAction(String typeAction) {
		this.typeAction = typeAction;
	}

	public String getStatut() {
		return statut;
	}

	public void setStatut(String statut) {
		this.statut = statut;
	}

	public Date getDateSubmission() {
		return dateSubmission;
	}

	public void setDateSubmission(Date dateSubmission) {
		this.dateSubmission = dateSubmission;
	}

	public Date getDateStatut() {
		return dateStatut;
	}

	public void setDateStatut(Date dateStatut) {
		this.dateStatut = dateStatut;
	}
}
