package core.ipc.repeatServer;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import core.controller.Core;
import core.ipc.repeatServer.processors.ServerMainProcessor;
import utilities.ILoggable;

class ClientServingThread implements Runnable, ILoggable {

	private static final int MAX_RETRY = 100;
	private static final int NULL_CHARACTER = 0x00;

	protected static final int MESSAGE_DELIMITER = 0x02;

	private Boolean stopped;
	private final Socket socket;
	private BufferedReader reader;
	private DataOutputStream writer;

	private final ServerMainProcessor requestProcessor;
	private final MainMessageSender messageSender;

	private int retryCount;

	protected ClientServingThread(Core core, Socket socket) {
		this.socket = socket;

		messageSender = new MainMessageSender();
		requestProcessor = new ServerMainProcessor(core, messageSender);

		retryCount = 0;
		stopped = false;
	}

	@Override
	public void run() {
		reader = null;
		writer = null;

		try {
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			writer = new DataOutputStream(socket.getOutputStream());
			messageSender.setWriter(writer);
		} catch (IOException e) {
			getLogger().log(Level.WARNING, "IO Exception when open reader and writer for socket", e);
			return;
		}

		try {
			while (!isStopped()) {
				if (!processLoop()) {
					getLogger().info("Failed to execute process loop...");
					break;
				}
			}
			getLogger().info("Client serving thread on socket on remote port " + socket.getPort() + " is terminated\n");
		} finally {
			try {
				reader.close();
				reader = null;
			} catch (IOException e) {
				getLogger().log(Level.WARNING, "IO Exception when closing input socket", e);
			}

			try {
				writer.close();
				writer = null;
			} catch (IOException e) {
				getLogger().log(Level.WARNING, "IO Exception when closing output socket", e);
			}

			try {
				socket.close();
			} catch (IOException e) {
				getLogger().log(Level.WARNING, "IO Exception when closing socket", e);
			}
		}
	}

	private boolean processLoop() {
		try {
        	return process();
        } catch (IOException e) {
        	getLogger().log(Level.WARNING, "IO Exception when serving client", e);
        	return false;
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Exception when serving client", e);
			return false;
		}
	}

	private boolean process() throws IOException {
		if (reader == null || writer == null) {
			getLogger().warning("Unable to process with reader " + reader + " and writer " + writer);
			return false;
		}

		List<String> messages = getMessages();
		if (messages == null || messages.size() == 0) {
			getLogger().warning("Messages is null or messages size is 0. " + messages);
			return false;
		}

		boolean result = true;

		for (String message : messages) {
			boolean newResult = requestProcessor.processRequest(message);
			result &= newResult;
			if (!newResult) {
				getLogger().warning("Unable to process request " + message);
			}
		}

		return result;
	}

	private List<String> getMessages() throws IOException {
		/**
		 * Create a blocking read waiting for the next communication
		 */
		int firstCharacter = reader.read();
		if (firstCharacter == -1) {
			retryCount++;
			if (retryCount < MAX_RETRY) {
				return null;
			} else {
				getLogger().log(Level.WARNING, "Max retry reached.");
				return null;
			}
		}

		/**
		 * Build the request, remembering that
		 */
		List<String> output = new LinkedList<>();
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();

		if (firstCharacter != MESSAGE_DELIMITER) {
			buffer.write(firstCharacter);
		}

		while (reader.ready()) {
			int readValue = reader.read();
			if (readValue != -1) {
				if (readValue == MESSAGE_DELIMITER) {
					if (buffer.size() != 0) {
						output.add(decode(buffer.toByteArray()));
						buffer.reset();
					}
				} else if (readValue != NULL_CHARACTER) {
					buffer.write(readValue);
				}
			} else {
				if (buffer.size() != 0) {
					output.add(decode(buffer.toByteArray()));
					buffer.reset();
				}
				break;
			}
		}
		return output;
	}

	/**
	 * Decode received array of bytes from client.
	 *
	 * @param bytes array of bytes to decode.
	 * @return decoded message.
	 */
	private String decode(byte[] bytes) {
		byte[] base64Decoded = Base64.getDecoder().decode(bytes);
		CharBuffer result = ControllerServer.ENCODING.decode(ByteBuffer.wrap(base64Decoded));
		return result.toString().trim();
	}

	protected void stop() {
		synchronized (stopped) {
			stopped = true;
		}
	}

	private boolean isStopped() {
		synchronized (stopped) {
			return stopped;
		}
	}

	@Override
	public Logger getLogger() {
		return Logger.getLogger(ControllerServer.class.getName());
	}
}
