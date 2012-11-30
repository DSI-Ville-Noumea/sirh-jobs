package nc.noumea.mairie.sirh.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PersistenceUnit;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "AGENT", schema = "SIRH")
@PersistenceUnit(unitName = "sirhPersistenceUnit")
public class Agent {

	@Id
	@Column(name = "ID_AGENT")
	private Integer idAgent;
	
    @Column(name = "NOMATR")
    private Integer nomatr;

    @Column(name = "NOM_MARITAL")
    private String nomMarital;

    @Column(name = "NOM_PATRONYMIQUE")
    private String nomPatronymique;
    
    @Column(name = "NOM_USAGE")
    private String nomUsage;
    
    @Column(name = "PRENOM")
    private String prenom;
    
    @Column(name = "PRENOM_USAGE")
    private String prenomUsage;
    
    @Column(name = "DATE_NAISSANCE")
    @Temporal(TemporalType.DATE)
    private Date dateNaissance;

	public Integer getIdAgent() {
		return idAgent;
	}

	public void setIdAgent(Integer idAgent) {
		this.idAgent = idAgent;
	}

	public Integer getNomatr() {
		return nomatr;
	}

	public void setNomatr(Integer nomatr) {
		this.nomatr = nomatr;
	}

	public String getNomMarital() {
		return nomMarital;
	}

	public void setNomMarital(String nomMarital) {
		this.nomMarital = nomMarital;
	}

	public String getNomPatronymique() {
		return nomPatronymique;
	}

	public void setNomPatronymique(String nomPatronymique) {
		this.nomPatronymique = nomPatronymique;
	}

	public String getNomUsage() {
		return nomUsage;
	}

	public void setNomUsage(String nomUsage) {
		this.nomUsage = nomUsage;
	}

	public String getPrenom() {
		return prenom;
	}

	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}

	public String getPrenomUsage() {
		return prenomUsage;
	}

	public void setPrenomUsage(String prenomUsage) {
		this.prenomUsage = prenomUsage;
	}

	public Date getDateNaissance() {
		return dateNaissance;
	}

	public void setDateNaissance(Date dateNaissance) {
		this.dateNaissance = dateNaissance;
	}
}
