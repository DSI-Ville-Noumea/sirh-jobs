package nc.noumea.mairie.sirh.service;

import java.util.List;

public interface ISirhWsConsumer {
	public List<String> getEaesDocumentsGedIdFromAvct(int idCap, int idCadreEmploi);
}
