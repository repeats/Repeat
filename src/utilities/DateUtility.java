package utilities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DateUtility {

	private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("dd/MM/yyyy H:m:s");
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

	public static Calendar calendarFromMillis(long millisSinceEpoch) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(millisSinceEpoch);
		return c;
	}

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

	/**
	 * Convert a long duration into readable string.
	 */
	public static String durationToString(long durationMillis) {
		int millis = (int) (durationMillis % 1000);
		long seconds = durationMillis / 1000;
		long minutes = seconds / 60;
		long hours = minutes / 60;
		long days = hours / 24;
		long years = days / 365;

		List<String> results = new ArrayList<>();

		if (years != 0) {
			results.add(years + " y");
		}
		if (days != 0) {
			results.add(days % 365 + " d");
		}
		if (hours != 0) {
			results.add(hours % 24 + " h");
		}
		if (minutes != 0) {
			results.add(minutes % 60 + " m");
		}
		if (seconds != 0) {
			results.add(seconds % 60 + " s");
		}
		if (millis != 0) {
			results.add(millis + " ms");
		}
		return StringUtilities.join(results, ", ");
	}

	private DateUtility() {}
}
