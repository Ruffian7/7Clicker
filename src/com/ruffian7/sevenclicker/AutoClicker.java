package com.ruffian7.sevenclikcer;

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

import com.ruffian7.sevenclikcer.gui.ClickerGui;
import com.ruffian7.sevenclikcer.listener.KeyListener;
import com.ruffian7.sevenclikcer.listener.MouseListener;

public class AutoClikcer {

	public static Robot robot;
	public static Point mousePos;
	public static ClikcerGui gui = new ClikcerGui();

	public static boolean toggled = false;
	public static boolean activated = false;
	public static boolean skipNext = false;
	public static boolean blockHit = false;

	private static int delay = -1;
	public static long lastTime = 0;
	public static int minCPS = 7;
	public static int maxCPS = 10;
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
		if (AutoClikcer.toggled) {
			AutoClikcer.toggled = false;
			AutoClikcer.gui.powerButton
					.setIcon(new ImageIcon(AutoClikcer.class.getClassLoader().getResource("assets/power_button.png")));
		} else {
			AutoClikcer.toggled = true;
			AutoClikcer.gui.powerButton.setIcon(
					new ImageIcon(AutoClikcer.class.getClassLoader().getResource("assets/power_button_on.png")));
		}

		AutoClikcer.activated = false;
		AutoClikcer.skipNext = false;
		AutoClikcer.blockHit = false;
	}
}
