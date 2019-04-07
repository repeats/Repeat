package org.simplenativehooks.events;

import java.time.LocalDateTime;

public class NativeKeyEvent {

	public enum Modifier {
		KEY_MODIFIER_UNKNOWN, // Unknown is equal to both left and right.
		KEY_MODIFIER_LEFT,
		KEY_MODIFIER_RIGHT;
	}

	private int key; // Values from java.awt.KeyEvent;
	private Modifier modifier;

	private boolean pressed; // Press or release.
	private LocalDateTime invokedTime;

	private NativeKeyEvent(int key, Modifier modifier, boolean pressed) {
		this.key = key;
		this.modifier = modifier;
		this.pressed = pressed;
		this.invokedTime = LocalDateTime.now();
	}

	public static NativeKeyEvent of(int key, Modifier modifier, boolean pressed) {
		return new NativeKeyEvent(key, modifier, pressed);
	}

	public int getKey() {
		return key;
	}

	public Modifier getModifier() {
		return modifier;
	}

	public boolean isPressed() {
		return pressed;
	}

	public LocalDateTime getInvokedTime() {
		return invokedTime;
	}
}
