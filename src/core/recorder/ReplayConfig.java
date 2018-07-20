package core.recorder;

public class ReplayConfig {
	private long count;
	private long delay;
	private float speedup;

	private ReplayConfig() {}

	public static ReplayConfig of() {
		return of(1, 0, 1);
	}

	public static ReplayConfig of(long count, long delay, float speedup) {
		ReplayConfig output = new ReplayConfig();
		output.count = count;
		output.delay = delay;
		output.speedup = speedup;
		return output;
	}

	public long getCount() {
		return count;
	}
	public long getDelay() {
		return delay;
	}
	public float getSpeedup() {
		return speedup;
	}
}
