package nc.noumea.mairie.sirh.dao;

import nc.noumea.mairie.sirh.domain.PrintJob;

public interface IPrintJobDao {

	public PrintJob getNextPrintJob();
	public void updateJobIdAndStatus(PrintJob job);
	public void updateStatus(PrintJob job);
}
