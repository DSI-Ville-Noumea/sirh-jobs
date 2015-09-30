package nc.noumea.mairie.ads.ws;

import java.util.List;

import nc.noumea.mairie.ads.ws.dto.EntiteDto;
import nc.noumea.mairie.ads.ws.dto.EntiteHistoDto;

public interface IAdsWSConsumer {

	List<EntiteHistoDto> getListeEntiteHistoChangementStatutVeille();

	List<Integer> getListeIdAgentEmailInfo();

	EntiteDto getInfoSiservByIdEntite(Integer idEntite);
}
