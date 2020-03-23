package core.webui.server.handlers.internals.tasks;

import utilities.json.AutoJsonable;

@SuppressWarnings("unused")
public class RunTaskRequest extends AutoJsonable {
	private String id;
	private RunConfig runConfig;

	protected static RunTaskRequest of() {
		return new RunTaskRequest();
	}

	protected String getId() {
		return id;
	}

	protected RunConfig getRunConfig() {
		return runConfig;
	}

	protected static class RunConfig extends AutoJsonable {
		private String repeatCount;
		private String delayMsBetweenRepeat;

		public static RunConfig of() {
			return new RunConfig();
		}

		public static RunConfig of(String repeatCount, String delayMsBetweenRepeat) {
			RunConfig result =  new RunConfig();
			result.repeatCount = repeatCount;
			result.delayMsBetweenRepeat = delayMsBetweenRepeat;
			return result;
		}

		protected String getRepeatCount() {
			return repeatCount;
		}

		protected String getDelayMsBetweenRepeat() {
			return delayMsBetweenRepeat;
		}
	}
}
