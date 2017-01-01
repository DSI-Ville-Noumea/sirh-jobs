package nc.noumea.mairie.sirh.service;

import java.io.InputStream;
import java.util.Date;

public interface IReportingService {

	public InputStream getTableauAvancementsReport(int idCap, int idCadreEmploi, boolean avisEAE,Integer idAgentConnecte) 
			throws Exception;

	public InputStream getAvctFirstLastPrintPage(String jobId, String jobUser, String codeCap, String cadreEmploi,
			Date submissionDate, boolean isFirst, boolean isEaes) throws Exception;
}
