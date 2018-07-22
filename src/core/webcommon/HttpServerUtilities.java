package core.webcommon;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.nio.protocol.BasicAsyncResponseProducer;
import org.apache.http.nio.protocol.HttpAsyncExchange;

import argo.jdom.JsonField;
import argo.jdom.JsonNode;
import utilities.json.JSONUtility;

public class HttpServerUtilities {

	private static final Logger LOGGER = Logger.getLogger(HttpServerUtilities.class.getName());

	private HttpServerUtilities() {}

	public static Map<String, String> parseGetParameters(String url) {
		try {
			List<NameValuePair> paramList = URLEncodedUtils.parse(new URI(url), StandardCharsets.UTF_8);
			Map<String, String>  params = new HashMap<>();
			for (NameValuePair param : paramList) {
				params.put(param.getName(), param.getValue());
			}
			return params;
		} catch (URISyntaxException e) {
			LOGGER.log(Level.WARNING, "Exception when parsing URL.", e);
			return null;
		}
	}

	public static Map<String, String> parseSimplePostParameters(HttpRequest request) {
		byte[] content = getPostContent(request);
		if (content == null) {
			LOGGER.warning("Failed to get POST content.");
			return null;
		}

		return getSimpleParameters(content);
	}

	public static byte[] getPostContent(HttpRequest request) {
		if (!(request instanceof HttpEntityEnclosingRequest)) {
			LOGGER.warning("Unknown request type for POST request " + request.getClass());
			return null;
		}
		HttpEntityEnclosingRequest entityRequest = (HttpEntityEnclosingRequest) request;
		HttpEntity entity = entityRequest.getEntity();
		if (!(entity instanceof BasicHttpEntity)) {
			LOGGER.warning("Unknown entity type for POST request " + entity.getClass());
			return null;
		}
		BasicHttpEntity basicEntity = (BasicHttpEntity) entity;
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		try {
			basicEntity.writeTo(buffer);
		} catch (IOException e) {
			LOGGER.log(Level.WARNING, "Failed to read all request content.", e);
			return null;
		}
		return buffer.toByteArray();
	}

	private static Map<String, String> getSimpleParameters(byte[] content) {
		String postContent = new String(content, StandardCharsets.UTF_8);
		Map<String, String> output = new HashMap<>();
		JsonNode node = JSONUtility.jsonFromString(postContent);
		if (node == null) {
			LOGGER.warning("Failed to parse content into JSON.");
			return null;
		}

		for (JsonField field : node.getFieldList()) {
			String name = field.getName().getStringValue();
			JsonNode valueNode = field.getValue();
			if (!valueNode.isStringValue() && !valueNode.isNumberValue()) {
				LOGGER.warning("Value not is not a string node.");
				return null;
			}
			String value = null;
			if (valueNode.isStringValue()) {
				value = valueNode.getStringValue();
			} else if (valueNode.isNumberValue()) {
				value = valueNode.getNumberValue();
			} else {
				LOGGER.warning("Value is not a string or number node.");
				return null;
			}
			output.put(name, value);
		}

		return output;
	}

	/**
	 * Construct an HTTP redirect response. Note that this uses code 302, not 301.
	 *
	 * @param dest path to the destination. This is absolute path not including the domain.
	 */
	public static Void redirect(HttpAsyncExchange exchange, String dest, Map<String, String> params) throws IOException {
		String location = "";
		try {
			URIBuilder builder = new URIBuilder(exchange.getRequest().getRequestLine().getUri());
			builder.clearParameters();
			for (Entry<String, String> entry : params.entrySet()) {
				builder.setParameter(entry.getKey(), entry.getValue());
			}
			location = builder.setPath(dest).build().toString();
		} catch (URISyntaxException e) {
			LOGGER.log(Level.WARNING, "Unable to parse request URI.", e);
			return prepareTextResponse(exchange, 500, "Unable to parse request URI.");
		}

		HttpResponse response = exchange.getResponse();
		response.setStatusCode(302);
		response.addHeader("Location", location);
		exchange.submitResponse(new BasicAsyncResponseProducer(response));
		return null;
	}

	public static Void prepareResponse(HttpAsyncExchange exchange, int code, byte[] data) throws IOException {
		HttpResponse response = exchange.getResponse();
		response.setStatusCode(code);
		response.setEntity(new ByteArrayEntity(data));
		exchange.submitResponse(new BasicAsyncResponseProducer(response));
        return null;
	}

	public static Void prepareHttpResponse(HttpAsyncExchange exchange, int code, String data) throws IOException {
		return prepareStringResponse(exchange, code, data, "text/html");
	}

	public static Void prepareTextResponse(HttpAsyncExchange exchange, int code, String data) throws IOException {
		return prepareStringResponse(exchange, code, data, "text/plain");
	}

	private static Void prepareStringResponse(HttpAsyncExchange exchange, int code, String data, String contentType) throws IOException {
		HttpResponse response = exchange.getResponse();
		response.setStatusCode(code);
		StringEntity entity = new StringEntity(data);
		entity.setContentEncoding("UTF-8");
		entity.setContentType(contentType);
		response.setEntity(entity);
		exchange.submitResponse(new BasicAsyncResponseProducer(response));
        return null;
	}
}
