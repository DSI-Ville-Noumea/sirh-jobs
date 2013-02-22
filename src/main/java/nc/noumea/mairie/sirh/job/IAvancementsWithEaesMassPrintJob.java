package nc.noumea.mairie.sirh.job;

import nc.noumea.mairie.sirh.domain.PrintJob;
import nc.noumea.mairie.sirh.service.PrinterHelper;

public interface IAvancementsWithEaesMassPrintJob {

	/**
	 * Retrieves the next print job from the queue
	 */
	public PrintJob getNextPrintJob();
	
	/**
	 * Initializes the print job
	 * - Generates JOB ID
	 * - Set status in PrintJobTable
	 * - Generates Cover PDF pages (front and back) (by calling Birt reporting instance)
	 */
	public void initializePrintJob(PrintJob job);
	
	/**
	 * Generates avancement report (by calling Birt reporting instance)
	 * - Update status in PrintJobTable
	 * @throws Exception 
	 */
	public void generateAvancementsReport(PrintJob job) throws Exception;
	
	/**
	 * Download and store EAEs related to avancements
	 * - Update status in PrintJobTable
	 * @throws Exception 
	 */
	public void downloadRelatedEaes(PrintJob job) throws Exception;
	
	/**
	 * Sends all the document to the printer server in the right sequence
	 * - Update status in PrintJobTable
	 */
	public void printAllDocuments(PrintJob job, PrinterHelper pH) throws Exception;
	
	/**
	 * Erases all the downloaded and created documents from the working directory
	 * - Update status in PrintJobTable
	 */
	public void wipeJobDocuments(PrintJob job);
	
	/**
	 * Update a job's status
	 * @param status
	 */
	public void updateStatus(PrintJob job, String status);
}
