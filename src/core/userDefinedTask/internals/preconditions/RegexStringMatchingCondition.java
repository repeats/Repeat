package core.userDefinedTask.internals.preconditions;

import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonRootNode;

/**
 * Represents a regex string matching condition.
 */
public class RegexStringMatchingCondition extends StringMatchingCondition {

	private static final Logger LOGGER = Logger.getLogger(RegexStringMatchingCondition.class.getName());

	private String regex;
	private Pattern pattern;

	public static RegexStringMatchingCondition of(String regex) {
		return new RegexStringMatchingCondition(regex);
	}

	public static boolean isValidRegex(String regex) {
		try {
			Pattern.compile(regex);
		} catch (PatternSyntaxException e) {
			return false;
		}
		return true;
	}

	@Override
	public boolean isValid(String value) {
		return pattern.matcher(value).matches();
	}

	@Override
	public boolean isStatic() {
		return false;
	}

	public String getRegex() {
		return regex;
	}

	public static RegexStringMatchingCondition parseJSON(JsonNode node) {
		String regex = node.getStringValue("regex");
		if (!isValidRegex(regex)) {
			LOGGER.warning("Regex '" + regex + "' is invalid.");
			return null;
		}
		return of(regex);
	}

	@Override
	public JsonRootNode jsonize() {
		return JsonNodeFactories.object(
				JsonNodeFactories.field("type", JsonNodeFactories.string(jsonTypeName())),
				JsonNodeFactories.field("regex", JsonNodeFactories.string(regex)));
	}

	@Override
	public String jsonTypeName() {
		return "regex";
	}

	private RegexStringMatchingCondition(String regex) {
		this.regex = regex;
		this.pattern = Pattern.compile(regex);
	}
}
