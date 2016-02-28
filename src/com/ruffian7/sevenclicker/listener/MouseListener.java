package com.ruffian7.sevenclicker.listener;

import java.awt.event.MouseEvent;

import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseListener;

import com.ruffian7.sevenclicker.AutoClicker;

public class MouseListener implements NativeMouseListener {
	private boolean leftClick, rightClick;
	private long lastClickTime = 0;

	@Override
	public void nativeMousePressed(NativeMouseEvent event) {
		if (event.getButton() == AutoClicker.toggleMouseButton && !AutoClicker.gui.focused) {
			AutoClicker.toggle();
		}

		if (AutoClicker.toggled && !AutoClicker.skipNext) {
			if (event.getButton() == MouseEvent.BUTTON1) {
				leftClick = true;
			} else if (event.getButton() == MouseEvent.BUTTON2) {
				rightClick = true;
			}

			if (leftClick && rightClick) {
				AutoClicker.blockHit = true;
			}

			if (event.getButton() == AutoClicker.button) {
				AutoClicker.mousePos = event.getPoint();
				AutoClicker.activated = true;
				AutoClicker.lastTime = System.currentTimeMillis();
			}
		}

		if (event.getButton() == AutoClicker.button) {
			if (System.currentTimeMillis() - lastClickTime > 1000 && lastClickTime != 0) {
				lastClickTime = 0;
			}

			if (lastClickTime == 0) {
				lastClickTime = System.currentTimeMillis();
			} else if (System.currentTimeMillis() != lastClickTime) {
				int cps = Math.round(1000.0f / (System.currentTimeMillis() - lastClickTime));
				AutoClicker.gui.cpsNumber.setText((cps < 10) ? "0" + cps : String.valueOf(cps));
				lastClickTime = 0;
			}
		}
	}

	@Override
	public void nativeMouseReleased(NativeMouseEvent event) {
		if (!AutoClicker.skipNext) {
			if (event.getButton() == AutoClicker.button) {
				leftClick = false;
				AutoClicker.activated = false;
			} else if (event.getButton() == ((AutoClicker.button == 1) ? 2 : 1)) {
				rightClick = false;
				AutoClicker.blockHit = false;
			}
		} else {
			AutoClicker.skipNext = event.getButton() == AutoClicker.button && AutoClicker.blockHit;
		}
	}

	@Override
	public void nativeMouseClicked(NativeMouseEvent event) {
		// NO-OP
	}
}