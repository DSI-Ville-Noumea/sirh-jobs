package nc.noumea.mairie.sirh.eae.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PersistenceUnit;
import javax.persistence.Table;

@Entity
@Table(name = "EAE_CAMPAGNE_EAE")
@PersistenceUnit(unitName = "eaePersistenceUnit")
public class EaeCampagne {

	@Id
	@Column(name = "ID_CAMPAGNE_EAE")
	private int idEaeCampagne;
	
	@Column(name = "ANNEE")
	private int annee;

	public int getIdEaeCampagne() {
		return idEaeCampagne;
	}

	public void setIdEaeCampagne(int idEaeCampagne) {
		this.idEaeCampagne = idEaeCampagne;
	}

	public int getAnnee() {
		return annee;
	}

	public void setAnnee(int annee) {
		this.annee = annee;
	}
}
