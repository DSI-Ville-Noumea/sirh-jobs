package nc.noumea.mairie.abs.domain;

public enum RefTypeAbsenceEnum {

	CONGE_ANNUEL(1), REPOS_COMP(2), RECUP(3), AUTRES(5), MALADIES(6), ASA_A48(7);

	private int type;

	private RefTypeAbsenceEnum(int _type) {
		type = _type;
	}

	public int getValue() {
		return type;
	}

	public static RefTypeAbsenceEnum getRefTypeAbsenceEnum(Integer type) {

		if (type == null)
			return null;

		switch (type) {
			case 1:
				return CONGE_ANNUEL;
			case 2:
				return REPOS_COMP;
			case 3:
				return RECUP;
			case 5:
				return AUTRES;
			case 6:
				return MALADIES;
			case 7:
				return ASA_A48;
			default:
				return null;
		}
	}
}
