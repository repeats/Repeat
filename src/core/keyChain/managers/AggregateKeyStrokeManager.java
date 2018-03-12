package core.keyChain.managers;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import core.config.Config;
import core.keyChain.KeyStroke;
import core.keyChain.TaskActivation;
import core.userDefinedTask.UserDefinedAction;

public class AggregateKeyStrokeManager extends KeyStrokeManager {

	private List<KeyStrokeManager> managers;

	public AggregateKeyStrokeManager(Config config, KeyStrokeManager... managers) {
		super(config);
		this.managers = Arrays.asList(managers);
	}

	@Override
	public void startListening() {
		managers.forEach(KeyStrokeManager::startListening);
	}

	@Override
	public Set<UserDefinedAction> onKeyStrokePressed(KeyStroke stroke) {
		return managers.stream().map(m -> m.onKeyStrokePressed(stroke)).flatMap(Collection::stream).collect(Collectors.toSet());
	}

	@Override
	public Set<UserDefinedAction> onKeyStrokeReleased(KeyStroke stroke) {
		return managers.stream().map(m -> m.onKeyStrokeReleased(stroke)).flatMap(Collection::stream).collect(Collectors.toSet());
	}

	@Override
	public void clear() {
		managers.forEach(KeyStrokeManager::clear);
	}

	@Override
	public Set<UserDefinedAction> collision(Collection<TaskActivation> activations) {
		return flatten(managers.stream().map(m -> m.collision(activations)));
	}

	@Override
	public Set<UserDefinedAction> registerAction(UserDefinedAction action) {
		return flatten(managers.stream().map(m -> m.registerAction(action)));
	}

	@Override
	public Set<UserDefinedAction> unRegisterAction(UserDefinedAction action) {
		return flatten(managers.stream().map(m -> m.unRegisterAction(action)));
	}

	private Set<UserDefinedAction> flatten(Stream<Set<UserDefinedAction>> streamOfCollection) {
		return streamOfCollection.flatMap(Collection::stream).collect(Collectors.toSet());
	}
}
