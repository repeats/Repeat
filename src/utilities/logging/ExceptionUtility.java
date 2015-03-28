package utilities.logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

public final class ExceptionUtility {

	public static String getStackTrace(Exception e) {
		final Writer result = new StringWriter();
		final PrintWriter printWriter = new PrintWriter(result);
		e.printStackTrace(printWriter);
		return result.toString();
	}

	private ExceptionUtility(){}
}
