package nc.noumea.mairie.sirh.eae.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.PersistenceUnit;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "EAE_CAMPAGNE_ACTION")
@PersistenceUnit(unitName = "eaePersistenceUnit")
@NamedQuery(
		name = "EaeCampagneAction.getTodayNotifications", 
		query = "SELECT eA from EaeCampagneAction eA WHERE (eA.dateTransmission <= :todayDate AND eA.dateMailEnvoye is NULL)"
		)
public class EaeCampagneAction {

	@Id
	@Column(name = "ID_CAMPAGNE_ACTION")
	private Integer idCampagneActeur;

	@OneToMany(mappedBy = "eaeCampagneAction", fetch = FetchType.EAGER, orphanRemoval = true)
	private Set<EaeCampagneActeur> eaeCampagneActeurs = new HashSet<EaeCampagneActeur>();
	
	@OneToMany(mappedBy = "eaeCampagneAction", fetch = FetchType.EAGER, orphanRemoval = true)
	private Set<EaeDocument> eaeDocuments = new HashSet<EaeDocument>();

	@Column(name = "NOM_ACTION")
	private String nomAction;

	@Column(name = "DATE_TRANSMISSION")
	@Temporal(TemporalType.DATE)
	private Date dateTransmission;
	
	@Column(name = "DATE_MAIL_ENVOYE")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateMailEnvoye;
	
	@Column(name = "DATE_A_FAIRE_LE")
	private Date dateAfaire;
	
	@Column(name = "COMMENTAIRE")
	private String commentaire;
	
	@Lob
	@Column(name = "MESSAGE")
	private String message;
	
	@Column(name = "ID_AGENT_REALISATION")
	private Integer idAgent;

	public Integer getIdCampagneActeur() {
		return idCampagneActeur;
	}

	public void setIdCampagneActeur(Integer idCampagneActeur) {
		this.idCampagneActeur = idCampagneActeur;
	}

	public Set<EaeCampagneActeur> getEaeCampagneActeurs() {
		return eaeCampagneActeurs;
	}

	public void setEaeCampagneActeurs(Set<EaeCampagneActeur> eaeCampagneActeurs) {
		this.eaeCampagneActeurs = eaeCampagneActeurs;
	}

	public Set<EaeDocument> getEaeDocuments() {
		return eaeDocuments;
	}

	public void setEaeDocuments(Set<EaeDocument> eaeDocuments) {
		this.eaeDocuments = eaeDocuments;
	}

	public String getNomAction() {
		return nomAction;
	}

	public void setNomAction(String nomAction) {
		this.nomAction = nomAction;
	}

	public Date getDateTransmission() {
		return dateTransmission;
	}

	public void setDateTransmission(Date dateTransmission) {
		this.dateTransmission = dateTransmission;
	}

	public Date getDateMailEnvoye() {
		return dateMailEnvoye;
	}

	public void setDateMailEnvoye(Date dateMailEnvoye) {
		this.dateMailEnvoye = dateMailEnvoye;
	}

	public Date getDateAfaire() {
		return dateAfaire;
	}

	public void setDateAfaire(Date dateAfaire) {
		this.dateAfaire = dateAfaire;
	}

	public String getCommentaire() {
		return commentaire;
	}

	public void setCommentaire(String commentaire) {
		this.commentaire = commentaire;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Integer getIdAgent() {
		return idAgent;
	}

	public void setIdAgent(Integer idAgent) {
		this.idAgent = idAgent;
	}
}
