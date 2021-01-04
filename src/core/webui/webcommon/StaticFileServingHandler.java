package core.webui.webcommon;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.nio.protocol.BasicAsyncResponseProducer;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import org.apache.http.protocol.HttpContext;

import staticResources.BootStrapResources;
import staticResources.WebUIResources;

public class StaticFileServingHandler extends HttpSimpleAsyncRequestHandler {

	private static final Logger LOGGER = Logger.getLogger(StaticFileServingHandler.class.getName());

	public StaticFileServingHandler() {}

	@Override
	public Void handleRequest(HttpRequest request, HttpAsyncExchange exchange, HttpContext context) throws HttpException, IOException {
		LOGGER.fine("Path is " + request.getRequestLine().getUri());
		if (!request.getRequestLine().getMethod().equalsIgnoreCase("GET")) {
			return HttpServerUtilities.prepareTextResponse(exchange, 400, "Only accept GET requests.");
		}

		String requestUri = request.getRequestLine().getUri();
		if (!requestUri.startsWith("/static/")) {
			return HttpServerUtilities.prepareTextResponse(exchange, 500, "URI must start with '/static/'.");
		}

		String uriWithoutParamter = "";
		try {
			URI uri = new URI(requestUri);
			uriWithoutParamter = new URI(uri.getScheme(),
	                   uri.getAuthority(),
	                   uri.getPath(),
	                   null, // Ignore the query part of the input url.
	                   uri.getFragment()).toString();
		} catch (URISyntaxException e) {
			LOGGER.log(Level.WARNING, "Encountered exception when trying to remove query parameters.", e);
			return HttpServerUtilities.prepareTextResponse(exchange, 500, "Encountered exception when trying to remove query parameters.");
		}

		String path = uriWithoutParamter.substring("/static/".length());
		String decodedPath = URLDecoder.decode(path, "UTF-8");
		if (decodedPath.contains("./") || decodedPath.contains("..") || decodedPath.endsWith("/")) {
			return HttpServerUtilities.prepareTextResponse(exchange, 404, String.format("File does not exist %s.", path));
		}

		HttpResponse response = exchange.getResponse();
		response.setStatusCode(HttpStatus.SC_OK);
		response.addHeader("Cache-Control", "max-age=3600"); // Max age = 1 hour.
		String contentType = contentType(decodedPath);
		InputStream inputStream = BootStrapResources.getStaticContentStream(WebUIResources.STATIC_RESOURCES_PREFIX + decodedPath);
		InputStreamEntity body = new InputStreamEntity(inputStream, ContentType.create(contentType));
		response.setEntity(body);
		exchange.submitResponse(new BasicAsyncResponseProducer(response));
		return null;
	}

	private String contentType(String filePath) throws IOException {
		if (filePath.endsWith(".js")) {
			return "application/javascript";
		}
		if (filePath.endsWith(".css")) {
			return "text/css";
		}
		if (filePath.endsWith(".htm") || filePath.endsWith(".html")) {
			return "text/html";
		}
		if (filePath.endsWith(".jpg") || filePath.endsWith(".jpeg") || filePath.endsWith(".jpe")) {
			return "image/jpeg";
		}
		if (filePath.endsWith(".png")) {
			return "image/png";
		}
		if (filePath.endsWith(".gif")) {
			return "image/gif";
		}
		return "text/plain";
	}
}
