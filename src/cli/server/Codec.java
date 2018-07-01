package cli.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import com.sun.net.httpserver.HttpExchange;

import argo.jdom.JsonNode;
import utilities.JSONUtility;

public class Codec {

	private static final int INPUT_STREAM_BUFFER_SIZE = 64;

	protected static byte[] encode(byte[] input) {
		return Base64.getEncoder().encode(input);
	}

	protected static JsonNode decode(String input) {
		byte[] data = Base64.getDecoder().decode(input.getBytes(CliServer.ENCODING));
		return JSONUtility.jsonFromString(new String(data, CliServer.ENCODING));
	}

	protected static String streamToString(InputStream in) throws IOException {
	    ReadableByteChannel channel = Channels.newChannel(in);
	    ByteBuffer byteBuffer = ByteBuffer.allocate(INPUT_STREAM_BUFFER_SIZE);
	    ByteArrayOutputStream bout = new ByteArrayOutputStream();
	    WritableByteChannel outChannel = Channels.newChannel(bout);
	    while (channel.read(byteBuffer) > 0 || byteBuffer.position() > 0) {
	        byteBuffer.flip();  //make buffer ready for write
	        outChannel.write(byteBuffer);
	        byteBuffer.compact(); //make buffer ready for reading
	    }
	    channel.close();
	    outChannel.close();
	    return bout.toString(StandardCharsets.UTF_8.name());
	}

	protected static Void prepareResponse(HttpExchange exchange, int code, String data) throws IOException {
		return prepareResponse(exchange, code, data.getBytes(CliServer.ENCODING));
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
