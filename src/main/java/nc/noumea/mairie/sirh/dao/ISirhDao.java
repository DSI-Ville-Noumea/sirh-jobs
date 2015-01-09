package nc.noumea.mairie.sirh.dao;

import java.util.List;

public interface ISirhDao {

	List<Integer> getReferentRHService(String codeService);

	Integer getReferentRHGlobal();
}
