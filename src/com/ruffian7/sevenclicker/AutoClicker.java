package com.ruffian7.sevenclicker;

import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.swing.ImageIcon;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.ruffian7.sevenclicker.gui.ClickerGui;
import com.ruffian7.sevenclicker.listener.KeyListener;
import com.ruffian7.sevenclicker.listener.MouseListener;

public class AutoClicker {

	public static ClickEngine clickEngine = new ClickEngine();
	public static ClickerGui gui = new ClickerGui();

	public static volatile boolean toggled = false;

	public static volatile int minCPS = 8;
	public static volatile int maxCPS = 12;

	public static String[] toggleKey = { "", "" };
	public static int toggleMouseButton = 3;

	public static void main(String[] args) {
		LogManager.getLogManager().reset();
		Logger.getLogger(GlobalScreen.class.getPackage().getName()).setLevel(Level.OFF);

		try {
			GlobalScreen.registerNativeHook();
			GlobalScreen.addNativeMouseListener(new MouseListener());
			GlobalScreen.addNativeKeyListener(new KeyListener());
			clickEngine.start();
		} catch (NativeHookException e) {
			e.printStackTrace();
		}

		try {
			while (true) {
				Thread.sleep(1000);
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	public static void setRightClickEnabled(boolean enabled) {
		clickEngine.setRightClickEnabled(enabled);
	}

	public static void toggle() {
		if (AutoClicker.toggled) {
			AutoClicker.toggled = false;
			AutoClicker.gui.powerButton
					.setIcon(new ImageIcon(AutoClicker.class.getClassLoader().getResource("assets/power_button.png")));
		} else {
			AutoClicker.toggled = true;
			AutoClicker.gui.powerButton.setIcon(
					new ImageIcon(AutoClicker.class.getClassLoader().getResource("assets/power_button_on.png")));
		}

		clickEngine.setEnabled(AutoClicker.toggled);
	}
}
