package core.keyChain.managers;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import core.config.Config;
import core.keyChain.ActivationEvent;
import core.keyChain.TaskActivation;
import core.userDefinedTask.UserDefinedAction;

public class AggregateActivationEventManager extends ActivationEventManager {

	private List<ActivationEventManager> managers;

	public AggregateActivationEventManager(Config config, ActivationEventManager... managers) {
		super(config);
		this.managers = Arrays.asList(managers);
	}

	@Override
	public void startListening() {
		managers.forEach(ActivationEventManager::startListening);
	}

	@Override
	public Set<UserDefinedAction> onActivationEvent(ActivationEvent event) {
		return managers.stream().map(m -> m.onActivationEvent(event)).flatMap(Collection::stream).collect(Collectors.toSet());
	}

	@Override
	public void clear() {
		managers.forEach(ActivationEventManager::clear);
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
