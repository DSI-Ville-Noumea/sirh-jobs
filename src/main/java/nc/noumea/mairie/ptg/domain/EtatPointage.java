package nc.noumea.mairie.ptg.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PersistenceUnit;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "PTG_ETAT_POINTAGE")
@PersistenceUnit(unitName = "ptgPersistenceUnit")
public class EtatPointage {

	@Id
	@SequenceGenerator(name = "etatPointageGen", sequenceName = "PTG_S_ETAT_POINTAGE")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "etatPointageGen")
	@Column(name = "ID_ETAT_POINTAGE")
	private Integer idEtatPointage;
	
	@NotNull
	@Column(name = "ID_POINTAGE")
	private Integer idPointage;

	@Column(name = "DATE_ETAT")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateEtat; 
	
	@Column(name = "DATE_MAJ")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateMaj;
	
	@NotNull
	@Column(name = "ETAT")
	@Enumerated(EnumType.ORDINAL)
	private EtatPointageEnum etat; 

	@NotNull
	@Column(name = "ID_AGENT")
	private Integer idAgent;
	
	@NotNull
	@Column(name = "VERSION")
	private Integer version;


	public Integer getIdEtatPointage() {
		return idEtatPointage;
	}


	public void setIdEtatPointage(Integer idEtatPointage) {
		this.idEtatPointage = idEtatPointage;
	}


	public Integer getIdPointage() {
		return idPointage;
	}


	public void setIdPointage(Integer idPointage) {
		this.idPointage = idPointage;
	}


	public Date getDateEtat() {
		return dateEtat;
	}


	public void setDateEtat(Date dateEtat) {
		this.dateEtat = dateEtat;
	}


	public Date getDateMaj() {
		return dateMaj;
	}


	public void setDateMaj(Date dateMaj) {
		this.dateMaj = dateMaj;
	}


	public EtatPointageEnum getEtat() {
		return etat;
	}


	public void setEtat(EtatPointageEnum etat) {
		this.etat = etat;
	}


	public Integer getIdAgent() {
		return idAgent;
	}


	public void setIdAgent(Integer idAgent) {
		this.idAgent = idAgent;
	}


	public Integer getVersion() {
		return version;
	}


	public void setVersion(Integer version) {
		this.version = version;
	}
	
	
}
