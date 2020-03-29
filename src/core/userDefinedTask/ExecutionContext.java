package core.userDefinedTask;

import core.controller.Core;
import core.keyChain.TaskActivation;

public class ExecutionContext {
	private Core controller;
	private TaskActivation activation;

	public TaskActivation getActivation() {
		return activation;
	}

	public Core getController() {
		return controller;
	}

	public static class Builder {
		private Core controller;
		private TaskActivation activation;

		public static Builder of() {
			return new Builder();
		}

		public Builder setController(Core controller) {
			this.controller = controller;
			return this;
		}

		public Builder setActivation(TaskActivation activation) {
			this.activation = activation;
			return this;
		}

		public ExecutionContext build() {
			ExecutionContext result = new ExecutionContext();
			result.controller = controller;
			result.activation = activation;
			return result;
		}
	}
}
