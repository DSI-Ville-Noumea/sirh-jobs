package nc.noumea.mairie.sirh.dao;

import nc.noumea.mairie.sirh.domain.AvctCapPrintJob;

public interface IAvctCapPrintJobDao {

	public AvctCapPrintJob getNextPrintJob();
	public void updateJobIdAndStatus(AvctCapPrintJob job);
	public void updateStatus(AvctCapPrintJob job);
}
