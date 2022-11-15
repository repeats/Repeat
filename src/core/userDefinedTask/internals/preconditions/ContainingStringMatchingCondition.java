package core.userDefinedTask.internals.preconditions;

import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonRootNode;

/**
 * Match a string if it contains certain substring.
 */
public class ContainingStringMatchingCondition extends StringMatchingCondition {

	private String substring;

	public static ContainingStringMatchingCondition of(String substring) {
		return new ContainingStringMatchingCondition(substring);
	}

	public String getSubstring() {
		return substring;
	}

	@Override
	public boolean isValid(String value) {
		return value.contains(substring);
	}

	@Override
	public boolean isStatic() {
		return false;
	}

	@Override
	public String jsonTypeName() {
		return "containing";
	}

	public static ContainingStringMatchingCondition parseJSON(JsonNode node) {
		String substring = node.getStringValue("substring");
		return of(substring);
	}

	@Override
	public JsonRootNode jsonize() {
		return JsonNodeFactories.object(
				JsonNodeFactories.field("type", JsonNodeFactories.string(jsonTypeName())),
				JsonNodeFactories.field("substring", JsonNodeFactories.string(substring))
				);
	}

	private ContainingStringMatchingCondition(String substring) {
		this.substring = substring;
	}
}
