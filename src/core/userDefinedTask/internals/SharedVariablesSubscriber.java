package core.userDefinedTask.internals;

public class SharedVariablesSubscriber {

	private SharedVariablesSubscription subscription;
	private ProcessingFunction f;

	private SharedVariablesSubscriber(SharedVariablesSubscription subscription, ProcessingFunction f) {
		this.subscription = subscription;
		this.f = f;
	}

	public static SharedVariablesSubscriber of(SharedVariablesSubscription subscription, ProcessingFunction f) {
		return new SharedVariablesSubscriber(subscription, f);
	}

	public void processEvent(SharedVariablesEvent e) {
		if (!subscription.includes(e)) {
			return;
		}

		f.process(e);
	}

	public static interface ProcessingFunction {
		public abstract void process(SharedVariablesEvent e);
	}
}
