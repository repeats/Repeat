package core.ipc.repeatServer;

import java.util.logging.Level;
import java.util.logging.Logger;

import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonRootNode;
import utilities.json.IJsonable;

/**
 * This class is very similar to <class> UserDefinedAction </class>, except that this is meant to be
 * for direct communication between ipc java client and native server
 * @author hptruong93
 *
 */
public class ClientTask implements IJsonable {
	private static final Logger LOGGER = Logger.getLogger(ClientTask.class.getName());

	private final String id;
	private final String fileName;

	protected ClientTask(String id, String fileName) {
		this.id = id;
		this.fileName = fileName;
	}

	public static ClientTask of(String id, String fileName) {
		return new ClientTask(id, fileName);
	}

	public String getId() {
		return id;
	}

	public String getFileName() {
		return fileName;
	}

	@Override
	public JsonRootNode jsonize() {
		return JsonNodeFactories.object(
				JsonNodeFactories.field("id", JsonNodeFactories.string(id)),
				JsonNodeFactories.field("file_name", JsonNodeFactories.string(fileName))
				);
	}

	public static ClientTask parseJSON(JsonNode node) {
		try {
			String id = node.getStringValue("id");
			String fileName = node.getStringValue("file_name");
			return new ClientTask(id, fileName);
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Failed to parse ClientTask.", e);
			return null;
		}
	}
}
