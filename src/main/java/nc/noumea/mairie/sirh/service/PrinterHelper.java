package nc.noumea.mairie.sirh.service;

import java.io.InputStream;
import java.net.URL;

import org.cups4j.CupsClient;
import org.cups4j.CupsPrinter;
import org.cups4j.PrintJob;
import org.cups4j.PrintRequestResult;

public class PrinterHelper {

	private String		host;
	private int			port;
	private String		printerName;
	private String		printJobName;
	private CupsPrinter	cupsPrinter;

	public PrinterHelper() {
	}

	public PrinterHelper(String host, int port, String printerName, String printJobName) throws Exception {
		this.host = host;
		this.port = port;
		this.printerName = printerName;
		this.printJobName = printJobName;

		initializeCupsPrinter();
	}

	protected void initializeCupsPrinter() throws Exception {

		cupsPrinter = null;

		try {
			CupsClient cc = new CupsClient(host, port);
			cupsPrinter = cc.getPrinter(new URL(printerName));

			if (cupsPrinter == null)
				throw new Exception("CUPS did not answer with a printer...");

			cupsPrinter.setDescription(printJobName);
		} catch (Exception e) {
			throw new Exception(String.format("An error occured when initializing the printer: server [%s] port [%s] printerName [%s] for job [%s]",
					host, port, printerName, printJobName), e);
		}
	}

	public void printDocument(InputStream is, String baseName, String userName) throws Exception {

		PrintJob pj = new PrintJob.Builder(is).jobName(baseName).userName(userName).build();
		PrintRequestResult res = cupsPrinter.print(pj);

		if (!res.isSuccessfulResult()) {
			throw new Exception(String.format("An error occured while submitting a job to the printer: id [%s] resultCode [%s] desc [%s]",
					res.getJobId(), res.getResultCode(), res.getResultDescription()));
		}
	}
}
