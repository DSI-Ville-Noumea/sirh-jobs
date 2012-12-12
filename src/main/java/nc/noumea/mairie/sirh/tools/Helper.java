package nc.noumea.mairie.sirh.tools;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.springframework.stereotype.Service;

@Service
public class Helper {

	public Date getCurrentDate() {
		Calendar c = new GregorianCalendar();
		return c.getTime();
	}
	
	/**
	 * Converts a 900xxxx agent id into an AD readable one: 90xxxx
	 * @param idAgent 900xxxx
	 * @return 90xxxx
	 */
	public String convertIdAgentToADId(Integer idAgent) {
		
		String adIdAgent = idAgent.toString();
		if (adIdAgent.length() == 7)
			adIdAgent = adIdAgent.substring(0, 1).concat(adIdAgent.substring(2));
		
		return adIdAgent;
	}
}
