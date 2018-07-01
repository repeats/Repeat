package cli.server;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import com.sun.net.httpserver.HttpExchange;

import argo.jdom.JsonNode;
import utilities.JSONUtility;

public class CliRpcCodec {

	public static final Charset ENCODING = StandardCharsets.UTF_8;

	public static byte[] encode(byte[] input) {
		return Base64.getEncoder().encode(input);
	}

	public static String decode(byte[] data) {
		return new String(Base64.getDecoder().decode(data), CliRpcCodec.ENCODING);
	}

	public static JsonNode decodeRequest(byte[] data) {
		return JSONUtility.jsonFromString(decode(data));
	}

	public static Void prepareResponse(HttpExchange exchange, int code, String data) throws IOException {
		return prepareResponse(exchange, code, data.getBytes(CliRpcCodec.ENCODING));
	}

	private static Void prepareResponse(HttpExchange exchange, int code, byte[] data) throws IOException {
		byte[] encodedData = encode(data);
		exchange.sendResponseHeaders(code, encodedData.length);
		OutputStream responseBody = exchange.getResponseBody();
        responseBody.write(encodedData);
        responseBody.close();
        return null;
	}
}
