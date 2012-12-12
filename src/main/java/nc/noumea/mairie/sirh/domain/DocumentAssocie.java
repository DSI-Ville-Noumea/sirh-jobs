package nc.noumea.mairie.sirh.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PersistenceUnit;
import javax.persistence.Table;

@Entity
@Table(name = "DOCUMENT_ASSOCIE", schema = "SIRH")
@PersistenceUnit(unitName = "sirhPersistenceUnit")
public class DocumentAssocie {

	@Id
	@Column(name = "ID_DOCUMENT")
	private Integer idDocument;
	
	@Column(name = "NOM_DOCUMENT")
	private String nomDocument;

	@Column(name = "LIEN_DOCUMENT")
	private String lienDocument;

	public Integer getIdDocument() {
		return idDocument;
	}

	public void setIdDocument(Integer idDocument) {
		this.idDocument = idDocument;
	}

	public String getNomDocument() {
		return nomDocument;
	}

	public void setNomDocument(String nomDocument) {
		this.nomDocument = nomDocument;
	}

	public String getLienDocument() {
		return lienDocument;
	}

	public void setLienDocument(String lienDocument) {
		this.lienDocument = lienDocument;
	}
}
