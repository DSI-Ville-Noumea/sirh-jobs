package nc.noumea.mairie.sirh.service;

import java.util.Date;

public interface IReportingService {

	public void getTableauAvancementsReportAndSaveItToFile(int idCap, int idCadreEmploi, String targetPath) throws Exception;
	public void getAvctFirstLastPrintPage(String jobId, String jobUser, String codeCap, String cadreEmploi, Date submissionDate, boolean isFirst, boolean isEaes, String targetPath) throws Exception;
}
