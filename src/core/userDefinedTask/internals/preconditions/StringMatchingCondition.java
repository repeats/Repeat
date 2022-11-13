package core.userDefinedTask.internals.preconditions;

import java.util.logging.Logger;

import argo.jdom.JsonNode;
import utilities.json.IJsonable;

/**
 * An abstract string matching condition.
 */
public abstract class StringMatchingCondition implements IJsonable {

	private static final Logger LOGGER = Logger.getLogger(StringMatchingCondition.class.getName());

	public abstract boolean isValid(String value);

	// Whether this is a static condition that doesn't depend on any external parameters.
	public abstract boolean isStatic();

	public abstract String jsonTypeName();

	public static StringMatchingCondition parseJSON(JsonNode node) {
		String type = node.getStringValue("type");
		if (type.equals(ExactStringMatchCondition.of("xxx").jsonTypeName())) {
			return ExactStringMatchCondition.parseJSON(node);
		}

		if (type.equals(RegexStringMatchingCondition.of("xxx").jsonTypeName())) {
			return RegexStringMatchingCondition.parseJSON(node);
		}

		if (type.equals(AlwaysMatchingStringCondition.INSTANCE.jsonTypeName())) {
			return AlwaysMatchingStringCondition.parseJSON(node);
		}

		LOGGER.warning("Unknown string matching condition of type '" + type + "'.");
		return null;
	}
}
