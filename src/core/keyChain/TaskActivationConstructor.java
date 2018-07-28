package core.keyChain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import utilities.StringUtilities;

public class TaskActivationConstructor {

	private LinkedList<KeyStroke> strokes;
	private List<KeyChain> keyChains;
	private List<KeySequence> keySequences;
	private List<ActivationPhrase> phrases;
	private List<MouseGesture> mouseGestures;
	private boolean listening;

	private Config config;

	public TaskActivationConstructor(TaskActivation reference) {
		this(reference, Config.of());
	}

	public TaskActivationConstructor(TaskActivation reference, Config config) {
		strokes = new LinkedList<>();
		keyChains = new ArrayList<>(reference.getHotkeys());
		keySequences = new ArrayList<>(reference.getKeySequences());
		phrases = new ArrayList<>(reference.getPhrases());
		mouseGestures = new ArrayList<>(reference.getMouseGestures());
		this.config = config;
	}

	public String getStrokes() {
		return StringUtilities.join(strokes.stream().map(KeyStroke::toString).collect(Collectors.toList()), " + ");
	}

	public void clearStrokes() {
		strokes.clear();
	}

	public TaskActivation getActivation() {
		return TaskActivation.newBuilder().withHotKeys(keyChains).withKeySequence(keySequences).withPhrases(phrases).withMouseGestures(mouseGestures).build();
	}

	public void startListening() {
		listening = true;
	}

	public void stopListening() {
		listening = false;
	}

	public void onStroke(KeyStroke stroke) {
		if (!listening) {
			return;
		}
		strokes.add(stroke);
		if (strokes.size() > config.maxStrokes) {
			strokes.removeFirst();
		}
	}

	public void addAsKeyChain() {
		if (strokes.isEmpty()) {
			return;
		}
		keyChains.add(new KeyChain(strokes));
		strokes = new LinkedList<>();
	}

	public void removeKeyChain(int index) {
		if (index < 0 || index >= keyChains.size()) {
			return;
		}
		keyChains.remove(index);
	}

	public void addAsKeySequence() {
		if (strokes.isEmpty()) {
			return;
		}
		keySequences.add(new KeySequence(strokes));
		strokes = new LinkedList<>();
	}

	public void removeKeySequence(int index) {
		if (index < 0 || index >= keySequences.size()) {
			return;
		}
		keySequences.remove(index);
	}

	public void addPhrase(String phrase) {
		if (phrase.isEmpty()) {
			return;
		}
		phrases.add(ActivationPhrase.of(phrase));
	}

	public void removePhrase(int index) {
		if (index < 0 || index >= phrases.size()) {
			return;
		}
		phrases.remove(index);
	}

	public void setMouseGestures(Collection<MouseGesture> gestures) {
		mouseGestures.clear();
		mouseGestures.addAll(gestures);
	}

	public Config getConfig() {
		return config;
	}

	public static class Config {
		private boolean disableKeyChain;
		private boolean disableKeySequence;
		private boolean disablePhrase;
		private boolean disableMouseGesture;

		private int maxStrokes = Integer.MAX_VALUE;

		public static Config of() {
			return new Config();
		}

		public static Config ofRestricted() {
			Config config = new Config();
			config.disableKeyChain = true;
			config.disableKeySequence = true;
			config.disablePhrase = true;
			config.disableMouseGesture = true;
			return config;
		}

		public int getMaxStrokes() {
			return maxStrokes;
		}

		public Config setMaxStrokes(int maxStrokes) {
			this.maxStrokes = maxStrokes;
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
}
