package nc.noumea.mairie.sirh.ws.dto;

import java.util.List;

public class ApprobateurWithAgentDto {

	private Integer idApprobateur;
	
	private List<AgentDto> agents;

	public Integer getIdApprobateur() {
		return idApprobateur;
	}

	public void setIdApprobateur(Integer idApprobateur) {
		this.idApprobateur = idApprobateur;
	}

	public List<AgentDto> getAgents() {
		return agents;
	}

	public void setAgents(List<AgentDto> agents) {
		this.agents = agents;
	}
	
	
}
