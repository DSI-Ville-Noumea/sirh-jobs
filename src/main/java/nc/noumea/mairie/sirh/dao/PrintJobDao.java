package nc.noumea.mairie.sirh.dao;

import nc.noumea.mairie.sirh.domain.PrintJob;

import org.hibernate.SessionFactory;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class PrintJobDao implements IPrintJobDao {

	@Autowired
	@Qualifier("sirhSessionFactory")
	private SessionFactory sirhSessionFactory;
	
	@Override
	public PrintJob getNextPrintJob() {

		PrintJob pj = new PrintJob();
		pj.setIdCap(11);
		pj.setIdCadreEmploi(87);
		pj.setAgentId("9005138");
		pj.setSubmissionDate(new DateTime(2013, 02, 22, 9, 57, 37).toDate());
		
		return pj;
	}

	@Override
	public void updateJobIdAndStatus(PrintJob job) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateStatus(PrintJob job) {
		// TODO Auto-generated method stub
		
	}
}
