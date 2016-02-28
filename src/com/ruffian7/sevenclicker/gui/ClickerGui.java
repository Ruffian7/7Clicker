package com.ruffian7.sevenclicker.gui;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

import org.pushingpixels.trident.Timeline;

import com.apple.eawt.Application;
import com.ruffian7.sevenclicker.AutoClicker;

public class ClickerGui {

	private final int WINDOW_WIDTH = 150;
	private final int WINDOW_HEIGHT = 125;
	private final int DROPDOWN_HEIGHT = 100;

	private final Color LIGHT_GRAY = new Color(60, 70, 73);
	private final Color DARK_GRAY = new Color(45, 47, 49);
	private final Color GREEN = new Color(35, 168, 105);

	public JFrame frame = new JFrame("7Clicker");

	public JPanel mainPane = new JPanel(null);
	public JPanel titleBar = new JPanel(null);
	public JPanel dropdown = new JPanel(null);

	public JLabel titleText = new JLabel("7Clicker");
	public JLabel cpsRange = new JLabel("CPS Range");
	public JLabel cpsNumber = new JLabel("00");
	public JLabel dropdownArrow = new JLabel(
			new ImageIcon(AutoClicker.class.getClassLoader().getResource("assets/arrow_down.png")));
	public JLabel powerButton = new JLabel(
			new ImageIcon(AutoClicker.class.getClassLoader().getResource("assets/power_button.png")));
	public JLabel toggleKeyText = new JLabel("Toggle Button");

	public JTextField minCPSField = new JTextField("8", 2);
	public JTextField maxCPSField = new JTextField("12", 2);
	public JTextField toggleKeyField = new JTextField("Mouse 3");

	public JCheckBox overlayBox = new JCheckBox("Overlay", true);
	public JCheckBox rightClickBox = new JCheckBox("Right Click", false);

	public RangeSlider slider = new RangeSlider(mainPane, 10, 130);

	public boolean focused = false;

	public ClickerGui() {
		setupFrame();
		setupMainPane();
		setupTitleBar();
		setupDropdown();
		setupSettings();
		setupMisc();
	}

