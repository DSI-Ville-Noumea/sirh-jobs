package nc.noumea.mairie.sirh.ws.dto;

import java.util.Date;

public class DemandeDto {

	private AgentWithServiceDto	agentWithServiceDto;

	private Integer				idDemande;
	private Integer				idTypeDemande;
	private String				libelleTypeDemande;
	private Date				dateDebut;

	// pour l'envoi des mails
	private String				dateEnString;

	public DemandeDto() {
	}

	@Override
	public boolean equals(Object obj) {
		return idDemande.equals(((DemandeDto) obj).getIdDemande());
	}

	public AgentWithServiceDto getAgentWithServiceDto() {
		return agentWithServiceDto;
	}

	public void setAgentWithServiceDto(AgentWithServiceDto agentWithServiceDto) {
		this.agentWithServiceDto = agentWithServiceDto;
	}

	public Integer getIdDemande() {
		return idDemande;
	}

	public void setIdDemande(Integer idDemande) {
		this.idDemande = idDemande;
	}

	public Integer getIdTypeDemande() {
		return idTypeDemande;
	}

	public void setIdTypeDemande(Integer idTypeDemande) {
		this.idTypeDemande = idTypeDemande;
	}

	public String getLibelleTypeDemande() {
		return libelleTypeDemande;
	}

	public void setLibelleTypeDemande(String libelleTypeDemande) {
		this.libelleTypeDemande = libelleTypeDemande;
	}

	public Date getDateDebut() {
		return dateDebut;
	}

	public void setDateDebut(Date dateDebut) {
		this.dateDebut = dateDebut;
	}

	public String getDateEnString() {
		return dateEnString;
	}

	public void setDateEnString(String dateEnString) {
		this.dateEnString = dateEnString;
	}

}
