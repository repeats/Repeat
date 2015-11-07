package core.ipc.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import com.sun.istack.internal.logging.Logger;

import core.controller.Core;

public class ControllerServer {

	private static final Logger LOGGER = Logger.getLogger(ControllerServer.class);

	private static final int DEFAULT_PORT = 9999;
	private static final int DEFAULT_TIMEOUT_MS = 10000;
	private static final int MAX_THREAD_COUNT = 10;

	private boolean isStopped;
	private final ScheduledThreadPoolExecutor threadPool;
	private int port;
	private ServerSocket listener;
	private Thread mainThread;

	private final Core core;

	public ControllerServer(Core core) {
		this.port = DEFAULT_PORT;
		threadPool = new ScheduledThreadPoolExecutor(MAX_THREAD_COUNT);
		this.core = core;
	}

	public void start() throws IOException {
		setStop(false);
		mainThread = new Thread() {
			@Override
			public void run() {
				try {
					listener = new ServerSocket(port);
				} catch (IOException e) {
					LOGGER.severe("IO Exception when starting server", e);
					return;
				}

				try {
					while (!isStopped()) {
						LOGGER.info("Waiting for client");
		                final Socket socket;
		                try {
		                	socket = listener.accept();
		                	socket.setSoTimeout(DEFAULT_TIMEOUT_MS);
		                	LOGGER.info("New client accepted: " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort());
		                } catch (SocketException e) {
		                	if (!listener.isClosed()) {
		                		LOGGER.severe("Socket exception when serving", e);
		                	}
		                	continue;
		                } catch (IOException e) {
		                	LOGGER.severe("IO Exception when serving", e);
		                	continue;
		                }

		                threadPool.submit(new ClientServingThread(core, socket));
		            }
				} finally {
					try {
						listener.close();
					} catch (IOException e) {
						LOGGER.severe("IO Exception when closing server", e);
					}
				}
			}
		};
		mainThread.start();
	}

	public void stop() {
		setStop(true);
		if (listener != null) {
			try {
				listener.close();
			} catch (IOException e) {
				LOGGER.warning("Failed to close server socket", e);
			}
		}
	}

	private synchronized boolean isStopped() {
		return isStopped;
	}

	private synchronized void setStop(boolean isStopped) {
		this.isStopped = isStopped;
	}

	public void changePort(int newPort) throws IOException {
		this.stop();
		this.port = newPort;
		this.start();
	}
}
