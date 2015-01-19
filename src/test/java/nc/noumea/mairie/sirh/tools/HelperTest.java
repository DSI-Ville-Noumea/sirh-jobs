package nc.noumea.mairie.sirh.tools;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;

import org.joda.time.DateTime;
import org.junit.Test;

public class HelperTest {

	@Test
	public void getFirstDayOfPreviousMonth() {
		
		Helper service = new Helper();
		Date firstDayOfPreviousMonth = service.getFirstDayOfPreviousMonth();
		
		Calendar calLundi = Calendar.getInstance();
		calLundi.setTime(new Date());
		calLundi.add(Calendar.MONTH, -1);
		
		assertEquals(1, new DateTime(firstDayOfPreviousMonth).getDayOfMonth());
		assertEquals(new DateTime(firstDayOfPreviousMonth).getMonthOfYear(), calLundi.get(Calendar.MONTH)+1); // +1 car janvier commence a zero
	}
	
	@Test
	public void getLastDayOfPreviousMonth() {
		
		Helper service = new Helper();
		Date firstDayOfPreviousMonth = service.getLastDayOfPreviousMonth();
		
		Calendar calLundi = Calendar.getInstance();
		calLundi.setTime(new Date());
		calLundi.add(Calendar.MONTH, -1);
		
		assertTrue(27 < new DateTime(firstDayOfPreviousMonth).getDayOfMonth());
		assertEquals(new DateTime(firstDayOfPreviousMonth).getMonthOfYear(), calLundi.get(Calendar.MONTH)+1);
	}
}
