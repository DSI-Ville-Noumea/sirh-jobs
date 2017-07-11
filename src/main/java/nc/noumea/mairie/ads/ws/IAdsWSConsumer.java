package nc.noumea.mairie.ads.ws;

import java.util.List;

import nc.noumea.mairie.ads.ws.dto.EntiteDto;
import nc.noumea.mairie.ads.ws.dto.EntiteHistoDto;
import nc.noumea.mairie.ads.ws.dto.MailADSDto;

public interface IAdsWSConsumer {

	List<EntiteHistoDto> getListeEntiteHistoChangementStatutVeille();

	EntiteDto getInfoSiservByIdEntite(Integer idEntite);

	MailADSDto getListeEmailInfo();
}
