package nc.noumea.mairie.sirh.ws.dto;

import java.util.List;

public class EmailInfoDto {

	private List<Integer> listViseurs;
	
	private List<Integer> listApprobateurs;

	public List<Integer> getListViseurs() {
		return listViseurs;
	}

	public void setListViseurs(List<Integer> listViseurs) {
		this.listViseurs = listViseurs;
	}

	public List<Integer> getListApprobateurs() {
		return listApprobateurs;
	}

	public void setListApprobateurs(List<Integer> listApprobateurs) {
		this.listApprobateurs = listApprobateurs;
	}
}
