package nc.noumea.mairie.sirh.tools;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

@Service
public class Helper {

	public Date getCurrentDate() {
		Calendar c = new GregorianCalendar();
		return c.getTime();
	}

	/**
	 * Converts a 90xxxx employeeNumber into an Nomatr readable one: xxxx
	 * 
	 * @param employeeNumber
	 *            90xxxx
	 * @return xxxx
	 */
	public String getNomatr(int employeeNumber) {
		return String.valueOf(employeeNumber).substring(2, String.valueOf(employeeNumber).length());
	}

	/**
	 * Converts a 90xxxx employeeNumber into an IdAgent readable one: 900xxxx
	 * 
	 * @param employeeNumber
	 *            90xxxx
	 * @return 900xxxx
	 */
	public String getIdAgent(int employeeNumber) {
		return "900" + getNomatr(employeeNumber);
	}

	/**
	 * Converts a 900xxxx IdAgent into an employeeNumber readable one: 90xxxx
	 * 
	 * @param idAgent
	 *            900xxxx
	 * @return 90xxxx
	 */
	public String getEmployeeNumber(Integer idAgent) {
		return "90" + String.valueOf(idAgent).substring(3, String.valueOf(idAgent).length());
	}
	
	public Date getFirstDayOfPreviousMonth() {
		DateTime date = new DateTime().withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0);
		
		return date.dayOfMonth()       // Accès à la propriété 'Jour du Mois'
		 .withMinimumValue().toDate();
	}
	
	public Date getLastDayOfPreviousMonth() {
		DateTime date = new DateTime().withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0);
		
		return date.dayOfMonth()       // Accès à la propriété 'Jour du Mois'
		 .withMaximumValue().toDate();
	}
}