	private void setupFrame() {
		frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		frame.setLocation(50, 50);
		frame.setLayout(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setUndecorated(true);
		frame.setBackground(new Color(0, 0, 0, 0));
		frame.setAlwaysOnTop(true);
		frame.setResizable(false);

		if (System.getProperty("os.name").toLowerCase().contains("mac")) {
			Application.getApplication().setDockIconImage(
					new ImageIcon(AutoClicker.class.getClassLoader().getResource("assets/7Clicker.png")).getImage());
		} else {
			frame.setIconImage(
					new ImageIcon(AutoClicker.class.getClassLoader().getResource("assets/7Clicker.png")).getImage());
		}

		frame.addWindowFocusListener(new WindowAdapter() {
			@Override
			public void windowGainedFocus(WindowEvent e) {
				frame.requestFocusInWindow();
				focused = true;
			}

			@Override
			public void windowLostFocus(WindowEvent e) {
				frame.requestFocusInWindow();
				focused = false;
			}
		});

		Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
			public void eventDispatched(AWTEvent event) {
				if (event.getID() == MouseEvent.MOUSE_CLICKED) {
					if (!(((MouseEvent) event).getSource() instanceof JTextField)) {
						KeyboardFocusManager.getCurrentKeyboardFocusManager().clearGlobalFocusOwner();
					}
				}
			}
		}, AWTEvent.MOUSE_EVENT_MASK);
	}

	private void setupMainPane() {
		mainPane.setBounds(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT + DROPDOWN_HEIGHT);
		mainPane.setBackground(LIGHT_GRAY);
		mainPane.setBorder(BorderFactory.createMatteBorder(5, 5, 5, 5, DARK_GRAY));

		powerButton.setBounds(10, 45, 50, 50);

		powerButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				AutoClicker.toggle();
			}
		});

		mainPane.add(powerButton);
		cpsNumber.setBounds(75, 45, 75, 50);
		cpsNumber.setForeground(GREEN);
		mainPane.add(cpsNumber);
	}

	private void setupTitleBar() {
		MouseAdapter dragListener = new MouseAdapter() {
			private int pX, pY;

			@Override
			public void mousePressed(MouseEvent e) {
				pX = e.getX();
				pY = e.getY();
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				frame.setLocation(frame.getLocation().x + e.getX() - pX, frame.getLocation().y + e.getY() - pY);
			}
		};

		titleBar.setBounds(0, 0, WINDOW_WIDTH, 30);
		titleBar.setBackground(DARK_GRAY);
		titleBar.addMouseListener(dragListener);
		titleBar.addMouseMotionListener(dragListener);

		titleText.setBounds(0, 0, WINDOW_WIDTH, 30);
		titleText.setHorizontalAlignment(SwingConstants.CENTER);
		titleText.setForeground(Color.WHITE);
		titleBar.add(titleText);
	}

	private void setupDropdown() {
		dropdown.setBounds(0, WINDOW_HEIGHT - 15, WINDOW_WIDTH, 15);
		dropdown.setBackground(DARK_GRAY);

		dropdown.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (frame.getHeight() == WINDOW_HEIGHT) {
					dropdownArrow.setIcon(
							new ImageIcon(AutoClicker.class.getClassLoader().getResource("assets/arrow_up.png")));
					Timeline dropdownTimeline = new Timeline(dropdown);
					dropdownTimeline.addPropertyToInterpolate("location", dropdown.getLocation(),
							new Point(0, dropdown.getY() + DROPDOWN_HEIGHT));
					dropdownTimeline.setDuration(300);
					dropdownTimeline.play();

					Timeline frameTimeline = new Timeline(frame);
					frameTimeline.addPropertyToInterpolate("size", frame.getSize(),
							new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT + DROPDOWN_HEIGHT));
					frameTimeline.setDuration(300);
					frameTimeline.play();

					KeyboardFocusManager.getCurrentKeyboardFocusManager().clearGlobalFocusOwner();
				} else if (frame.getHeight() == WINDOW_HEIGHT + DROPDOWN_HEIGHT) {
					dropdownArrow.setIcon(
							new ImageIcon(AutoClicker.class.getClassLoader().getResource("assets/arrow_down.png")));

					Timeline dropdownTimeline = new Timeline(dropdown);
					dropdownTimeline.addPropertyToInterpolate("location", dropdown.getLocation(),
							new Point(0, WINDOW_HEIGHT - 15));
					dropdownTimeline.setDuration(300);
					dropdownTimeline.play();

					Timeline frameTimeline = new Timeline(frame);
					frameTimeline.addPropertyToInterpolate("size", frame.getSize(),
							new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
					frameTimeline.setInitialDelay(50);
					frameTimeline.setDuration(300);
					frameTimeline.play();

					KeyboardFocusManager.getCurrentKeyboardFocusManager().clearGlobalFocusOwner();
				}
			}
		});

		dropdownArrow.setBounds(69, 2, 13, 10);
		dropdown.add(dropdownArrow);
	}

	private void setupSettings() {
		cpsRange.setBounds(0, 110, WINDOW_WIDTH, 13);
		cpsRange.setHorizontalAlignment(SwingConstants.CENTER);
		cpsRange.setForeground(Color.WHITE);
		mainPane.add(cpsRange);

		minCPSField.setBounds(10, 140, 20, 20);
		minCPSField.setHorizontalAlignment(SwingConstants.CENTER);
		minCPSField.setBackground(DARK_GRAY);
		minCPSField.setForeground(Color.WHITE);
		minCPSField.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

		minCPSField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				textFieldSetCPS(true);
			}
		});

		minCPSField.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				textFieldSetCPS(true);
			}
		});

		mainPane.add(minCPSField);

		maxCPSField.setBounds(WINDOW_WIDTH - 30, 140, 20, 20);
		maxCPSField.setHorizontalAlignment(SwingConstants.CENTER);
		maxCPSField.setBackground(DARK_GRAY);
		maxCPSField.setForeground(Color.WHITE);
		maxCPSField.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

		maxCPSField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				textFieldSetCPS(false);
			}
		});

		maxCPSField.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				textFieldSetCPS(false);
			}
		});

		mainPane.add(maxCPSField);

		overlayBox.setBounds(5, 163, 67, 16);
		overlayBox.setBackground(LIGHT_GRAY);
		overlayBox.setForeground(Color.WHITE);
		overlayBox.setIcon(
				new ImageIcon(AutoClicker.class.getClassLoader().getResource("assets/checkbox_unchecked.png")));
		overlayBox.setSelectedIcon(
				new ImageIcon(AutoClicker.class.getClassLoader().getResource("assets/checkbox_checked.png")));

		overlayBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				frame.setAlwaysOnTop(overlayBox.isSelected());
			}
		});

		mainPane.add(overlayBox);

		rightClickBox.setBounds(66, 163, 80, 16);
		rightClickBox.setBackground(LIGHT_GRAY);
		rightClickBox.setForeground(Color.WHITE);
		rightClickBox.setIcon(
				new ImageIcon(AutoClicker.class.getClassLoader().getResource("assets/checkbox_unchecked.png")));
		rightClickBox.setSelectedIcon(
				new ImageIcon(AutoClicker.class.getClassLoader().getResource("assets/checkbox_checked.png")));

		rightClickBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AutoClicker.button = (rightClickBox.isSelected()) ? 2 : 1;
			}
		});

		mainPane.add(rightClickBox);

		toggleKeyText.setBounds(11, 180, 66, 25);
		toggleKeyText.setForeground(Color.WHITE);
		mainPane.add(toggleKeyText);

		toggleKeyField.setBounds(WINDOW_WIDTH - 70, 182, 60, 20);
		toggleKeyField.setHorizontalAlignment(SwingConstants.CENTER);
		toggleKeyField.setBackground(DARK_GRAY);
		toggleKeyField.setForeground(Color.WHITE);
		toggleKeyField.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

		((AbstractDocument) toggleKeyField.getDocument()).setDocumentFilter(new DocumentFilter() {
			@Override
			public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attributes)
					throws BadLocationException {
				if (offset == -1 && length == -1) {
					super.replace(fb, 0, toggleKeyField.getText().length(), text, attributes);
				}
			}

			@Override
			public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
				// NO-OP
			}

			@Override
			public void insertString(FilterBypass fb, int offset, String string, AttributeSet attributes)
					throws BadLocationException {
				// NO-OP
			}
		});

		toggleKeyField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				try {
					if (!KeyEvent.getKeyModifiersText(e.getModifiers()).contains(KeyEvent.getKeyText(e.getKeyCode()))
							&& e.getKeyCode() != KeyEvent.VK_CAPS_LOCK) {
						AutoClicker.toggleKey[0] = KeyEvent.getKeyText(e.getKeyCode());
						AutoClicker.toggleKey[1] = KeyEvent.getKeyModifiersText(e.getModifiers());
						AutoClicker.toggleMouseButton = -1;
						((AbstractDocument) toggleKeyField.getDocument()).replace(-1, -1,
								getKeyString(e.getKeyCode(), e.getModifiers()), null);
					}
				} catch (BadLocationException ex) {
					ex.printStackTrace();
				}
			}
		});

		toggleKeyField.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				try {
					if (e.getButton() == 2 || e.getButton() > 3) {
						AutoClicker.toggleMouseButton = (e.getButton() == 2) ? 3 : e.getButton();
						AutoClicker.toggleKey[0] = "";
						AutoClicker.toggleKey[1] = "";
						((AbstractDocument) toggleKeyField.getDocument()).replace(-1, -1,
								"Mouse " + ((e.getButton() == 2) ? 3 : e.getButton()), null);
					}
				} catch (BadLocationException ex) {
					ex.printStackTrace();
				}
			}
		});

		mainPane.add(toggleKeyField);
	}

	private void setupMisc() {
		try {
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			InputStream fontFile = AutoClicker.class.getClassLoader().getResourceAsStream("assets/BebasNeue.otf");
			Font font = Font.createFont(Font.TRUETYPE_FONT, fontFile);
			ge.registerFont(font);
			fontFile.close();

			titleText.setFont(font.deriveFont(Font.PLAIN, 25));
			cpsNumber.setFont(font.deriveFont(Font.PLAIN, 69));
			cpsRange.setFont(font.deriveFont(Font.PLAIN, 18));
			overlayBox.setFont(font.deriveFont(Font.PLAIN, 14));
			rightClickBox.setFont(font.deriveFont(Font.PLAIN, 14));
			toggleKeyText.setFont(font.deriveFont(Font.PLAIN, 14));
			minCPSField.setFont(new Font("arial", Font.PLAIN, 12));
			maxCPSField.setFont(new Font("arial", Font.PLAIN, 12));
			toggleKeyField.setFont(new Font("arial", Font.PLAIN, 12));
		} catch (IOException | FontFormatException e) {
			e.printStackTrace();
		}

		frame.add(titleBar);
		frame.add(dropdown);
		frame.add(mainPane);
		frame.setVisible(true);
	}

	private void textFieldSetCPS(boolean isMin) {
		JTextField textField = isMin ? minCPSField : maxCPSField;

		if (textField.getText().matches("^\\d+$")
				&& (isMin && Integer.parseInt(textField.getText()) >= 1
						&& Integer.parseInt(textField.getText()) <= AutoClicker.maxCPS)
				|| (!isMin && Integer.parseInt(textField.getText()) >= AutoClicker.minCPS
						&& Integer.parseInt(textField.getText()) <= 99)) {
			int cpsFieldVal = Integer.parseInt(textField.getText());

			if ((isMin && slider.sliderVal1 <= slider.sliderVal2)
					|| (!isMin && slider.sliderVal1 > slider.sliderVal2)) {
				slider.sliderVal1 = (cpsFieldVal > 20) ? 19 : cpsFieldVal - 1;
				slider.sliderThumb1.x = (slider.sliderVal1 / 20.0f) * 130;
			} else {
				slider.sliderVal2 = (cpsFieldVal > 20) ? 19 : cpsFieldVal - 1;
				slider.sliderThumb2.x = (slider.sliderVal2 / 20.0f) * 130;
			}

			slider.sliderRange.x = Math.min(slider.sliderThumb1.x, slider.sliderThumb2.x) + 5;
			slider.sliderRange.width = Math.max(slider.sliderThumb1.x, slider.sliderThumb2.x)
					- Math.min(slider.sliderThumb1.x, slider.sliderThumb2.x);
			textField.setText(textField.getText().replaceFirst("^0*", ""));
			KeyboardFocusManager.getCurrentKeyboardFocusManager().clearGlobalFocusOwner();

			if (isMin) {
				AutoClicker.minCPS = cpsFieldVal;
			} else {
				AutoClicker.maxCPS = cpsFieldVal;
			}

			slider.repaint();
		} else {
			textField.setText(String.valueOf(isMin ? AutoClicker.minCPS : AutoClicker.maxCPS));
		}
	}

	private String getKeyString(int keyCode, int modifiers) {
		String modifiersString = KeyEvent.getKeyModifiersText(modifiers).replace("+", "");
		String keyString;

		if (keyCode == 0) {
			keyString = "Invalid Key";
			modifiersString = "";
		} else if (keyCode == 32) {
			keyString = "Space";
		} else {
			keyString = KeyEvent.getKeyText(keyCode);
		}

		return modifiersString + keyString;
	}
}