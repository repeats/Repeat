package cli.server.handlers;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.nio.protocol.BasicAsyncRequestConsumer;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import org.apache.http.nio.protocol.HttpAsyncRequestConsumer;
import org.apache.http.nio.protocol.HttpAsyncRequestHandler;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import cli.server.CliRpcCodec;
import frontEnd.MainBackEndHolder;

public abstract class HttpHandlerWithBackend implements HttpAsyncRequestHandler<HttpRequest> {

	protected MainBackEndHolder backEndHolder;

	public synchronized void setMainBackEndHolder(MainBackEndHolder backEndHolder) {
		this.backEndHolder = backEndHolder;
	}

	@Override
	public void handle(HttpRequest request, HttpAsyncExchange exchange, HttpContext context)
			throws HttpException, IOException {
		if (backEndHolder == null) {
			CliRpcCodec.prepareResponse(exchange, 500, "Missing backend...");
			return;
		}

		handleWithBackend(request, exchange, context);
	}

	@Override
	public HttpAsyncRequestConsumer<HttpRequest> processRequest(HttpRequest request, HttpContext context)
			throws HttpException, IOException {
		// Buffer request content in memory for simplicity.
		return new BasicAsyncRequestConsumer();
	}

	protected abstract void handleWithBackend(HttpRequest request, HttpAsyncExchange exchange, HttpContext context)
			throws HttpException, IOException;

	protected final byte[] getRequestBody(HttpRequest request) throws IOException {
		if (request instanceof HttpEntityEnclosingRequest) {
			HttpEntity entity = ((HttpEntityEnclosingRequest) request).getEntity();
			return EntityUtils.toByteArray(entity);
		}
		return new byte[] {};
	}
}
