package core.ipc.repeatServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.logging.Level;
import java.util.logging.Logger;

import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import core.controller.Core;
import core.ipc.IPCServiceWithModifablePort;
import utilities.JSONUtility;

public class ControllerServer extends IPCServiceWithModifablePort {

	protected static final Charset ENCODING = StandardCharsets.UTF_8;
	private static final int DEFAULT_PORT = 9999;
	private static final int DEFAULT_TIMEOUT_MS = 10000;
	private static final int MAX_THREAD_COUNT = 10;

	private boolean isStopped;
	private final ScheduledThreadPoolExecutor threadPool;
	private final LinkedList<ClientServingThread> clientServingThreads;
	private ServerSocket listener;
	private Thread mainThread;

	public ControllerServer() {
		threadPool = new ScheduledThreadPoolExecutor(MAX_THREAD_COUNT);
		clientServingThreads = new LinkedList<>();
		this.setPort(DEFAULT_PORT);
	}

	@Override
	protected void start() throws IOException {
		setStop(false);

		mainThread = new Thread() {
			@Override
			public void run() {
				try {
					listener = new ServerSocket(port);
				} catch (IOException e) {
					getLogger().log(Level.SEVERE, "IO Exception when starting server", e);
					return;
				}

				try {
					getLogger().info("Waiting for client connections...");
					while (!isStopped()) {
		                final Socket socket;
		                try {
		                	socket = listener.accept();
		                	socket.setSoTimeout(DEFAULT_TIMEOUT_MS);
		                	getLogger().info("New client accepted: " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort());
		                } catch (SocketException e) {
		                	if (!listener.isClosed()) {
		                		getLogger().log(Level.SEVERE, "Socket exception when serving", e);
		                	}
		                	continue;
		                } catch (IOException e) {
		                	getLogger().log(Level.SEVERE, "IO Exception when serving", e);
		                	continue;
		                }

		                ClientServingThread newClient = new ClientServingThread(Core.getInstance(), socket);
		                clientServingThreads.add(newClient);
		                threadPool.submit(newClient);
		            }
				} finally {
					try {
						listener.close();
					} catch (IOException e) {
						getLogger().log(Level.SEVERE, "IO Exception when closing server", e);
					}
				}

				getLogger().log(Level.INFO, "Controller server terminating...");
				for (ClientServingThread clientThread : clientServingThreads) {
					clientThread.stop();
				}
				clientServingThreads.clear();
				getLogger().log(Level.INFO, "Controller server terminated!");
			}
		};
		mainThread.start();
	}

	@Override
	public void stop() {
		setStop(true);
		if (listener != null) {
			try {
				listener.close();
			} catch (IOException e) {
				getLogger().log(Level.SEVERE, "Failed to close server socket", e);
			}
		}
	}

	private synchronized boolean isStopped() {
		return isStopped;
	}

	private synchronized void setStop(boolean isStopped) {
		this.isStopped = isStopped;
	}

	@Override
	public boolean isRunning() {
		return mainThread != null && mainThread.isAlive();
	}

	@Override
	public String getName() {
		return "Controller server";
	}

	@Override
	protected JsonNode getSpecificConfig() {
		 return JSONUtility.addChild(super.getSpecificConfig(), "port", JsonNodeFactories.number(port));
	}

	@Override
	public Logger getLogger() {
		return Logger.getLogger(ControllerServer.class.getName());
	}
}
