package core.cli.server;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.apache.http.HttpResponse;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.nio.protocol.BasicAsyncResponseProducer;
import org.apache.http.nio.protocol.HttpAsyncExchange;

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

	public static Void prepareResponse(HttpAsyncExchange exchange, int code, String data) throws IOException {
		return prepareResponse(exchange, code, data.getBytes(CliRpcCodec.ENCODING));
	}

	private static Void prepareResponse(HttpAsyncExchange exchange, int code, byte[] data) throws IOException {
		byte[] encodedData = encode(data);
		HttpResponse response = exchange.getResponse();
		response.setStatusCode(code);
		response.setEntity(new ByteArrayEntity(encodedData));
		exchange.submitResponse(new BasicAsyncResponseProducer(response));
        return null;
	}
}
