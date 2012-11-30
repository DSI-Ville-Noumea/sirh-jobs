package nc.noumea.mairie.sirh.eae.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PersistenceUnit;
import javax.persistence.Table;

@Entity
@Table(name = "EAE_DOCUMENT")
@PersistenceUnit(unitName = "eaePersistenceUnit")
public class EaeDocument {

	@Id
	@Column(name = "ID_EAE_DOCUMENT")
	private Integer idEaeDocument;
	
	@ManyToOne
	@JoinColumn(name = "ID_CAMPAGNE_ACTION", referencedColumnName = "ID_CAMPAGNE_ACTION")
	private EaeCampagneAction eaeCampagneAction;
	
	@Column(name = "ID_DOCUMENT")
	private Integer sirhIdDocument;
	
	@Column(name = "TYPE_DOCUMENT")
	private String typeDocument;

	public Integer getIdEaeDocument() {
		return idEaeDocument;
	}

	public void setIdEaeDocument(Integer idEaeDocument) {
		this.idEaeDocument = idEaeDocument;
	}

	public EaeCampagneAction getEaeCampagneAction() {
		return eaeCampagneAction;
	}

	public void setEaeCampagneAction(EaeCampagneAction eaeCampagneAction) {
		this.eaeCampagneAction = eaeCampagneAction;
	}

	public Integer getSirhIdDocument() {
		return sirhIdDocument;
	}

	public void setSirhIdDocument(Integer sirhIdDocument) {
		this.sirhIdDocument = sirhIdDocument;
	}

	public String getTypeDocument() {
		return typeDocument;
	}

	public void setTypeDocument(String typeDocument) {
		this.typeDocument = typeDocument;
	}
}
