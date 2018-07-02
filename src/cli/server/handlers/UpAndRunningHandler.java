package cli.server.handlers;

import java.io.IOException;
import java.util.logging.Logger;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.entity.StringEntity;
import org.apache.http.nio.protocol.BasicAsyncRequestConsumer;
import org.apache.http.nio.protocol.BasicAsyncResponseProducer;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import org.apache.http.nio.protocol.HttpAsyncRequestConsumer;
import org.apache.http.nio.protocol.HttpAsyncRequestHandler;
import org.apache.http.protocol.HttpContext;

/**
 * Returns response saying that server is up and running.
 */
public class UpAndRunningHandler implements HttpAsyncRequestHandler<HttpRequest> {

	private static final Logger LOGGER = Logger.getLogger(UpAndRunningHandler.class.getName());

	@Override
	public void handle(HttpRequest request, HttpAsyncExchange exchange, HttpContext context) throws HttpException, IOException {
		LOGGER.info("Server is up and running.");

		String responseText = "This is the response.";
		HttpResponse response = exchange.getResponse();
		response.setStatusCode(HttpStatus.SC_OK);
		response.setEntity(new StringEntity(responseText));
		exchange.submitResponse(new BasicAsyncResponseProducer(response));
	}

	@Override
	public HttpAsyncRequestConsumer<HttpRequest> processRequest(HttpRequest arg0, HttpContext arg1)
			throws HttpException, IOException {
		// Buffer request content in memory for simplicity.
		return new BasicAsyncRequestConsumer();
	}
}
