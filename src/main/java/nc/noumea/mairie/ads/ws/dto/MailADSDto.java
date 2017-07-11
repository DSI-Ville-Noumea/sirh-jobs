package nc.noumea.mairie.ads.ws.dto;

import java.util.List;

public class MailADSDto {
	List<String>	listeDestinataire;
	List<String>	listeCopie;
	List<String>	listeCopieCachee;

	public List<String> getListeDestinataire() {
		return listeDestinataire;
	}

	public void setListeDestinataire(List<String> listeDestinataire) {
		this.listeDestinataire = listeDestinataire;
	}

	public List<String> getListeCopie() {
		return listeCopie;
	}

	public void setListeCopie(List<String> listeCopie) {
		this.listeCopie = listeCopie;
	}

	public List<String> getListeCopieCachee() {
		return listeCopieCachee;
	}

	public void setListeCopieCachee(List<String> listeCopieCachee) {
		this.listeCopieCachee = listeCopieCachee;
	}

}
