package utilities.json;

import argo.jdom.JsonNode;
import argo.jdom.JsonRootNode;

/**
 * This is a helper to automatically convert private non-static
 * class attributes into JSON.
 */
public abstract class AutoJsonable implements IJsonable {

	public final boolean parse(JsonNode node) {
		return Jsonizer.parse(node, this);
	}

	@Override
	public final JsonRootNode jsonize() {
		JsonNode node = Jsonizer.jsonize(this);
		if (node == null) {
			return null;
		}
		return node.getRootNode();
	}
}
