package core.ipc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class IPCProtocol {

	private static final Logger LOGGER = Logger.getLogger(IPCProtocol.class.getName());

	private IPCProtocol() {}

	protected static final int MESSAGE_DELIMITER = 0x02;
	protected static final long READ_LOOP_SLEEP_DURATION_MS = 200;
	protected static final int MAX_MESSAGE_RETRY = 10;
	protected static final int NULL_CHARACTER = 0x00;

	/**
	 * Send a message to the output stream.
	 */
	public static void sendMessage(OutputStream writer, String message) throws IOException {
		writer.write(MESSAGE_DELIMITER);
		writer.write(MESSAGE_DELIMITER);
		writer.write(IPCCodec.encode(message));
		writer.write(MESSAGE_DELIMITER);
		writer.write(MESSAGE_DELIMITER);
		writer.flush();
	}

	/**
	 * Retrieve a list of messages from the reader.
	 * This blocks until there is a message to be read from
	 * the reader.
	 */
	public static List<String> getMessages(Reader reader) throws IOException, InterruptedException {
		/**
		 * Create a blocking read waiting for the next communication.
		 */
		int retryCount = 0;
		int firstCharacter = reader.read();
		while (firstCharacter == -1) {
			retryCount++;
			if (retryCount < MAX_MESSAGE_RETRY) {
				Thread.sleep(READ_LOOP_SLEEP_DURATION_MS);
			} else {
				LOGGER.log(Level.WARNING, "Max retry reached.");
				return null;
			}
			firstCharacter = reader.read();
		}

		/**
		 * Build the request.
		 */
		List<String> output = new LinkedList<>();
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();

		if (firstCharacter != MESSAGE_DELIMITER) {
			buffer.write(firstCharacter);
		}

		while (reader.ready() || output.isEmpty()) {
			int readValue = reader.read();
			if (readValue != -1) {
				if (readValue == MESSAGE_DELIMITER) {
					if (buffer.size() != 0) {
						output.add(IPCCodec.decode(buffer.toByteArray()));
						buffer.reset();
					}
				} else if (readValue != NULL_CHARACTER) {
					buffer.write(readValue);
				}
			} else {
				if (buffer.size() != 0) {
					output.add(IPCCodec.decode(buffer.toByteArray()));
					buffer.reset();
				}
				break;
			}
		}
		return output;
	}
}
