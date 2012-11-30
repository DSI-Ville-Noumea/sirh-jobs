package nc.noumea.mairie.sirh.eae.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PersistenceUnit;
import javax.persistence.Table;

@Entity
@Table(name = "EAE_CAMPAGNE_ACTEURS")
@PersistenceUnit(unitName = "eaePersistenceUnit")
public class EaeCampagneActeur {

	@Id
	@Column(name = "ID_CAMPAGNE_ACTEURS")
	private Integer idCampagneActeur;
	
	@Column(name = "ID_AGENT")
	private Integer idAgent;
	
	@ManyToOne
	@JoinColumn(name = "ID_CAMPAGNE_ACTION", referencedColumnName = "ID_CAMPAGNE_ACTION")
	private EaeCampagneAction eaeCampagneAction;

	public Integer getIdAgent() {
		return idAgent;
	}

	public void setIdAgent(Integer idAgent) {
		this.idAgent = idAgent;
	}

	public EaeCampagneAction getEaeCampagneAction() {
		return eaeCampagneAction;
	}

	public void setEaeCampagneAction(EaeCampagneAction eaeCampagneAction) {
		this.eaeCampagneAction = eaeCampagneAction;
	}

	public Integer getIdCampagneActeur() {
		return idCampagneActeur;
	}

	public void setIdCampagneActeur(Integer idCampagneActeur) {
		this.idCampagneActeur = idCampagneActeur;
	}
}
