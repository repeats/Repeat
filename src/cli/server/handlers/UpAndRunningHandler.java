package cli.server.handlers;

import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Logger;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

/**
 * Returns response saying that server is up and running.
 */
public class UpAndRunningHandler implements HttpHandler {

	private static final Logger LOGGER = Logger.getLogger(UpAndRunningHandler.class.getName());

	@Override
	public void handle(HttpExchange t) throws IOException {
		LOGGER.info("Server is up and running.");

        String response = "This is the response";
        t.sendResponseHeaders(200, response.length());
        OutputStream os = t.getResponseBody();
        os.write(response.getBytes());
        os.close();
	}
}
