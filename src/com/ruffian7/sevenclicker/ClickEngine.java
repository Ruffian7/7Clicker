package com.ruffian7.sevenclicker;

import java.awt.MouseInfo;
import java.awt.Point;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;

import javax.swing.SwingUtilities;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;

public class ClickEngine {
	private static final int CLICK_HOLD_MILLIS = 5;
	private static final long CLICK_HOLD_NANOS = CLICK_HOLD_MILLIS * 1_000_000L;
	private static final long IDLE_WAIT_NANOS = 1_000_000L;
	private static final long ACTIVE_WAIT_NANOS = 250_000L;

	private final AtomicBoolean enabled = new AtomicBoolean();
	private final AtomicBoolean rightClickEnabled = new AtomicBoolean();
	private final AtomicBoolean leftButtonHeld = new AtomicBoolean();
	private final AtomicBoolean rightButtonHeld = new AtomicBoolean();
	private final AtomicReference<Point> pointerPosition = new AtomicReference<>();
	private final AtomicIntegerArray injectedPressCounts = new AtomicIntegerArray(6);
	private final AtomicIntegerArray injectedReleaseCounts = new AtomicIntegerArray(6);

	private volatile boolean started;
	private volatile long nextCpsDisplayUpdateAt;

	public synchronized void start() {
		if (started)
			return;

		started = true;
		startChannel("7Clicker-left", NativeMouseEvent.BUTTON1);
		startChannel("7Clicker-right", NativeMouseEvent.BUTTON2);
	}

	public void setEnabled(boolean enabled) {
		this.enabled.set(enabled);
	}

	public void setRightClickEnabled(boolean enabled) {
		rightClickEnabled.set(enabled);
	}

	public void physicalMousePressed(NativeMouseEvent event) {
		if (isInjected(event))
			return;

		if (event.getButton() == NativeMouseEvent.BUTTON1) {
			leftButtonHeld.set(true);
		} else if (event.getButton() == NativeMouseEvent.BUTTON2) {
			rightButtonHeld.set(true);
		}

		pointerPosition.set(event.getPoint());
	}

	public void physicalMouseReleased(NativeMouseEvent event) {
		if (isInjected(event))
			return;

		if (event.getButton() == NativeMouseEvent.BUTTON1) {
			leftButtonHeld.set(false);
		} else if (event.getButton() == NativeMouseEvent.BUTTON2) {
			rightButtonHeld.set(false);
		}
	}

	public boolean isInjected(NativeMouseEvent event) {
		int button = event.getButton();
		if (button < 0 || button >= 6)
			return false;

		AtomicIntegerArray counter = getInjectedCounter(event.getID());
		if (counter == null)
			return false;

		while (true) {
			int current = counter.get(button);
			if (current == 0)
				return false;
			if (counter.compareAndSet(button, current, current - 1))
				return true;
		}
	}

	private void startChannel(String name, int button) {
		Thread thread = new Thread(() -> runChannel(button), name);
		thread.setDaemon(true);
		thread.start();
	}

	private void runChannel(int button) {
		long nextClickAt = 0;
		while (!Thread.currentThread().isInterrupted()) {
			if (!shouldClick(button)) {
				nextClickAt = 0;
				LockSupport.parkNanos(IDLE_WAIT_NANOS);
				continue;
			}

			long now = System.nanoTime();
			if (nextClickAt == 0 || now >= nextClickAt) {
				int delay = nextDelayMillis();
				nextClickAt = (nextClickAt == 0 ? now : nextClickAt) + delay * 1_000_000L;
				injectClick(button);
				updateCpsDisplay(delay);
				nextClickAt = Math.max(nextClickAt, System.nanoTime());
			} else {
				LockSupport.parkNanos(Math.min(nextClickAt - now, ACTIVE_WAIT_NANOS));
			}
		}
	}

	private void injectClick(int button) {
		Point position = pointerPosition.get();
		if (position == null)
			position = MouseInfo.getPointerInfo().getLocation();

		postMouseEvent(position, button, NativeMouseEvent.NATIVE_MOUSE_PRESSED);
		try {
			LockSupport.parkNanos(CLICK_HOLD_NANOS);
		} finally {
			postMouseEvent(position, button, NativeMouseEvent.NATIVE_MOUSE_RELEASED);
		}
	}

	private void postMouseEvent(Point position, int button, int eventId) {
		AtomicIntegerArray counter = getInjectedCounter(eventId);
		if (counter != null)
			counter.incrementAndGet(button);

		GlobalScreen.postNativeEvent(new NativeMouseEvent(eventId, 0, position.x, position.y, 1, button));
	}

	private int nextDelayMillis() {
		int minCps = Math.max(1, Math.min(AutoClicker.minCPS, AutoClicker.maxCPS));
		int maxCps = Math.max(minCps, Math.max(AutoClicker.minCPS, AutoClicker.maxCPS));
		int shortestDelay = 1000 / maxCps;
		int longestDelay = 1000 / minCps;
		return ThreadLocalRandom.current().nextInt(shortestDelay, longestDelay + 1);
	}

	private void updateCpsDisplay(int delayMillis) {
		long now = System.nanoTime();
		if (now < nextCpsDisplayUpdateAt)
			return;

		nextCpsDisplayUpdateAt = now + 200_000_000L;
		String cps = String.format("%02d", Math.round(1000.0f / delayMillis));
		SwingUtilities.invokeLater(() -> AutoClicker.gui.cpsNumber.setText(cps));
	}

	private boolean shouldClick(int button) {
		if (!enabled.get() || AutoClicker.gui.focused)
			return false;

		if (button == NativeMouseEvent.BUTTON1)
			return leftButtonHeld.get();

		return rightClickEnabled.get() && rightButtonHeld.get();
	}

	private AtomicIntegerArray getInjectedCounter(int eventId) {
		if (eventId == NativeMouseEvent.NATIVE_MOUSE_PRESSED)
			return injectedPressCounts;
		if (eventId == NativeMouseEvent.NATIVE_MOUSE_RELEASED)
			return injectedReleaseCounts;
		return null;
	}
}
