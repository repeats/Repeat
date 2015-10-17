package core.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import utilities.ExceptableFunction;

import com.sun.istack.internal.logging.Logger;

import core.controller.Core;

public class ControllerServer {

	private static final Logger LOGGER = Logger.getLogger(ControllerServer.class);

	private static final int DEFAULT_PORT = 9999;
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
		mainThread = new Thread(){
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
		                	LOGGER.info("New Client accepted");
		                } catch (IOException e) {
		                	LOGGER.warning("IO Exception when starting serving", e);
		                	continue;
		                }

		                threadPool.submit(new Runnable() {
							@Override
							public void run() {
								BufferedReader input = null;
								BufferedWriter output = null;
								try {
									input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
									output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
								} catch (IOException e1) {
									LOGGER.warning("IO Exception when open reader and writer for socket", e1);
									return;
								}

				                try {
				                	process(input, output);
				                } catch (IOException e) {
				                	LOGGER.warning("IO Exception when serving client", e);
								} catch (Exception e) {
									LOGGER.warning("Exception when serving client", e);
								} finally {
				                    try {
										socket.close();
									} catch (IOException e) {
										LOGGER.warning("IO Exception when closing socket", e);
									}
				                }
							}
						});
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

	private void process(BufferedReader reader, BufferedWriter writer) throws IOException {
		if (reader == null || writer == null) {
			return;
		}

		StringBuilder builder = new StringBuilder();
		while (reader.ready()) {
			int readValue = reader.read();
			if (readValue != -1) {
				builder.append(Character.toString((char) readValue));
			} else {
				break;
			}
		}

		List<ExceptableFunction<Void, Object, InterruptedException>> callings = RequestParser.parseRequest(builder.toString(), core);
		for (ExceptableFunction<Void, Object, InterruptedException> calling : callings) {
			try {
				Object result = calling.apply(null);
				if (result != null) {
					writer.write("Success. " + result);
				} else {
					writer.write("Success.");
				}
			} catch (InterruptedException e) {
				LOGGER.warning("Failed to execute function from client", e);
				writer.write("Failure");
			}
		}


		writer.flush();
	}

	public void stop() {
		setStop(true);
		while (mainThread.isAlive()) {
			mainThread.interrupt();
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
