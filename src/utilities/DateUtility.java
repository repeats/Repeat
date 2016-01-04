package utilities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtility {

	private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("dd/MM/yyyy H:m:s");
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

	public static String calendarToTimeString(Calendar calendar) {
		if (calendar == null) {
			return null;
		}
		return TIME_FORMAT.format(calendar.getTime());
	}

	public static String calendarToDateString(Calendar calendar) {
		if (calendar == null) {
			return null;
		}
		return DATE_FORMAT.format(calendar.getTime());
	}

	/**
	 * Convert a string date/time into the according calendar.
	 * The format used is the formats defined as constants above.
	 * @param string to convert
	 * @return the according calendar object, or null if there is a parse exception occurred.
	 */
	public static Calendar stringToCalendar(String s) {
		Date date = null;

		try {
			date = TIME_FORMAT.parse(s);
		} catch (ParseException e) {
			try {
				date = DATE_FORMAT.parse(s);
			} catch (ParseException e1) {
				return null;
			}
		}

		if (date == null) {
			return null;
		}
		Calendar output = Calendar.getInstance();
		output.setTime(date);
		return output;
	}

	private DateUtility() {}
}
