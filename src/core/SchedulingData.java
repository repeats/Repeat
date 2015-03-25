package core;

public class SchedulingData<T> {
	private long time;
	private T data;
	
	public SchedulingData(long time, T data) {
		this.time = time;
		this.data = data;
	}
	
	public long getTime() {
		return time;
	}

	public T getData() {
		return data;
	}
}
