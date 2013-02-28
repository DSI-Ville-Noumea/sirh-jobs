package nc.noumea.mairie.sirh.service;

import java.io.InputStream;
import java.net.URL;
import java.util.Map;

import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;
import org.cups4j.CupsClient;
import org.cups4j.CupsPrinter;
import org.cups4j.PrintJob;
import org.cups4j.PrintRequestResult;

public class PrinterHelper {

	private String host;
	private int port;
	private String printerName;
	private String printJobName;
	private CupsPrinter cupsPrinter;
	private FileSystemManager fsManager;
	
	private FileSystemManager getFsManager() throws FileSystemException {
		if (fsManager == null)
			fsManager = VFS.getManager();
		
		return fsManager;
	}
	
	public PrinterHelper(String host, int port, String printerName, String printJobName) throws Exception {
		this.host= host;
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
			cupsPrinter.setDescription(printJobName);
		} catch (Exception e) {
			throw new Exception(
					String.format(
							"An error occured when initializing the printer: server [%s] port [%s] printerName [%s] for job [%s]",
							host, port, printerName, printJobName),
					e);
		}
	}

	public void printDocument(String filePath, Map<String, String> properties) throws Exception {
		
		InputStream is = getFsManager().resolveFile(filePath).getContent().getInputStream();
        PrintJob pj = new PrintJob.Builder(is).build();
        pj.setAttributes(properties);
        PrintRequestResult res = cupsPrinter.print(pj);
        
        if (!res.isSuccessfulResult()) {
			throw new Exception(
					String.format(
							"An error occured while submitting a job to the printer: id [%s] resultCode [%s] desc [%s]",
							res.getJobId(), res.getResultCode(),
							res.getResultDescription()));
        }
	}
}
