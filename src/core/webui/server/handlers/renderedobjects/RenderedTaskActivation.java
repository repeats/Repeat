package core.webui.server.handlers.renderedobjects;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import core.keyChain.ActivationPhrase;
import core.keyChain.KeyChain;
import core.keyChain.KeySequence;
import core.keyChain.TaskActivation;
import core.keyChain.TaskActivationConstructor;

public class RenderedTaskActivation {
	private List<SortedString> keyChains;
	private List<SortedString> keySequences;
	private List<SortedString> phrases;
	private RenderedMouseGestureActivation mouseGestures;
	private RenderedSharedVariablesActivation sharedVariables;
	private RenderedGlobalActivation globalActivation;
	private Config config;

	public static RenderedTaskActivation fromActivation(TaskActivationConstructor constructor) {
		TaskActivation activation = constructor.getActivation();

		RenderedTaskActivation output = new RenderedTaskActivation();
		// Since the lists below are ordered strings, we can't
		// use the Set interface provided by TaskActivation because set
		// iteration does not have any specific order.
		output.keyChains = sortedStrings(constructor.getKeyChains().stream().map(KeyChain::toString).collect(Collectors.toList()));
		output.keySequences = sortedStrings(constructor.getKeySequences().stream().map(KeySequence::toString).collect(Collectors.toList()));
		output.phrases = sortedStrings(constructor.getPhrases().stream().map(ActivationPhrase::toString).collect(Collectors.toList()));
		output.mouseGestures = RenderedMouseGestureActivation.fromActivation(activation);
		output.sharedVariables = RenderedSharedVariablesActivation.fromActivation(constructor.getVariables());
		output.globalActivation = RenderedGlobalActivation.fromActivation(activation);
		TaskActivationConstructor.Config config = constructor.getConfig();
		output.config = Config.of(true)
							.setDisableGlobalAction(config.isDisabledGlobalKeyAction())
							.setDisableKeyChain(config.isDisableKeyChain())
							.setDisableKeySequence(config.isDisableKeySequence())
							.setDisablePhrase(config.isDisablePhrase())
							.setDisableMouseGesture(config.isDisableMouseGesture())
							.setDisableSharedVariable(config.isDisableVariablesActivation());
		return output;
	}

	private static List<SortedString> sortedStrings(List<String> vals) {
		return IntStream.range(0, vals.size()).mapToObj(i -> SortedString.of(i, vals.get(i))).sorted(Comparator.comparing(s -> s.getValue())).collect(Collectors.toList());
	}

	public static class SortedString {
		int originalIndex;
		String value;

		public static SortedString of(int originalIndex, String value) {
			SortedString result = new SortedString();
			result.originalIndex = originalIndex;
			result.value = value;
			return result;
		}

		public int getOriginalIndex() {
			return originalIndex;
		}
		public void setOriginalIndex(int originalIndex) {
			this.originalIndex = originalIndex;
		}
		public String getValue() {
			return value;
		}
		public void setValue(String value) {
			this.value = value;
		}
	}

	public static class Config {
		private boolean modifiable;
		private boolean disableGlobalAction;
		private boolean disableKeyChain;
		private boolean disableKeySequence;
		private boolean disablePhrase;
		private boolean disableMouseGesture;
		private boolean disableSharedVariable;

		public static Config of(boolean modifiable) {
			Config result = new Config();
			result.modifiable = modifiable;
			return result;
		}

		public boolean isModifiable() {
			return modifiable;
		}

		public void setModifiable(boolean modifiable) {
			this.modifiable = modifiable;
		}

		public boolean isDisableGlobalAction() {
			return disableGlobalAction;
		}

		public Config setDisableGlobalAction(boolean disableGlobalAction) {
			this.disableGlobalAction = disableGlobalAction;
			return this;
		}

		public boolean isDisableKeyChain() {
			return disableKeyChain;
		}

		public Config setDisableKeyChain(boolean disableKeyChain) {
			this.disableKeyChain = disableKeyChain;
			return this;
		}

		public boolean isDisableKeySequence() {
			return disableKeySequence;
		}

		public Config setDisableKeySequence(boolean disableKeySequence) {
			this.disableKeySequence = disableKeySequence;
			return this;
		}

		public boolean isDisablePhrase() {
			return disablePhrase;
		}

		public Config setDisablePhrase(boolean disablePhrase) {
			this.disablePhrase = disablePhrase;
			return this;
		}

		public boolean isDisableMouseGesture() {
			return disableMouseGesture;
		}

		public Config setDisableMouseGesture(boolean disableMouseGesture) {
			this.disableMouseGesture = disableMouseGesture;
			return this;
		}

		public boolean isDisableSharedVariable() {
			return disableSharedVariable;
		}

		public Config setDisableSharedVariable(boolean disableSharedVariable) {
			this.disableSharedVariable = disableSharedVariable;
			return this;
		}
	}

	public List<SortedString> getKeyChains() {
		return keyChains;
	}
	public void setKeyChains(List<SortedString> keyChains) {
		this.keyChains = keyChains;
	}
	public List<SortedString> getKeySequences() {
		return keySequences;
	}
	public void setKeySequences(List<SortedString> keySequences) {
		this.keySequences = keySequences;
	}
	public List<SortedString> getPhrases() {
		return phrases;
	}
	public void setPhrases(List<SortedString> phrases) {
		this.phrases = phrases;
	}
	public RenderedMouseGestureActivation getMouseGestures() {
		return mouseGestures;
	}
	public void setMouseGestures(RenderedMouseGestureActivation mouseGestures) {
		this.mouseGestures = mouseGestures;
	}
	public RenderedSharedVariablesActivation getSharedVariables() {
		return sharedVariables;
	}
	public void setSharedVariables(RenderedSharedVariablesActivation sharedVariables) {
		this.sharedVariables = sharedVariables;
	}
	public RenderedGlobalActivation getGlobalActivation() {
		return globalActivation;
	}
	public void setGlobalActivation(RenderedGlobalActivation globalActivation) {
		this.globalActivation = globalActivation;
	}
	public Config getConfig() {
		return config;
	}
	public void setConfig(Config config) {
		this.config = config;
	}
}
