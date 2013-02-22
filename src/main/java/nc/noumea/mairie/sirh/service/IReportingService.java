package nc.noumea.mairie.sirh.service;

public interface IReportingService {

	public void getTableauAvancementsReportAndSaveItToFile(int idCap, int idCadreEmploi, String targetPath) throws Exception;
}
