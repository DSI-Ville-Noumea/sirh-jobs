package nc.noumea.mairie.ads.ws.dto;

import java.util.ArrayList;
import java.util.List;

public enum StatutEntiteEnum {

	PREVISION(0), ACTIF(1), TRANSITOIRE(2), INACTIF(3);

	private int idRefStatutEntite;

	StatutEntiteEnum(int _value) {
		idRefStatutEntite = _value;
	}

	public int getIdRefStatutEntite() {
		return idRefStatutEntite;
	}

	public static StatutEntiteEnum getStatutEntiteEnum(Integer idRefStatutEntite) {

		if (idRefStatutEntite == null)
			return null;

		switch (idRefStatutEntite) {
			case 0:
				return PREVISION;
			case 1:
				return ACTIF;
			case 2:
				return TRANSITOIRE;
			case 3:
				return INACTIF;
			default:
				return null;
		}
	}

	public static List<StatutEntiteEnum> getAllStatutEntiteEnum() {
		List<StatutEntiteEnum> result = new ArrayList<StatutEntiteEnum>();
		result.add(PREVISION);
		result.add(ACTIF);
		result.add(TRANSITOIRE);
		result.add(INACTIF);
		return result;
	}
}
