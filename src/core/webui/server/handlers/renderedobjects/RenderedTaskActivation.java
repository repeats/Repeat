package core.webui.server.handlers.renderedobjects;

import java.util.List;
import java.util.stream.Collectors;

import core.keyChain.ActivationPhrase;
import core.keyChain.KeyChain;
import core.keyChain.KeySequence;
import core.keyChain.TaskActivation;
import core.keyChain.TaskActivationConstructor;

public class RenderedTaskActivation {
	private List<String> keyChains;
	private List<String> keySequences;
	private List<String> phrases;
	private RenderedMouseGestureActivation mouseGestures;
	private RenderedGlobalActivation globalActivation;
	private Config config;

	public static RenderedTaskActivation fromActivation(TaskActivationConstructor constructor) {
		TaskActivation activation = constructor.getActivation();

		RenderedTaskActivation output = new RenderedTaskActivation();
		output.keyChains = activation.getHotkeys().stream().map(KeyChain::toString).collect(Collectors.toList());
		output.keySequences = activation.getKeySequences().stream().map(KeySequence::toString).collect(Collectors.toList());
		output.phrases = activation.getPhrases().stream().map(ActivationPhrase::toString).collect(Collectors.toList());
		output.mouseGestures = RenderedMouseGestureActivation.fromActivation(activation);
		output.globalActivation = RenderedGlobalActivation.fromActivation(activation);

		TaskActivationConstructor.Config config = constructor.getConfig();
		output.config = Config.of()
							.setDisableGlobalAction(config.isDisabledGlobalKeyAction())
							.setDisableKeyChain(config.isDisableKeyChain())
							.setDisableKeySequence(config.isDisableKeySequence())
							.setDisablePhrase(config.isDisablePhrase())
							.setDisableMouseGesture(config.isDisableMouseGesture());
		return output;
	}

	public static class Config {
		private boolean disableGlobalAction;
		private boolean disableKeyChain;
		private boolean disableKeySequence;
		private boolean disablePhrase;
		private boolean disableMouseGesture;

		public static Config of() {
			return new Config();
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
	}

	public List<String> getKeyChains() {
		return keyChains;
	}

	public void setKeyChains(List<String> keyChains) {
		this.keyChains = keyChains;
	}

	public List<String> getKeySequences() {
		return keySequences;
	}

	public void setKeySequences(List<String> keySequences) {
		this.keySequences = keySequences;
	}

	public List<String> getPhrases() {
		return phrases;
	}

	public void setPhrases(List<String> phrases) {
		this.phrases = phrases;
	}

	public RenderedMouseGestureActivation getMouseGestures() {
		return mouseGestures;
	}

	public void setMouseGestures(RenderedMouseGestureActivation mouseGestures) {
		this.mouseGestures = mouseGestures;
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
