package com.ruffian7.sevenclicker.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;

import com.ruffian7.sevenclicker.AutoClicker;

public class RangeSlider extends JPanel {

	private static final long serialVersionUID = 1L;
	public int sliderVal1 = 7;
	public int sliderVal2 = 11;

	Rectangle2D.Double sliderBody = new Rectangle2D.Double(0, 2.5, 130, 5);
	Ellipse2D.Double sliderThumb1 = new Ellipse2D.Double((sliderVal1 / 20.0f) * 130, 0, 10, 10);
	Ellipse2D.Double sliderThumb2 = new Ellipse2D.Double((sliderVal2 / 20.0f) * 130, 0, 10, 10);
	Rectangle2D.Double sliderRange = new Rectangle2D.Double((sliderVal1 / 20.0f) * 130 + 5, 3,
			((sliderVal2 - sliderVal1) / 20.0f) * 130, 4);

	public RangeSlider(JPanel panel, int x, int y) {
		setLayout(null);
		setBounds(x, y, 130, 10);
		setBackground(new Color(60, 70, 73));

		MouseAdapter dragListener = new MouseAdapter() {
			private boolean thumbPressed1 = false;
			private boolean thumbPressed2 = false;

			@Override
			public void mousePressed(MouseEvent e) {
				if (sliderThumb1.getBounds().contains(e.getPoint())) {
					thumbPressed1 = true;
				} else if (sliderThumb2.getBounds().contains(e.getPoint())) {
					thumbPressed2 = true;
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				thumbPressed1 = false;
				thumbPressed2 = false;
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				if (thumbPressed1) {
					sliderThumb1.x = (e.getX() - 5 < -5) ? -5 : (e.getX() - 5 > 124) ? 124 : e.getX() - 5;
					sliderRange.x = Math.min(sliderThumb1.x, sliderThumb2.x) + 5;
					sliderRange.width = Math.max(sliderThumb1.x, sliderThumb2.x)
							- Math.min(sliderThumb1.x, sliderThumb2.x);
					sliderVal1 = (int) Math.round(((sliderThumb1.x + 2) / 130) * 20);
					AutoClicker.minCPS = Math.min(sliderVal1, sliderVal2) + 1;
					AutoClicker.maxCPS = Math.max(sliderVal1, sliderVal2) + 1;
					AutoClicker.gui.minCPSField.setText(String.valueOf(AutoClicker.minCPS));
					AutoClicker.gui.maxCPSField.setText(String.valueOf(AutoClicker.maxCPS));
					repaint();
				} else if (thumbPressed2) {
					sliderThumb2.x = (e.getX() - 5 < -5) ? -5 : (e.getX() - 5 > 124) ? 124 : e.getX() - 5;
					sliderRange.x = Math.min(sliderThumb1.x, sliderThumb2.x) + 5;
					sliderRange.width = Math.max(sliderThumb1.x, sliderThumb2.x)
							- Math.min(sliderThumb1.x, sliderThumb2.x);
					sliderVal2 = (int) Math.round(((sliderThumb2.x + 2) / 130) * 20);
					AutoClicker.minCPS = Math.min(sliderVal1, sliderVal2) + 1;
					AutoClicker.maxCPS = Math.max(sliderVal1, sliderVal2) + 1;
					AutoClicker.gui.minCPSField.setText(String.valueOf(AutoClicker.minCPS));
					AutoClicker.gui.maxCPSField.setText(String.valueOf(AutoClicker.maxCPS));
					repaint();
				}
			}
		};

		addMouseListener(dragListener);
		addMouseMotionListener(dragListener);
		panel.add(this);
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		g2d.setColor(new Color(45, 47, 49));
		g2d.fill(sliderBody);

		g2d.setColor(new Color(35, 168, 105));
		g2d.fill(sliderRange);

		g2d.setColor(Color.BLACK);
		g2d.fill(sliderThumb1);

		g2d.setColor(Color.BLACK);
		g2d.fill(sliderThumb2);
	}
}
