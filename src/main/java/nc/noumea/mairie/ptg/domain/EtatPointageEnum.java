package nc.noumea.mairie.ptg.domain;

public enum EtatPointageEnum {

	SAISI(0), APPROUVE(1), REFUSE(2), REFUSE_DEFINITIVEMENT(3), VENTILE(4), REJETE(
			5), REJETE_DEFINITIVEMENT(6), VALIDE(7), EN_ATTENTE(8), JOURNALISE(
			9);

	private int codeEtat;

	EtatPointageEnum(int _value) {
		codeEtat = _value;
	}

	public int getCodeEtat() {
		return codeEtat;
	}

	@Override
	public String toString() {
		return String.valueOf(codeEtat);
	}

	public static EtatPointageEnum getEtatPointageEnum(Integer codeEtat) {

		if (codeEtat == null)
			return null;
		
		switch (codeEtat) {
			case 0:
				return SAISI;
			case 1:
				return APPROUVE;
			case 2:
				return REFUSE;
			case 3:
				return REFUSE_DEFINITIVEMENT;
			case 4:
				return VENTILE;
			case 5:
				return REJETE;
			case 6:
				return REJETE_DEFINITIVEMENT;
			case 7:
				return VALIDE;
			case 8:
				return EN_ATTENTE;
			case 9:
				return JOURNALISE;
			default:
				return null;
		}
	}

}
