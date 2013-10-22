package nc.noumea.mairie.ptg.dao;

import nc.noumea.mairie.ptg.domain.ExportEtatsPayeurTask;
import nc.noumea.mairie.ptg.domain.ExportPaieTask;
import nc.noumea.mairie.ptg.domain.VentilTask;

public interface IPointagesDao {

	void beginTransaction();

	void commitTransaction();

	void rollBackTransaction();

	VentilTask getNextVentilTask();

	ExportPaieTask getNextExportPaieTask();

	ExportEtatsPayeurTask getNextExportEtatsPayeurTask();
}
