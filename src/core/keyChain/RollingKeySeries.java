package core.keyChain;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * A chronologically ordered key series that have an upper limit of number of
 * key strokes and will remove the last one.
 */
public class RollingKeySeries extends KeySeries {

	private static final int DEFAULT_LIMIT = 100;

	private int limit;
	private LinkedList<KeyStroke> keys; // This is an alias for the underlying keys.

	public RollingKeySeries() {
		this(Arrays.asList(), DEFAULT_LIMIT);
	}

	public RollingKeySeries(List<KeyStroke> keys) {
		this(keys, DEFAULT_LIMIT);
	}

	public RollingKeySeries(List<KeyStroke> keys, int limit) {
		super();
		super.keys = new LinkedList<>();
		this.keys = (LinkedList<KeyStroke>) super.keys;

		this.limit = limit;

		for (KeyStroke key : keys) {
			addKeyStroke(key);
		}
	}

	/*
	 * Add all key strokes from another key chain.
	 */
	@Override
	public void addFrom(KeySeries other) {
		for (KeyStroke key : other.keys) {
			addKeyStroke(key);
		}
	}

	/**
	 * Add a single stroke to the key chain.
	 * @param stroke stroke to add.
	 */
	@Override
	public void addKeyStroke(KeyStroke stroke) {
		keys.add(stroke);
		if (keys.size() > limit) {
			keys.removeFirst();
		}
	}

	/**
	 * This method is only valid for input of class {@link KeySequence}.
	 * Checks whether the pressing key strokes in this object in the specified order
	 * will trigger the other {@link KeySequence}.
	 */
	@Override
	public boolean collideWith(KeySeries other) {
		if (!(other instanceof KeySequence)) {
			throw new IllegalStateException("This method is not supported for this class " + other.getClass() + ".");
		}

		List<KeyStroke> otherKeyStrokes = other.getKeyStrokes();
		if (otherKeyStrokes.size() > keys.size()) {
			return false;
		}

		ListIterator<KeyStroke> otherIterator = otherKeyStrokes.listIterator(otherKeyStrokes.size());
		ListIterator<KeyStroke> iterator = keys.listIterator(keys.size());
		for (int i = 0; i < otherKeyStrokes.size(); i++) {
			KeyStroke otherKeyStroke = otherIterator.previous();
			KeyStroke keyStroke = iterator.previous();

			if (!otherKeyStroke.equals(keyStroke)) {
				return false;
			}
		}
		return true;
	}


}
