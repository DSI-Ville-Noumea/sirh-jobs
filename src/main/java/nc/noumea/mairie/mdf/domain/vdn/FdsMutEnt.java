package nc.noumea.mairie.mdf.domain.vdn;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PersistenceUnit;
import javax.persistence.Table;

@Entity
@Table(name = "FDSMUTENT")
public class FdsMutEnt {

	@Column(name = "TYPEN1", columnDefinition = "numeric")
	private Integer typeEnregistrement;

	@Column(name = "VERSION", columnDefinition = "numeric")
	private Integer version;

	@Column(name = "TELCTCT", columnDefinition = "numeric")
	private Integer telContactTech;

	@Column(name = "TELCTCT2", columnDefinition = "numeric")
	private Integer telContactSirh;

	@Column(name = "PERCOU", columnDefinition = "numeric")
	private Integer periodeCourante;
	
	@Column(name = "COLLEC1", columnDefinition = "char")
	private String codeCollectivité;
	
	@Column(name = "NOMFIC", columnDefinition = "char")
	private String nomFichier;
	
	@Column(name = "TYPFIC", columnDefinition = "char")
	private String typeFichier;
	
	@Column(name = "EDITEUR", columnDefinition = "char")
	private String editeur;
	
	@Column(name = "DATFIC", columnDefinition = "char")
	private String dateFichier;
	
	@Column(name = "NOMVER", columnDefinition = "char")
	private String nomLogiciel;
	
	@Column(name = "CONTACT", columnDefinition = "char")
	private String contactTech;
	
	@Column(name = "MELCTCT", columnDefinition = "char")
	private String mailContactTech;
	
	@Column(name = "CONTSIRH", columnDefinition = "char")
	private String contactSirh;
	
	@Column(name = "MELSIRH", columnDefinition = "char")
	private String mailContactSirh;

	public Integer getTypeEnregistrement() {
		return typeEnregistrement;
	}

	public void setTypeEnregistrement(Integer typeEnregistrement) {
		this.typeEnregistrement = typeEnregistrement;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public Integer getTelContactTech() {
		return telContactTech;
	}

	public void setTelContactTech(Integer telContactTech) {
		this.telContactTech = telContactTech;
	}

	public Integer getTelContactSirh() {
		return telContactSirh;
	}

	public void setTelContactSirh(Integer telContactSirh) {
		this.telContactSirh = telContactSirh;
	}

	public Integer getPeriodeCourante() {
		return periodeCourante;
	}

	public void setPeriodeCourante(Integer periodeCourante) {
		this.periodeCourante = periodeCourante;
	}

	public String getCodeCollectivité() {
		return codeCollectivité;
	}

	public void setCodeCollectivité(String codeCollectivité) {
		this.codeCollectivité = codeCollectivité;
	}

	public String getNomFichier() {
		return nomFichier;
	}

	public void setNomFichier(String nomFichier) {
		this.nomFichier = nomFichier;
	}

	public String getTypeFichier() {
		return typeFichier;
	}

	public void setTypeFichier(String typeFichier) {
		this.typeFichier = typeFichier;
	}

	public String getEditeur() {
		return editeur;
	}

	public void setEditeur(String editeur) {
		this.editeur = editeur;
	}

	public String getDateFichier() {
		return dateFichier;
	}

	public void setDateFichier(String dateFichier) {
		this.dateFichier = dateFichier;
	}

	public String getNomLogiciel() {
		return nomLogiciel;
	}

	public void setNomLogiciel(String nomLogiciel) {
		this.nomLogiciel = nomLogiciel;
	}

	public String getContactTech() {
		return contactTech;
	}

	public void setContactTech(String contactTech) {
		this.contactTech = contactTech;
	}

	public String getMailContactTech() {
		return mailContactTech;
	}

	public void setMailContactTech(String mailContactTech) {
		this.mailContactTech = mailContactTech;
	}

	public String getContactSirh() {
		return contactSirh;
	}

	public void setContactSirh(String contactSirh) {
		this.contactSirh = contactSirh;
	}

	public String getMailContactSirh() {
		return mailContactSirh;
	}

	public void setMailContactSirh(String mailContactSirh) {
		this.mailContactSirh = mailContactSirh;
	}

}
