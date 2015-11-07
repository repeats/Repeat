package core.ipc.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import utilities.JSONUtility;
import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonRootNode;

import com.sun.istack.internal.logging.Logger;

import core.keyChain.KeyChain;
import core.userDefinedTask.UserDefinedAction;

public abstract class AbstractIPCClient {

	protected static final String STATUS_SUCCESS = "Success";
	protected static final String STATUS_FAILURE = "Failure";

	protected static final int SERVER_TIMEOUT_MS = 1000;
	protected static final int MAX_TIMEOUT_MS = 1000;
	protected static final int MAX_RTT_MS = 100;

	private static final int DEFAULT_AVERAGE_TASK_COUNT = 10;

	protected int port;

	protected String serverAddress;
	protected Socket socket;
	protected BufferedReader reader;
	protected BufferedWriter writer;
	protected Map<Integer, ClientTask> tasks;

	protected AbstractIPCClient(String serverAddress, int port) {
		this.port = port;
		this.serverAddress = serverAddress;
		tasks = new HashMap<>(DEFAULT_AVERAGE_TASK_COUNT);
	}

	public void connect() throws UnknownHostException, IOException {
		socket = new Socket(serverAddress, port);
		socket.setSoTimeout(MAX_TIMEOUT_MS);

        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
	}

	public void stop() throws IOException {
		if (reader != null) {
			reader.close();
		}

		if (writer != null) {
			writer.close();
		}

		if (socket != null) {
			socket.close();
		}
	}

	public void changePort(int port) throws IOException {
		stop();
		setPort(port);
		connect();
	}

	public boolean syncTasks(Set<UserDefinedAction> actions) {
		JsonRootNode message = JsonNodeFactories.object(
				JsonNodeFactories.field("action", JsonNodeFactories.string("sync_tasks")),
				JsonNodeFactories.field("params",
					JsonNodeFactories.array()
				)
			);

		JsonNode reply = fullRequest(message);
		if (reply == null) {
			return false;
		}

		this.tasks.clear();
		List<JsonNode> jsonTasks = reply.getArrayNode("existing_tasks");
		for (JsonNode task : jsonTasks) {
			ClientTask clientTask = ClientTask.parseJSON(task);
			if (clientTask == null) {
				continue;
			}

			this.tasks.put(clientTask.getId(), clientTask);
		}

		return true;
	}

	public int createTask(File file) {
		JsonRootNode requestMessage = JsonNodeFactories.object(
				JsonNodeFactories.field("action", JsonNodeFactories.string("create_task")),
				JsonNodeFactories.field("params",
					JsonNodeFactories.array(
						JsonNodeFactories.string(file.getAbsolutePath())
					)
				)
			);

		ClientTask newTask = clientTaskResponseFullRequest(requestMessage);
		if (newTask == null) {
			return -1;
		}

		this.tasks.put(newTask.getId(), newTask);

		return newTask.getId();
	}

	public boolean runTask(int id, KeyChain invoker) {
		JsonRootNode requestMessage = JsonNodeFactories.object(
				JsonNodeFactories.field("action", JsonNodeFactories.string("run_task")),
				JsonNodeFactories.field("params",
					JsonNodeFactories.array(
						JsonNodeFactories.number(id),
						invoker.jsonize()
					)
				)
			);

		ClientTask runningTask = clientTaskResponseFullRequest(requestMessage);
		if (runningTask == null || runningTask.getId() != id) {
			return false;
		}

		return true;
	}

	public boolean removeTask(int id) {
		JsonRootNode message = JsonNodeFactories.object(
				JsonNodeFactories.field("action", JsonNodeFactories.string("remove_task")),
				JsonNodeFactories.field("params",
					JsonNodeFactories.array(JsonNodeFactories.number(id))
				)
			);

		ClientTask removed = clientTaskResponseFullRequest(message);
		if (removed == null || removed.getId() != id) {
			return false;
		}
		tasks.remove(removed.getId());
		return true;
	}

	protected ClientTask clientTaskResponseFullRequest(JsonRootNode jsonRequest) {
		JsonNode response = fullRequest(jsonRequest);
		if (response == null) {
			return null;
		}

		ClientTask output = ClientTask.parseJSON(response);
		return output;
	}

	protected JsonNode fullRequest(JsonRootNode jsonRequest) {
		String request = JSONUtility.jsonToString(jsonRequest);

		if (!sendRequest(request)) {
			return null;
		}

		String replyString = getReply(MAX_RTT_MS);
		getLogger().info("Doing the reply " + replyString);

		if (!replyString.isEmpty()) {
			JsonRootNode reply = JSONUtility.jsonFromString(replyString);

			String status = "";
			JsonNode message = null;
			try {
				status = reply.getStringValue("status");
				message = reply.getNode("message");
			} catch (IllegalArgumentException e) {
				getLogger().info("Failed to perform request. Error in receiving reply message", e);
				return null;
			}

			if (status.equals(STATUS_SUCCESS)) {
				return message;
			} else {
				getLogger().info("Failed to perform request. Message is " + message);
				return null;
			}
		} else {
			getLogger().warning("Failed to retrieve reply from server.");
			return null;
		}
	}

	private boolean sendRequest(String request) {
		try {
			writer.write(request);
			writer.flush();
			return true;
		} catch (IOException e) {
			getLogger().warning("Failed to send request", e);
			return false;
		}
	}

	private String getReply(long timeout) {
		long start = System.currentTimeMillis();
		StringBuffer buffer;

		while (true) {
			buffer = new StringBuffer();
			try {
				while (reader.ready()) {
					int readValue = reader.read();
					if (readValue != -1) {
						buffer.append(Character.toString((char) readValue));
					} else {
						break;
					}
				}
			} catch (IOException e) {
				getLogger().warning("Failed to receive reply", e);
				return "";
			}

			if (buffer.toString().trim().length() != 0) {
				return buffer.toString();
			}

			long timeLeft = timeout - (System.currentTimeMillis() - start);
			if (timeLeft <= 0) {
				return "";
			}
		}
	}

	protected abstract Logger getLogger();

	private void setPort(int port) {
		this.port = port;
	}
}
