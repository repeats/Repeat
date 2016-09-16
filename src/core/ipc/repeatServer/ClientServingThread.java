package core.ipc.repeatServer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import utilities.ILoggable;
import core.controller.Core;
import core.ipc.repeatServer.processors.ServerMainProcessor;

class ClientServingThread implements Runnable, ILoggable {

	private static final int MAX_RETRY = 100;

	protected static final char MESSAGE_DELIMITER = 0x02;

	private Boolean stopped;
	private final Socket socket;
	private BufferedReader reader;
	private BufferedWriter writer;

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
			writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
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

		List<StringBuilder> messages = getMessages();
		if (messages == null || messages.size() == 0) {
			getLogger().warning("Messages is null or messages size is 0. " + messages);
			return false;
		}

		boolean result = true;

		for (StringBuilder message : messages) {
			boolean newResult = requestProcessor.processRequest(message.toString());
			result &= newResult;
			if (!newResult) {
				getLogger().warning("Unable to process request " + message.toString());
			}
		}

		return result;
	}

	private List<StringBuilder> getMessages() throws IOException {
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
		List<StringBuilder> output = new LinkedList<>();
		StringBuilder builder = new StringBuilder();

		if (firstCharacter != MESSAGE_DELIMITER) {
			builder.append(Character.toString((char) firstCharacter));
		}

		while (reader.ready()) {
			int readValue = reader.read();
			if (readValue != -1) {
				if (readValue == MESSAGE_DELIMITER) {
					if (builder.length() != 0) {
						output.add(builder);
						builder = new StringBuilder();
					}
				} else {
					builder.append(Character.toString((char) readValue));
				}
			} else {
				if (builder.length() != 0) {
					output.add(builder);
				}
				break;
			}
		}
		return output;
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
