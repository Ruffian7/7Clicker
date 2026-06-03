package com.ruffian7.sevenclicker.listener;

import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseListener;

import com.ruffian7.sevenclicker.AutoClicker;

public class MouseListener implements NativeMouseListener {

	@Override
	public void nativeMousePressed(NativeMouseEvent event) {
		if (AutoClicker.clickEngine.isInjected(event))
			return;

		if (event.getButton() == AutoClicker.toggleMouseButton && !AutoClicker.gui.focused) {
			AutoClicker.toggle();
		}

		AutoClicker.clickEngine.physicalMousePressed(event);
	}

	@Override
	public void nativeMouseReleased(NativeMouseEvent event) {
		if (AutoClicker.clickEngine.isInjected(event))
			return;

		AutoClicker.clickEngine.physicalMouseReleased(event);
	}

	@Override
	public void nativeMouseClicked(NativeMouseEvent event) {
		// NO-OP
	}
}
