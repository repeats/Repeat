package core.ipc.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.List;

import utilities.ExceptableFunction;
import utilities.JSONUtility;
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonRootNode;

import com.sun.istack.internal.logging.Logger;

import core.controller.Core;

class ClientServingThread implements Runnable {

	private static final Logger LOGGER = Logger.getLogger(ControllerServer.class);

	private static final int MAX_RETRY = 10;

	private final Socket socket;
	private final Core core;
	private int retryCount;

	protected ClientServingThread(Core core, Socket socket) {
		this.socket = socket;
		this.core = core;
		retryCount = 0;
	}

	@Override
	public void run() {
		BufferedReader input = null;
		BufferedWriter output = null;
		try {
			input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		} catch (IOException e) {
			LOGGER.warning("IO Exception when open reader and writer for socket", e);
			return;
		}

		try {
			while (true) {
				if (!processLoop(input, output)) {
					break;
				}
			}
			LOGGER.info("Finished\n");
		} finally {
			try {
				input.close();
			} catch (IOException e) {
				LOGGER.warning("IO Exception when closing input socket", e);
			}

			try {
				output.close();
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

	private boolean processLoop(BufferedReader reader, BufferedWriter writer) {
		try {
        	return process(reader, writer);
        } catch (IOException e) {
        	LOGGER.warning("IO Exception when serving client", e);
        	return false;
		} catch (Exception e) {
			LOGGER.warning("Exception when serving client", e);
			return false;
		}
	}

	private boolean process(BufferedReader reader, BufferedWriter writer) throws IOException {
		if (reader == null || writer == null) {
			return false;
		}

		/**
		 * Create a blocking read waiting for the next communication
		 */
		int firstCharacter = reader.read();
		if (firstCharacter == -1) {
			retryCount++;
			return retryCount < MAX_RETRY;
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

		List<ExceptableFunction<Void, Object, InterruptedException>> callings = ServerRequestParser.parseRequest(builder.toString(), core);
		if (callings.size() == 0) {
			JsonRootNode reply = JsonNodeFactories.object(
					JsonNodeFactories.field("status", JsonNodeFactories.string("Terminating connection")),
					JsonNodeFactories.field("result", JsonNodeFactories.string(""))
					);
			writer.write(JSONUtility.jsonToString(reply));
			writer.flush();
			return false;
		}

		for (ExceptableFunction<Void, Object, InterruptedException> calling : callings) {
			JsonRootNode reply;
			try {
				Object result = calling.apply(null);
				 reply = JsonNodeFactories.object(
										JsonNodeFactories.field("status", JsonNodeFactories.string("Success")),
										JsonNodeFactories.field("result", JsonNodeFactories.string(result + ""))
										);
			} catch (InterruptedException e) {
				LOGGER.warning("Failed to execute function from client", e);
				reply = JsonNodeFactories.object(
										JsonNodeFactories.field("status", JsonNodeFactories.string("Failure")),
										JsonNodeFactories.field("result", JsonNodeFactories.string(""))
										);
			}
			writer.write(JSONUtility.jsonToString(reply));
		}

		writer.flush();
		return true;
	}
}
