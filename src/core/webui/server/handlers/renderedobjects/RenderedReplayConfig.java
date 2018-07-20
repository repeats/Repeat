package core.webui.server.handlers.renderedobjects;

import core.recorder.ReplayConfig;

public class RenderedReplayConfig {
	private long count;
	private long delay;
	private float speedup;

	public static RenderedReplayConfig fromReplayConfig(ReplayConfig replayConfig) {
		RenderedReplayConfig output = new RenderedReplayConfig();
		output.count = replayConfig.getCount();
		output.delay = replayConfig.getDelay();
		output.speedup = replayConfig.getSpeedup();
		return output;
	}

	public long getCount() {
		return count;
	}
	public void setCount(long count) {
		this.count = count;
	}
	public long getDelay() {
		return delay;
	}
	public void setDelay(long delay) {
		this.delay = delay;
	}
	public float getSpeedup() {
		return speedup;
	}
	public void setSpeedup(float speedup) {
		this.speedup = speedup;
	}
}
