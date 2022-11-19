package core.userDefinedTask.internals.preconditions;

import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonRootNode;

/**
 * Condition that always returns valid.
 */
public class AlwaysMatchingStringCondition extends StringMatchingCondition {

	public static final AlwaysMatchingStringCondition INSTANCE = new AlwaysMatchingStringCondition();

	@Override
	public JsonRootNode jsonize() {
		return JsonNodeFactories.object(JsonNodeFactories.field("type", JsonNodeFactories.string(jsonTypeName())));
	}

	@Override
	public boolean isValid(String value) {
		return true;
	}

	@Override
	public boolean isStatic() {
		return true;
	}

	@Override
	public AlwaysMatchingStringCondition copy() {
		return INSTANCE;
	}

	@Override
	public String jsonTypeName() {
		return "always_valid";
	}

	public static AlwaysMatchingStringCondition parseJSON(JsonNode node) {
		return INSTANCE;
	}

	private AlwaysMatchingStringCondition() {}
}
