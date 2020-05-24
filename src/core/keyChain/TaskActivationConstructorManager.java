package core.keyChain;

import org.simplenativehooks.events.NativeKeyEvent;
import org.simplenativehooks.listeners.AbstractGlobalKeyListener;
import org.simplenativehooks.utilities.Function;

import globalListener.GlobalListenerFactory;

public class TaskActivationConstructorManager extends core.background.AbstractBackgroundEntityManager<TaskActivationConstructor> {

	private AbstractGlobalKeyListener keyListener;

	public TaskActivationConstructorManager() {
		keyListener = GlobalListenerFactory.of().createGlobalKeyListener();
	}

	@Override
	public void start() {
		super.start();

		keyListener.setKeyReleased(new Function<NativeKeyEvent, Boolean>() {
			@Override
			public Boolean apply(NativeKeyEvent r) {
				onStroke(KeyStroke.of(r));
				return true;
			}
		});

		keyListener.startListening();
	}

	@Override
	public void stop() {
		keyListener.stopListening();
		super.stop();
	}

	private synchronized void onStroke(KeyStroke stroke) {
		for (TaskActivationConstructor constructor : entities.values()) {
			constructor.onStroke(stroke);
		}
	}

	public synchronized String addNewConstructor(TaskActivation source) {
		return addNewConstructor(source, TaskActivationConstructor.Config.of());
	}

	public synchronized String addNewConstructor(TaskActivation source, TaskActivationConstructor.Config config) {
		TaskActivationConstructor constructor = new TaskActivationConstructor(source, config);
		return add(constructor);
	}
}
