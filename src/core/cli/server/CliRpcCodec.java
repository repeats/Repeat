package core.cli.server;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.apache.http.nio.protocol.HttpAsyncExchange;

import argo.jdom.JsonNode;
import core.webcommon.HttpServerUtilities;
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

	public static Void prepareResponse(HttpAsyncExchange exchange, int code, String data) throws IOException {
		return prepareResponse(exchange, code, data.getBytes(CliRpcCodec.ENCODING));
	}

	private static Void prepareResponse(HttpAsyncExchange exchange, int code, byte[] data) throws IOException {
		return HttpServerUtilities.prepareResponse(exchange, code, encode(data));
	}
}
