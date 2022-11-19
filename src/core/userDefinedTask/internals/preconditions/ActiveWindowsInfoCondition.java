package core.userDefinedTask.internals.preconditions;

import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonRootNode;
import utilities.json.IJsonable;

/**
 * Condition on an active window information.
 */
public class ActiveWindowsInfoCondition implements IJsonable {

	private StringMatchingCondition title;
	private StringMatchingCondition processName;

	public static ActiveWindowsInfoCondition of(StringMatchingCondition title, StringMatchingCondition processName) {
		ActiveWindowsInfoCondition result = new ActiveWindowsInfoCondition();
		result.title = title;
		result.processName = processName;
		return result;
	}

	public ActiveWindowsInfoCondition copy() {
		return of(title.copy(), processName.copy());
	}

	public StringMatchingCondition getTitleCondition() {
		return title;
	}

	public StringMatchingCondition getProcessNameCondition() {
		return processName;
	}

	public boolean isStatic() {
		return title.isStatic() && processName.isStatic();
	}

	private ActiveWindowsInfoCondition() {}

	public static ActiveWindowsInfoCondition parseJSON(JsonNode node) {
		StringMatchingCondition title = AlwaysMatchingStringCondition.INSTANCE;
		if (node.isObjectNode("title")) {
			title = StringMatchingCondition.parseJSON(node.getNode("title"));
			if (title == null) {
				return null;
			}
		}

		StringMatchingCondition processName = AlwaysMatchingStringCondition.INSTANCE;
		if (node.isObjectNode("process_name")) {
			processName = StringMatchingCondition.parseJSON(node.getNode("process_name"));
			if (processName == null) {
				return null;
			}
		}

		return ActiveWindowsInfoCondition.of(title, processName);
	}

	@Override
	public JsonRootNode jsonize() {
		return JsonNodeFactories.object(
				JsonNodeFactories.field("title", title.jsonize()),
				JsonNodeFactories.field("process_name", processName.jsonize())
				);
	}
}
