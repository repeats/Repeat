package core.keyChain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import utilities.IJsonable;
import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonRootNode;

/**
 * Enum representing classification categories
 *
 */
public enum MouseGesture implements IJsonable {
	ALPHA("alpha"),
	DERIVATIVE("derivative"),
	GREATER_THAN("greater_than"),
	HAT("hat"),
	HORIZONTAL("horizontal"),
	RANDOM("random"),
	TRIANGLE("triangle"),
	VERTICAL("vertical")
	;


	private final String text;

    /**
     * @param text human readable text form of this classification
     */
    private MouseGesture(final String text) {
        this.text = text;
    }

    /**
     * @return list of enabled mouse gestures that can be used to activate tasks.
     */
    public static List<MouseGesture> enabledGestures() {
    	List<MouseGesture> output = new ArrayList<>();

		for (MouseGesture value : values()) {
			if (value != RANDOM) {
				output.add(value);
			}
		}

    	return output;
    }

    /**
     * Find the mouse gesture given its name.
     *
     * @param name name of the mouse gesture
     * @return the found mouse gesture, or null if cannot find one
     */
    protected static MouseGesture find(String name) {
    	for (MouseGesture classification : MouseGesture.values()) {
    		if (classification.text.equals(name)) {
    			return classification;
    		}
    	}

    	return null;
    }

    /**
     * Parse a json list of strings into a set of mouse gestures.
     *
     * @param nodes the json list of strings
     * @return set of mouse gestures parsed.
     */
    public static Set<MouseGesture> parseJSON(List<JsonNode> nodes) {
    	Set<MouseGesture> output = new HashSet<>();
    	for (JsonNode node : nodes) {
    		String name = node.getStringValue("name");
    		MouseGesture gesture = find(name);

    		if (gesture != null) {
    			output.add(gesture);
    		}
    	}
    	return output;
    }

	@Override
	public JsonRootNode jsonize() {
		return JsonNodeFactories.object(JsonNodeFactories.field("name", JsonNodeFactories.string(text)));
	}
}