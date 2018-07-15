package core.webui.server.handlers.renderedobjects;

import java.util.List;
import java.util.stream.Collectors;

import core.keyChain.ActivationPhrase;
import core.keyChain.KeyChain;
import core.keyChain.KeySequence;
import core.keyChain.TaskActivation;

public class RenderedActivation {
	private List<String> keyChains;
	private List<String> keySequences;
	private List<String> phrases;
	private RenderedMouseGestureActivation mouseGestures;

	public static RenderedActivation fromActivation(TaskActivation activation) {
		RenderedActivation output = new RenderedActivation();
		output.keyChains = activation.getHotkeys().stream().map(KeyChain::toString).collect(Collectors.toList());
		output.keySequences = activation.getKeySequences().stream().map(KeySequence::toString).collect(Collectors.toList());
		output.phrases = activation.getPhrases().stream().map(ActivationPhrase::toString).collect(Collectors.toList());
		output.mouseGestures = RenderedMouseGestureActivation.fromActivation(activation);
		return output;
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
}
