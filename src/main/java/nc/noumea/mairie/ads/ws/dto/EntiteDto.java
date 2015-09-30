package nc.noumea.mairie.ads.ws.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class EntiteDto {

	private Integer idEntite;
	private String sigle;
	private String label;
	private String labelCourt;
	private ReferenceDto typeEntite;
	private String codeServi;
	private List<EntiteDto> enfants;
	private EntiteDto entiteParent;
	private EntiteDto entiteRemplacee;

	private Integer idStatut;
	private Integer idAgentCreation;
	private Date dateCreation;
	private Integer idAgentModification;
	private Date dateModification;
	private String refDeliberationActif;
	private Date dateDeliberationActif;
	private String refDeliberationInactif;
	private Date dateDeliberationInactif;

	private String commentaire;
	private String nfa;

	private EntiteDto entiteDirection;
	private String codeServiAS400;

	private boolean entiteAs400;

	public EntiteDto() {
		enfants = new ArrayList<>();
	}

	public EntiteDto(EntiteDto entite) {
		mapEntite(entite);

		for (EntiteDto n : entite.getEnfants()) {
			this.enfants.add(new EntiteDto(n));
		}
	}

	public EntiteDto mapEntite(EntiteDto entite) {
		this.idEntite = entite.getIdEntite();
		this.sigle = entite.getSigle();
		this.label = entite.getLabel();
		this.labelCourt = entite.getLabelCourt();
		this.typeEntite = entite.getTypeEntite();
		this.codeServi = entite.getCodeServi();
		this.enfants = new ArrayList<>();
		this.entiteParent = null == entite.getEntiteParent() ? null : new EntiteDto(entite.getEntiteParent());
		this.entiteRemplacee = null == entite.getEntiteRemplacee() ? null : new EntiteDto(entite.getEntiteRemplacee());
		this.idStatut = entite.getIdStatut();
		this.idAgentCreation = entite.getIdAgentCreation();
		this.dateCreation = entite.getDateCreation();
		this.idAgentModification = entite.getIdAgentModification();
		this.dateModification = entite.getDateModification();
		this.refDeliberationActif = entite.getRefDeliberationActif();
		this.dateDeliberationActif = entite.getDateDeliberationActif();
		this.refDeliberationInactif = entite.getRefDeliberationInactif();
		this.dateDeliberationInactif = entite.getDateDeliberationInactif();
		this.commentaire = entite.getCommentaire();
		this.nfa = entite.getNfa();
		this.entiteAs400 = entite.isEntiteAs400();

		return this;
	}

	public Integer getIdEntite() {
		return idEntite;
	}

	public void setIdEntite(Integer idEntite) {
		this.idEntite = idEntite;
	}

	public String getSigle() {
		return sigle;
	}

	public void setSigle(String sigle) {
		this.sigle = sigle;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public ReferenceDto getTypeEntite() {
		return typeEntite;
	}

	public void setTypeEntite(ReferenceDto typeEntite) {
		this.typeEntite = typeEntite;
	}

	public String getCodeServi() {
		return codeServi;
	}

	public void setCodeServi(String codeServi) {
		this.codeServi = codeServi;
	}

	public List<EntiteDto> getEnfants() {
		return enfants;
	}

	public void setEnfants(List<EntiteDto> enfants) {
		this.enfants = enfants;
	}

	public Integer getIdAgentCreation() {
		return idAgentCreation;
	}

	public void setIdAgentCreation(Integer idAgentCreation) {
		this.idAgentCreation = idAgentCreation;
	}

	public Date getDateCreation() {
		return dateCreation;
	}

	public void setDateCreation(Date dateCreation) {
		this.dateCreation = dateCreation;
	}

	public Integer getIdAgentModification() {
		return idAgentModification;
	}

	public void setIdAgentModification(Integer idAgentModification) {
		this.idAgentModification = idAgentModification;
	}

	public Date getDateModification() {
		return dateModification;
	}

	public void setDateModification(Date dateModification) {
		this.dateModification = dateModification;
	}

	public String getRefDeliberationActif() {
		return refDeliberationActif;
	}

	public void setRefDeliberationActif(String refDeliberationActif) {
		this.refDeliberationActif = refDeliberationActif;
	}

	public Date getDateDeliberationActif() {
		return dateDeliberationActif;
	}

	public void setDateDeliberationActif(Date dateDeliberationActif) {
		this.dateDeliberationActif = dateDeliberationActif;
	}

	public String getRefDeliberationInactif() {
		return refDeliberationInactif;
	}

	public void setRefDeliberationInactif(String refDeliberationInactif) {
		this.refDeliberationInactif = refDeliberationInactif;
	}

	public Date getDateDeliberationInactif() {
		return dateDeliberationInactif;
	}

	public void setDateDeliberationInactif(Date dateDeliberationInactif) {
		this.dateDeliberationInactif = dateDeliberationInactif;
	}

	public EntiteDto getEntiteParent() {
		return entiteParent;
	}

	public void setEntiteParent(EntiteDto entiteParent) {
		this.entiteParent = entiteParent;
	}

	public EntiteDto getEntiteRemplacee() {
		return entiteRemplacee;
	}

	public void setEntiteRemplacee(EntiteDto entiteRemplacee) {
		this.entiteRemplacee = entiteRemplacee;
	}

	public String getLabelCourt() {
		return labelCourt;
	}

	public void setLabelCourt(String labelCourt) {
		this.labelCourt = labelCourt;
	}

	public Integer getIdStatut() {
		return idStatut;
	}

	public void setIdStatut(Integer idStatut) {
		this.idStatut = idStatut;
	}

	public String getCommentaire() {
		return commentaire;
	}

	public void setCommentaire(String commentaire) {
		this.commentaire = commentaire;
	}

	public EntiteDto getEntiteDirection() {
		return entiteDirection;
	}

	public void setEntiteDirection(EntiteDto entiteDirection) {
		this.entiteDirection = entiteDirection;
	}

	public String getNfa() {
		return nfa;
	}

	public void setNfa(String nfa) {
		this.nfa = nfa;
	}

	public boolean isEntiteAs400() {
		return entiteAs400;
	}

	public void setEntiteAs400(boolean entiteAs400) {
		this.entiteAs400 = entiteAs400;
	}

	public String getLibelleEntiteRemplacee() {
		if (this.entiteRemplacee == null) {
			return "";
		}

		return this.entiteRemplacee.getSigle();
	}

	public String getLibStatut() {
		return StatutEntiteEnum.getStatutEntiteEnum(this.idStatut) == null ? null : StatutEntiteEnum.getStatutEntiteEnum(this.idStatut).name();
	}

	public String getCodeServiAS400() {
		return codeServiAS400;
	}

	public void setCodeServiAS400(String codeServiAS400) {
		this.codeServiAS400 = codeServiAS400;
	}
}
