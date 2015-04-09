package core.languageHandler.sourceGenerator;

import java.util.logging.Logger;

import utilities.Function;
import core.scheduler.AbstractScheduler;
import core.scheduler.SchedulingData;

class TaskSourceScheduler extends AbstractScheduler<String> {

	private static final Logger LOGGER = Logger.getLogger(TaskSourceScheduler.class.getName());
	
	private Function<Long, String> getSleepSource;
	
	protected TaskSourceScheduler() {
		super();
	}

	protected String getSource() {
		if (!isLegalAddTask()) {
			return null;
		}
		
		StringBuffer output = new StringBuffer();
		
		long time = 0;
		for (SchedulingData<String> t : tasks) {
			long currentTime = t.getTime();

			if (currentTime < time) {
				LOGGER.severe("Something went really bad");
				System.exit(1);
			}

			output.append(getSleepSource.apply(currentTime - time));

			time = currentTime;
			output.append(t.getData());
		}
		
		return output.toString();
	}
	
	@Override
	protected boolean isLegalAddTask() {
		if (getSleepSource == null) {
			LOGGER.severe("Unable to generate source. Function getSleepSource is null.");
			return false;
		}
		return true;
	}
	
	protected void setSleepSource(Function<Long, String> getSleepSource) {
		this.getSleepSource = getSleepSource;
	}
}
