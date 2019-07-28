package core.ipc;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class IPCCodec {

	private static final Charset ENCODING = StandardCharsets.UTF_8;

	private IPCCodec() {}

	/**
	 * Encode a message to send.
	 *
	 * @param message message to encode.
	 * @return byte array representing bytes to send.
	 */
	public static byte[] encode(String message) {
		return Base64.getEncoder().encode(message.getBytes(ENCODING));
	}

	/**
	 * Decode received array of bytes.
	 *
	 * @param bytes array of bytes to decode.
	 * @return decoded message.
	 */
	public static String decode(byte[] bytes) {
		byte[] base64Decoded = Base64.getDecoder().decode(bytes);
		CharBuffer result = ENCODING.decode(ByteBuffer.wrap(base64Decoded));
		return result.toString().trim();
	}
}
