package nc.noumea.mairie.sirh.ws.dto;

import java.util.ArrayList;
import java.util.List;

public class ActeursDto {

	private List<AgentDto>			listOperateurs;

	private List<AgentDto>			listViseurs;

	private List<ApprobateurDto>	listApprobateurs;

	public ActeursDto() {
		this.listOperateurs = new ArrayList<AgentDto>();
		this.listViseurs = new ArrayList<AgentDto>();
		this.listApprobateurs = new ArrayList<ApprobateurDto>();
	}

	public List<AgentDto> getListOperateurs() {
		return listOperateurs;
	}

	public void setListOperateurs(List<AgentDto> listOperateurs) {
		this.listOperateurs = listOperateurs;
	}

	public List<AgentDto> getListViseurs() {
		return listViseurs;
	}

	public void setListViseurs(List<AgentDto> listViseurs) {
		this.listViseurs = listViseurs;
	}

	public List<ApprobateurDto> getListApprobateurs() {
		return listApprobateurs;
	}

	public void setListApprobateurs(List<ApprobateurDto> listApprobateurs) {
		this.listApprobateurs = listApprobateurs;
	}

}
