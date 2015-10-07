package nc.noumea.mairie.ads.ws.dto;

import java.text.SimpleDateFormat;
import java.util.Date;

public class EntiteHistoDto extends EntiteDto {

	private Integer idEntiteHisto;
	private Date dateHisto;
	private Integer idAgentHisto;
	private Integer typeHisto;

	public Integer getIdEntiteHisto() {
		return idEntiteHisto;
	}

	public void setIdEntiteHisto(Integer idEntiteHisto) {
		this.idEntiteHisto = idEntiteHisto;
	}

	public Date getDateHisto() {
		return dateHisto;
	}

	public void setDateHisto(Date dateHisto) {
		this.dateHisto = dateHisto;
	}

	public Integer getIdAgentHisto() {
		return idAgentHisto;
	}

	public void setIdAgentHisto(Integer idAgentHisto) {
		this.idAgentHisto = idAgentHisto;
	}

	public Integer getTypeHisto() {
		return typeHisto;
	}

	public void setTypeHisto(Integer typeHisto) {
		this.typeHisto = typeHisto;
	}

	public String getHeureHisto() {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		return sdf.format(this.dateHisto);
	}

}
