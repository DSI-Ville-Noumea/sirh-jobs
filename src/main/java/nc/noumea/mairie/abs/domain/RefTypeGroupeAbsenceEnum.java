package nc.noumea.mairie.abs.domain;

public enum RefTypeGroupeAbsenceEnum {

	RECUP(1), REPOS_COMP(2), ASA(3), CONGES_EXCEP(4), NOT_EXIST(99);

	private int type;

	private RefTypeGroupeAbsenceEnum(int _type) {
		type = _type;
	}

	public int getValue() {
		return type;
	}

	public static RefTypeGroupeAbsenceEnum getRefTypeGroupeAbsenceEnum(Integer type) {

		if (type == null)
			return null;

		switch (type) {
			case 1:
				return RECUP;
			case 2:
				return REPOS_COMP;
			case 3:
				return ASA;
			case 4:
				return CONGES_EXCEP;
			default:
				return NOT_EXIST;
		}
	}
}
