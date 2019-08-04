package core.ipc.repeatClient.repeatPeerClient;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.Socket;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonRootNode;
import core.ipc.IIPCService;
import core.ipc.repeatClient.repeatPeerClient.api.RepeatsClientApi;
import utilities.json.IJsonable;

public class RepeatsPeerServiceClient extends IIPCService implements IJsonable {

	private static final Logger LOGGER = Logger.getLogger(RepeatsPeerServiceClient.class.getName());

	private String id;
	private String host;
	private Socket socket;
	private ResponseManager responseManager;

	private RepeatPeerServiceClientReader reader;
	private RepeatPeerServiceClientWriter writer;

	private Thread readerThread, writerThread;
	private RepeatsClientApi api;

	private RepeatsPeerServiceClient(String id, String host, int port) {
		this.id = id;
		this.port = port;
		this.host = host;
		this.responseManager = new ResponseManager();
	}

	public RepeatsPeerServiceClient(String host, int port) {
		this(UUID.randomUUID().toString(), host, port);
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

		readerThread = new Thread(reader);
		writerThread = new Thread(writer);
		readerThread.start();
		writerThread.start();
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
			readerThread.join();
			writerThread.join();
		} catch (InterruptedException e) {
			LOGGER.log(Level.WARNING, "Waiting for reader and writer to stop.", e);
		}

		socket.close();
		socket = null;
	}

	public String getClientId() {
		return id;
	}

	@Override
	public boolean isRunning() {
		return socket != null && !socket.isClosed();
	}

	public String getHost() {
		return host;
	}

	@Override
	public String getName() {
		return "Repeats remote client " + host + ":" + port;
	}

	@Override
	public Logger getLogger() {
		return LOGGER;
	}

	@Override
	public JsonRootNode jsonize() {
		return JsonNodeFactories.object(
				JsonNodeFactories.field("id", JsonNodeFactories.string(id)),
				JsonNodeFactories.field("host", JsonNodeFactories.string(host)),
				JsonNodeFactories.field("port", JsonNodeFactories.number(port)),
				JsonNodeFactories.field("launch_at_startup", JsonNodeFactories.booleanNode(isLaunchAtStartup()))
				);
	}

	public static RepeatsPeerServiceClient parseJSON(JsonNode node) {
		String id = node.getStringValue("id");
		String host = node.getStringValue("host");
		String portString = node.getNumberValue("port");
		int port = Integer.parseInt(portString);
		boolean launchAtStartup = node.getBooleanValue("launch_at_startup");

		RepeatsPeerServiceClient client = new RepeatsPeerServiceClient(id, host, port);
		client.setLaunchAtStartup(launchAtStartup);
		return client;
	}
}
