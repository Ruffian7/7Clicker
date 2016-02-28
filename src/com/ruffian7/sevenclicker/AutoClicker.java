package com.ruffian7.sevenclicker;

import java.awt.AWTException;
import java.awt.Point;
import java.awt.Robot;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.swing.ImageIcon;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;

import com.ruffian7.sevenclicker.gui.ClickerGui;
import com.ruffian7.sevenclicker.listener.KeyListener;
import com.ruffian7.sevenclicker.listener.MouseListener;

public class AutoClicker {

	public static Robot robot;
	public static Point mousePos;
	public static ClickerGui gui = new ClickerGui();

	public static boolean toggled = false;
	public static boolean activated = false;
	public static boolean skipNext = false;
	public static boolean blockHit = false;

	private static int delay = -1;
	public static long lastTime = 0;
	public static int minCPS = 8;
	public static int maxCPS = 12;
	public static int button = 1;

	public static String[] toggleKey = { "", "" };
	public static int toggleMouseButton = 3;

	public static void main(String[] args) {
		LogManager.getLogManager().reset();
		Logger.getLogger(GlobalScreen.class.getPackage().getName()).setLevel(Level.OFF);

		try {
			robot = new Robot();
			GlobalScreen.registerNativeHook();
			GlobalScreen.addNativeMouseListener(new MouseListener());
			GlobalScreen.addNativeKeyListener(new KeyListener());
		} catch (NativeHookException | AWTException e) {
			e.printStackTrace();
		}

		try {
			while (true) {
				Thread.sleep(1);
				Random random = new Random();
				if (delay == -1)
					delay = random.nextInt((1000 / minCPS) - (1000 / maxCPS) + 1) + (1000 / maxCPS);

				if (activated && toggled && !gui.focused) {
					if (System.currentTimeMillis() - lastTime >= delay) {
						click();
						lastTime = System.currentTimeMillis();
						delay = random.nextInt((1000 / minCPS) - (1000 / maxCPS) + 1) + (1000 / maxCPS);
					}
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private static void click() {
		skipNext = true;
		robot.mousePress((button == 1) ? 16 : 4);
		robot.mouseRelease((button == 1) ? 16 : 4);

		if (blockHit) {
			robot.mousePress((button == 1) ? 4 : 16);
			robot.mouseRelease((button == 1) ? 4 : 16);
		}
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

		AutoClicker.activated = false;
		AutoClicker.skipNext = false;
		AutoClicker.blockHit = false;
	}
}