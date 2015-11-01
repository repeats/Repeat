package core.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import utilities.ExceptableFunction;
import utilities.JSONUtility;
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonRootNode;

import com.sun.istack.internal.logging.Logger;

import core.controller.Core;

public class ControllerServer {

	private static final Logger LOGGER = Logger.getLogger(ControllerServer.class);

	private static final int DEFAULT_PORT = 9999;
	private static final int DEFAULT_TIMEOUT_MS = 1000;
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
		                	socket.setSoTimeout(DEFAULT_TIMEOUT_MS);
		                	LOGGER.info("New client accepted");
		                } catch (SocketException e) {
		                	if (!listener.isClosed()) {
		                		LOGGER.severe("Socket exception when serving", e);
		                	}
		                	continue;
		                } catch (IOException e) {
		                	LOGGER.severe("IO Exception when serving", e);
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
								} catch (IOException e) {
									LOGGER.warning("IO Exception when open reader and writer for socket", e);
									return;
								}

								try {
									while (true) {
										LOGGER.info("Doing it\n");
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
			return true;
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

		List<ExceptableFunction<Void, Object, InterruptedException>> callings = RequestParser.parseRequest(builder.toString(), core);
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
