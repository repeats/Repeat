package core.userDefinedTask.internals.preconditions;

import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonRootNode;

/**
 * Represents an exact string matching condition.
 */
public class ExactStringMatchCondition extends StringMatchingCondition {

	private String value;

	public static ExactStringMatchCondition of(String value) {
		ExactStringMatchCondition result = new ExactStringMatchCondition();
		result.value = value;
		return result;
	}

	public String getValue() {
		return value;
	}

	@Override
	public boolean isValid(String value) {
		return this.value.equals(value);
	}

	@Override
	public boolean isStatic() {
		return false;
	}

	@Override
	public ExactStringMatchCondition copy() {
		return of(value);
	}

	@Override
	public String jsonTypeName() {
		return "exact_match";
	}

	public static ExactStringMatchCondition parseJSON(JsonNode node) {
		String value = node.getStringValue("value");
		return of(value);
	}

	@Override
	public JsonRootNode jsonize() {
		return JsonNodeFactories.object(
				JsonNodeFactories.field("type", JsonNodeFactories.string(jsonTypeName())),
				JsonNodeFactories.field("value", JsonNodeFactories.string(value))
				);
	}

	private ExactStringMatchCondition() {}
}
