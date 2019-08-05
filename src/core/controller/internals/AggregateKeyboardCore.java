package core.controller.internals;

import java.util.Arrays;
import java.util.List;

public class AggregateKeyboardCore extends AbstractKeyboardCoreImplementation {

	private final List<AbstractKeyboardCoreImplementation> keyboards;

	private AggregateKeyboardCore(List<AbstractKeyboardCoreImplementation> keyboards) {
		this.keyboards = keyboards;
	}

	public static AggregateKeyboardCore of(List<AbstractKeyboardCoreImplementation> keyboards) {
		if (keyboards == null || keyboards.isEmpty()) {
			keyboards = Arrays.asList(NoopKeyboardCore.of());
		}
		return new AggregateKeyboardCore(keyboards);
	}

	@Override
	public void type(String... strings) {
		for (AbstractKeyboardCoreImplementation keyboard : keyboards) {
			keyboard.type(strings);
		}
	}

	@Override
	public void type(char... chars) {
		for (AbstractKeyboardCoreImplementation keyboard : keyboards) {
			keyboard.type(chars);
		}
	}

	@Override
	public void type(int... keys) throws InterruptedException {
		for (AbstractKeyboardCoreImplementation keyboard : keyboards) {
			keyboard.type(keys);
		}
	}

	@Override
	public void combination(int... keys) {
		for (AbstractKeyboardCoreImplementation keyboard : keyboards) {
			keyboard.combination(keys);
		}
	}

	@Override
	public void hold(int key, int duration) throws InterruptedException {
		for (AbstractKeyboardCoreImplementation keyboard : keyboards) {
			keyboard.hold(key, duration);
		}
	}

	@Override
	public void press(int... keys) {
		for (AbstractKeyboardCoreImplementation keyboard : keyboards) {
			keyboard.press(keys);
		}
	}

	@Override
	public void release(int... keys) {
		for (AbstractKeyboardCoreImplementation keyboard : keyboards) {
			keyboard.release(keys);
		}
	}

	@Override
	public boolean isLocked(int key) {
		return keyboards.iterator().next().isLocked(key);
	}
}
