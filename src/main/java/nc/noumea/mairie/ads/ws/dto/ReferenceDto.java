package nc.noumea.mairie.ads.ws.dto;


public class ReferenceDto {

	private Integer	id;
	private String	label;

	public ReferenceDto() {
	}

	public ReferenceDto(String label) {
		this();
		this.label = label;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
}
