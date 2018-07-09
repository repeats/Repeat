package core.webcommon;

import java.io.IOException;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.nio.protocol.BasicAsyncRequestConsumer;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import org.apache.http.nio.protocol.HttpAsyncRequestConsumer;
import org.apache.http.nio.protocol.HttpAsyncRequestHandler;
import org.apache.http.protocol.HttpContext;

public abstract class HttpSimpleAsyncRequestHandler implements HttpAsyncRequestHandler<HttpRequest> {

	@Override
	public final void handle(HttpRequest request, HttpAsyncExchange exchange, HttpContext context) throws HttpException, IOException {
		handleRequest(request, exchange, context);
	}

	public abstract Void handleRequest(HttpRequest request, HttpAsyncExchange exchange, HttpContext context) throws HttpException, IOException;

	@Override
	public final HttpAsyncRequestConsumer<HttpRequest> processRequest(HttpRequest arg0, HttpContext arg1)
			throws HttpException, IOException {
		// Buffer request content in memory for simplicity.
		return new BasicAsyncRequestConsumer();
	}
}
