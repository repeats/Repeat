package core.webcommon;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.nio.protocol.BasicAsyncResponseProducer;
import org.apache.http.nio.protocol.HttpAsyncExchange;

public class HttpServerUtilities {
	private HttpServerUtilities() {}

	public static Void prepareResponse(HttpAsyncExchange exchange, int code, byte[] data) throws IOException {
		HttpResponse response = exchange.getResponse();
		response.setStatusCode(code);
		response.setEntity(new ByteArrayEntity(data));
		exchange.submitResponse(new BasicAsyncResponseProducer(response));
        return null;
	}

	public static Void prepareHttpResponse(HttpAsyncExchange exchange, int code, String data) throws IOException {
		HttpResponse response = exchange.getResponse();
		response.setStatusCode(code);
		StringEntity entity = new StringEntity(data);
		entity.setContentEncoding("UTF-8");
		entity.setContentType("text/html");
		response.setEntity(entity);
		exchange.submitResponse(new BasicAsyncResponseProducer(response));
        return null;
	}

	public static Void prepareResponse(HttpAsyncExchange exchange, int code, String data) throws IOException {
		HttpResponse response = exchange.getResponse();
		response.setStatusCode(code);
		response.setEntity(new StringEntity(data));
		exchange.submitResponse(new BasicAsyncResponseProducer(response));
        return null;
	}
}
