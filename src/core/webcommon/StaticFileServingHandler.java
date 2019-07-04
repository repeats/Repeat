package core.webcommon;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NFileEntity;
import org.apache.http.nio.protocol.BasicAsyncResponseProducer;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import org.apache.http.protocol.HttpContext;

public class StaticFileServingHandler extends HttpSimpleAsyncRequestHandler {

	private static final Logger LOGGER = Logger.getLogger(UpAndRunningHandler.class.getName());

	private final String root;

	public StaticFileServingHandler(String root) {
		this.root = root;
	}

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

		String rootPath = new File(root).getCanonicalPath();
		File file = new File(root, URLDecoder.decode(path, "UTF-8"));
		if (!file.getCanonicalPath().startsWith(rootPath)) {
			return HttpServerUtilities.prepareTextResponse(exchange, 404, String.format("File does not exist %s.", path));
		}

		HttpResponse response = exchange.getResponse();
		if (!file.exists()) {
			return HttpServerUtilities.prepareTextResponse(exchange, 404, String.format("File does not exist %s.", path));
		} else if (!file.canRead() || file.isDirectory()) {
			return HttpServerUtilities.prepareTextResponse(exchange, 403, String.format("Cannot read file %s.", path));
		} else if (!file.canRead()) {
			return HttpServerUtilities.prepareTextResponse(exchange, 400, String.format("Path is a directory %s.", path));
		} else {
            response.setStatusCode(HttpStatus.SC_OK);
            response.addHeader("Cache-Control", "max-age=3600"); // Max age = 1 hour.
            String contentType = contentType(file.toPath());
            NFileEntity body = new NFileEntity(file, ContentType.create(contentType));
            response.setEntity(body);
            exchange.submitResponse(new BasicAsyncResponseProducer(response));
            return null;
        }
	}

	private String contentType(Path path) throws IOException {
		String absolutePath = path.toAbsolutePath().toString();
		if (absolutePath.endsWith(".js")) {
			return "application/javascript";
		}
		if (absolutePath.endsWith(".css")) {
			return "text/css";
		}
		if (absolutePath.endsWith(".htm") || absolutePath.endsWith(".html")) {
			return "text/html";
		}
		if (absolutePath.endsWith(".jpg") || absolutePath.endsWith(".jpeg") || absolutePath.endsWith(".jpe")) {
			return "image/jpeg";
		}
		if (absolutePath.endsWith(".png")) {
			return "image/png";
		}
		if (absolutePath.endsWith(".gif")) {
			return "image/gif";
		}
		return "text/plain";
	}
}
