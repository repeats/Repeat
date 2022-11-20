package core.webui.server;

import java.io.IOException;

import org.apache.http.ExceptionLogger;

/**
 * Logger that ignores certain commonly encountered (but harmless) exceptions.
 */
public class UIServerExceptionLogger implements ExceptionLogger {
	@Override
	public void log(Exception arg0) {
		if (arg0 instanceof IOException) {
			if (arg0.getLocalizedMessage().contains("An established connection was aborted by the software in your host machine")) {
				return;
			}
			if (arg0.getLocalizedMessage().contains("An existing connection was forcibly closed by the remote host")) {
				return;
			}
		}

		arg0.printStackTrace();
	}
}
