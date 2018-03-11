package core.keyChain;

import java.util.logging.Logger;

import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonRootNode;

public class ActivationPhrase extends KeySeries {

	private static final Logger LOGGER = Logger.getLogger(ActivationPhrase.class.getName());
	private String value;

	private ActivationPhrase(String value) {
		this.value = value;
	}

	public static ActivationPhrase of(String value) {
		return new ActivationPhrase(value);
	}

	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		return value;
	}

	@Override
	public boolean isEmpty() {
		return value.isEmpty();
	}

	@Override
	public boolean collideWith(KeySeries series) {
		if (!(series instanceof ActivationPhrase)) {
			throw new IllegalStateException("Cannot check collision with class " + series.getClass());
		}

		ActivationPhrase other = (ActivationPhrase) series;
		if (value.length() > other.value.length()) {
			return value.indexOf(other.value) >= 0;
		} else {
			return other.value.indexOf(value) >= 0;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ActivationPhrase other = (ActivationPhrase) obj;
		if (value == null) {
			if (other.value != null) {
				return false;
			}
		} else if (!value.equals(other.value)) {
			return false;
		}
		return true;
	}

	@Override
	public JsonRootNode jsonize() {
		return JsonNodeFactories.object(JsonNodeFactories.field("value", JsonNodeFactories.string(value)));
	}

	public static ActivationPhrase parseJSON(JsonNode node) {
		if (!node.isStringValue("value")) {
			LOGGER.warning("Missing `value` field in JSON node " + node);
			return null;
		}

		String value = node.getStringValue("value");
		return of(value);
	}

}
