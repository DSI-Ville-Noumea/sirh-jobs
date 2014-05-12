package nc.noumea.mairie.sirh.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PersistenceUnit;
import javax.persistence.Table;

@Entity
@Table(name = "SIIDMA_SIRH")
@PersistenceUnit(unitName = "sirhPersistenceUnit")
public class SIIDMA_SIRH {

	@Id
	@Column(name = "ID_AGENT")
	private Integer idAgent;

	@Column(name = "LOGIN")
	private String login;

	@Column(name = "MAIL")
	private String Mail;

	@Column(name = "NOMATR")
	private Integer nomatr;

	public Integer getIdAgent() {
		return idAgent;
	}

	public void setIdAgent(Integer idAgent) {
		this.idAgent = idAgent;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getMail() {
		return Mail;
	}

	public void setMail(String mail) {
		Mail = mail;
	}

	public Integer getNomatr() {
		return nomatr;
	}

	public void setNomatr(Integer nomatr) {
		this.nomatr = nomatr;
	}
}
