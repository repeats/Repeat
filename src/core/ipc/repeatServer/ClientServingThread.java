package core.ipc.repeatServer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import com.sun.istack.internal.logging.Logger;

import core.controller.Core;

class ClientServingThread implements Runnable {

	private static final Logger LOGGER = Logger.getLogger(ControllerServer.class);

	private static final int MAX_RETRY = 100;

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
			LOGGER.warning("IO Exception when open reader and writer for socket", e);
			return;
		}

		try {
			while (!isStopped()) {
				if (!processLoop()) {
					break;
				}
			}
			LOGGER.info("Finished\n");
		} finally {
			try {
				reader.close();
				reader = null;
			} catch (IOException e) {
				LOGGER.warning("IO Exception when closing input socket", e);
			}

			try {
				writer.close();
				writer = null;
			} catch (IOException e) {
				LOGGER.warning("IO Exception when closing output socket", e);
			}

            try {
				socket.close();
			} catch (IOException e) {
				LOGGER.warning("IO Exception when closing socket", e);
			}
        }
	}

	private boolean processLoop() {
		try {
        	return process();
        } catch (IOException e) {
        	LOGGER.warning("IO Exception when serving client", e);
        	return false;
		} catch (Exception e) {
			LOGGER.warning("Exception when serving client", e);
			return false;
		}
	}

	private boolean process() throws IOException {
		if (reader == null || writer == null) {
			return false;
		}

		StringBuilder message = getMessage();
		if (message == null) {
			return false;
		}

		return requestProcessor.processRequest(message.toString());
	}

	private StringBuilder getMessage() throws IOException {
		/**
		 * Create a blocking read waiting for the next communication
		 */
		int firstCharacter = reader.read();
		if (firstCharacter == -1) {
			retryCount++;
			if (retryCount < MAX_RETRY) {
				return new StringBuilder();
			} else {
				LOGGER.warning("Max retry reached.");
				return null;
			}
		}

		/**
		 * Build the request, remembering that
		 */
		StringBuilder builder = new StringBuilder();
		builder.append(Character.toString((char) firstCharacter));

		while (reader.ready()) {
			int readValue = reader.read();
			if (readValue != -1) {
				builder.append(Character.toString((char) readValue));
			} else {
				break;
			}
		}
		return builder;
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
}
