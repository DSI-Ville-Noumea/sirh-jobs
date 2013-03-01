package nc.noumea.mairie.sirh.tools;

public enum AvancementsWithEaesMassPrintJobStatusEnum {

	START("Job démarré"),
	AVCT_REPORT("Creation du tableau d'avancement"),
	EAE_DOWNLOAD("Téléchargement des EAEs"),
	QUEUE_PRINT("Mise en file d'impression"),
	HOUSEKEEPING("Nettoyage de l'environnement"),
	DONE("Envoi à l'imprimante terminé");
	
	private String status;
	
	private AvancementsWithEaesMassPrintJobStatusEnum(String _status) {
		this.status = _status;
	}
	
	@Override
	public String toString() {
		return status;
	}
}
