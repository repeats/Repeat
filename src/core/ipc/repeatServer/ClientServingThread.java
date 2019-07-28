package core.ipc.repeatServer;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import core.controller.Core;
import core.ipc.IPCProtocol;
import core.ipc.repeatServer.processors.ServerMainProcessor;
import utilities.ILoggable;

class ClientServingThread implements Runnable, ILoggable {

	private Boolean stopped;
	private final Socket socket;
	private BufferedReader reader;
	private DataOutputStream writer;

	private final ServerMainProcessor requestProcessor;
	private final MainMessageSender messageSender;

	protected ClientServingThread(Core core, Socket socket) {
		this.socket = socket;

		messageSender = new MainMessageSender();
		requestProcessor = new ServerMainProcessor(core, messageSender);

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
        } catch (SocketException e) {
        	getLogger().log(Level.WARNING, "Socket Exception when serving client", e);
        	return false;
		} catch (IOException e) {
        	getLogger().log(Level.WARNING, "IO Exception when serving client", e);
        	return false;
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Exception when serving client", e);
			return false;
		}
	}

	private boolean process() throws IOException, InterruptedException {
		if (reader == null || writer == null) {
			getLogger().warning("Unable to process with reader " + reader + " and writer " + writer);
			return false;
		}

		List<String> messages = IPCProtocol.getMessages(reader);
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
