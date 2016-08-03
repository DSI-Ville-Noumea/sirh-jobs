package nc.noumea.mairie.sirh.job;

import nc.noumea.mairie.sirh.domain.AvctCapPrintJob;
import nc.noumea.mairie.sirh.service.PrinterHelper;
import nc.noumea.mairie.sirh.tools.AvancementsWithEaesMassPrintJobStatusEnum;

public interface IAvancementsWithEaesMassPrintJob {

	/**
	 * Retrieves the next print job from the queue
	 */
	public AvctCapPrintJob getNextPrintJob();
	
	/**
	 * Initializes the print job
	 * - Generates JOB ID
	 * - Set status in PrintJobTable
	 * - Generates Cover PDF pages (front and back) (by calling Birt reporting instance)
	 */
	public void initializePrintJob(AvctCapPrintJob job);
	
	/**
	 * Generates avancement report (by calling Birt reporting instance)
	 * - Update status in PrintJobTable
	 * @throws Exception 
	 */
	public void generateAvancementsReport(AvctCapPrintJob job, PrinterHelper pH) throws AvancementsWithEaesMassPrintException;
	
	/**
	 * Print EAEs related to avancements
	 * - Update status in PrintJobTable
	 * @throws Exception 
	 */
	public void printRelatedEaes(AvctCapPrintJob job, PrinterHelper pH) throws AvancementsWithEaesMassPrintException;
	
	/**
	 * Update a job's status
	 * @param job
	 * @param status
	 */
	public void updateStatus(AvctCapPrintJob job, AvancementsWithEaesMassPrintJobStatusEnum status);
}
