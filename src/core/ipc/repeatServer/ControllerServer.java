package core.ipc.repeatServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.logging.Level;
import java.util.logging.Logger;

import core.controller.Core;
import core.ipc.repeatClient.IIPCService;

public class ControllerServer extends IIPCService {

	private static final int DEFAULT_PORT = 9999;
	private static final int DEFAULT_TIMEOUT_MS = 10000;
	private static final int MAX_THREAD_COUNT = 10;

	private boolean isStopped;
	private final ScheduledThreadPoolExecutor threadPool;
	private ServerSocket listener;
	private Thread mainThread;

	private final Core core;


	public ControllerServer(Core core) {
		threadPool = new ScheduledThreadPoolExecutor(MAX_THREAD_COUNT);
		this.core = core;
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
					while (!isStopped()) {
						getLogger().info("Waiting for client");
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

		                threadPool.submit(new ClientServingThread(core, socket));
		            }
				} finally {
					try {
						listener.close();
					} catch (IOException e) {
						getLogger().log(Level.SEVERE, "IO Exception when closing server", e);
					}
				}
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
	public Logger getLogger() {
		return Logger.getLogger(ControllerServer.class.getName());
	}
}
