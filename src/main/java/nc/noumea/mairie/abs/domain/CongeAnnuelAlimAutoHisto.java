package nc.noumea.mairie.abs.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PersistenceUnit;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "ABS_CA_ALIM_AUTO_HISTO")
@PersistenceUnit(unitName = "absPersistenceUnit")
public class CongeAnnuelAlimAutoHisto {
	
	@Id
	@Column(name = "ID_CA_ALIM_AUTO_HISTO")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer idCongeAnnuelAlimAutoHisto;
	
	@Column(name = "ID_AGENT")
	private Integer idAgent;
	
	@Column(name = "DATE_MONTH")
	@Temporal(TemporalType.DATE)
	private Date dateMonth;
	
	@Column(name = "DATE_MODIFICATION")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateModification;
	
	@Column(name = "STATUS")
	private String status;

	public Integer getIdCongeAnnuelAlimAutoHisto() {
		return idCongeAnnuelAlimAutoHisto;
	}

	public void setIdCongeAnnuelAlimAutoHisto(Integer idCongeAnnuelAlimAutoHisto) {
		this.idCongeAnnuelAlimAutoHisto = idCongeAnnuelAlimAutoHisto;
	}

	public Integer getIdAgent() {
		return idAgent;
	}

	public void setIdAgent(Integer idAgent) {
		this.idAgent = idAgent;
	}

	public Date getDateMonth() {
		return dateMonth;
	}

	public void setDateMonth(Date dateMonth) {
		this.dateMonth = dateMonth;
	}

	public Date getDateModification() {
		return dateModification;
	}

	public void setDateModification(Date dateModification) {
		this.dateModification = dateModification;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
}
