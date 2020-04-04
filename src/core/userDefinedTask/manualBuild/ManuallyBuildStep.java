package core.userDefinedTask.manualBuild;

import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonRootNode;
import core.controller.Core;
import utilities.json.IJsonable;
import utilities.json.Jsonizer;

public abstract class ManuallyBuildStep implements IJsonable {
	public abstract void execute(Core controller) throws InterruptedException;
	public abstract String getDisplayString();
	public abstract String getJsonSignature();

	protected boolean parse(JsonNode node) {
		return Jsonizer.parse(getDataNode(node), this);
	}

	private static JsonNode getDataNode(JsonNode node) {
		return node.getNode("data");
	}

	private final JsonNode jsonizeContent() {
		return Jsonizer.jsonize(this);
	}

	@Override
	public final JsonRootNode jsonize() {
		return JsonNodeFactories.object(
				JsonNodeFactories.field("signature", JsonNodeFactories.string(getJsonSignature())),
				JsonNodeFactories.field("data", jsonizeContent()));
	}
}
