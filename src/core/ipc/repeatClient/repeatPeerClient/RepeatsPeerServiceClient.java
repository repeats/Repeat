package core.ipc.repeatClient.repeatPeerClient;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.Socket;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.logging.Level;
import java.util.logging.Logger;

import core.ipc.IIPCService;
import core.ipc.repeatClient.repeatPeerClient.api.RepeatsClientApi;

public class RepeatsPeerService extends IIPCService {

	private static final Logger LOGGER = Logger.getLogger(RepeatsPeerService.class.getName());

	private String host;
	private Socket socket;
	private ScheduledThreadPoolExecutor executor;
	private ResponseManager responseManager;

	private RepeatPeerServiceClientReader reader;
	private RepeatPeerServiceClientWriter writer;

	private Future<?> readerFuture, writerFuture;
	private RepeatsClientApi api;

	public RepeatsPeerService(String host, int port, ScheduledThreadPoolExecutor executor) {
		super();
		this.port = port;
		this.host = host;
		this.responseManager = new ResponseManager();
		this.executor = executor;
	}

	public RepeatsClientApi api() {
		return api;
	}

	@Override
	protected void start() throws IOException {
		socket = new Socket(host, port);

		Reader r = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		DataOutputStream w = new DataOutputStream(socket.getOutputStream());
		reader = new RepeatPeerServiceClientReader(r, responseManager);
		writer = new RepeatPeerServiceClientWriter(w, responseManager);
		writer.enqueueKeepAlive();

		api = new RepeatsClientApi(writer);

		readerFuture = executor.submit(reader);
		writerFuture = executor.submit(writer);
	}

	@Override
	protected void stop() throws IOException {
		if (!isRunning()) {
			LOGGER.warning("Repeats client " + host + ":" + port + "cannot be stopped since it is not running.");
			return;
		}
		reader.stop();
		writer.stop();

		try {
			readerFuture.get();
			writerFuture.get();
		} catch (InterruptedException | ExecutionException e) {
			LOGGER.log(Level.WARNING, "Waiting for reader and writer to stop.", e);
		}

		socket.close();
		socket = null;
	}

	@Override
	public boolean isRunning() {
		return socket != null && !socket.isClosed();
	}

	@Override
	public String getName() {
		return "Repeats client " + host + ":" + port;
	}

	@Override
	public Logger getLogger() {
		return LOGGER;
	}
}
