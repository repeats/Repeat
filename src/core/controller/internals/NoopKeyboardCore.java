package core.controller.internals;

public class NoopKeyboardCore extends AbstractKeyboardCoreImplementation {

	private static final NoopKeyboardCore INSTANCE = new NoopKeyboardCore();

	private NoopKeyboardCore() {}

	public static NoopKeyboardCore of() {
		return INSTANCE;
	}

	@Override
	public void type(String... strings) {}

	@Override
	public void type(char... chars) {}

	@Override
	public void type(int... keys) throws InterruptedException {}

	@Override
	public void combination(int... keys) {}

	@Override
	public void hold(int key, int duration) throws InterruptedException {}

	@Override
	public void press(int... keys) {}

	@Override
	public void release(int... keys) {}

	@Override
	public boolean isLocked(int key) {
		return false;
	}
}
