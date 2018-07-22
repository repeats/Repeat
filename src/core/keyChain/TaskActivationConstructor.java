package core.keyChain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import utilities.StringUtilities;

public class TaskActivationConstructor {

	private List<KeyStroke> strokes;
	private List<KeyChain> keyChains;
	private List<KeySequence> keySequences;
	private List<ActivationPhrase> phrases;
	private List<MouseGesture> mouseGestures;
	private boolean listening;

	public TaskActivationConstructor(TaskActivation reference) {
		strokes = new ArrayList<>();
		keyChains = new ArrayList<>(reference.getHotkeys());
		keySequences = new ArrayList<>(reference.getKeySequences());
		phrases = new ArrayList<>(reference.getPhrases());
		mouseGestures = new ArrayList<>(reference.getMouseGestures());
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
	}

	public void addAsKeyChain() {
		if (strokes.isEmpty()) {
			return;
		}
		keyChains.add(new KeyChain(strokes));
		strokes = new ArrayList<>();
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
		strokes = new ArrayList<>();
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
}
