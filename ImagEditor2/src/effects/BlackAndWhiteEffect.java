package effects;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import le.gui.components.LSlider;
import main.Main;
import shapes.Picture;

public class BlackAndWhiteEffect extends Effect{
	int redStrength = 100;
	int greenStrength = 100;
	int blueStrength = 100;
	public BufferedImage getImage(BufferedImage bf) {
		int width = bf.getWidth();
		int height = bf.getHeight();
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				bf.setRGB(i, j, getColor(bf.getRGB(i, j)));
			}
		}
		return bf;
	}
	public int getColor(int rgb) {
		Color c = new Color(rgb);
		int avg = (int)(c.getRed() * (redStrength/100.0) + c.getGreen() * (greenStrength/100.0)
				+ c.getBlue() * (blueStrength/100.0))/3;
		avg = avg > 255?255:avg;
		return new Color(avg, avg, avg, c.getAlpha()).getRGB();
	}
	@Override
	public void edit(Picture parent) {
		JDialog editBlackAwhiteDialog = new JDialog(Main.f);
		editBlackAwhiteDialog.getContentPane().setBackground(Main.theme.getBackgroundColor());
		editBlackAwhiteDialog.setTitle("Edit Black & White");
		editBlackAwhiteDialog.setLayout(new BorderLayout());
		JPanel slidersPanel = new JPanel(new GridLayout(3, 1));
		Main.theme.affect(slidersPanel);
		LSlider red = new LSlider("Red:", 0, 255, redStrength);
		Main.theme.affect(red);
		slidersPanel.add(red);
		LSlider green = new LSlider("Green:", 0, 255, greenStrength);
		Main.theme.affect(green);
		slidersPanel.add(green);
		LSlider blue = new LSlider("Blue:", 0, 255, blueStrength);
		Main.theme.affect(blue);
		slidersPanel.add(blue);
		editBlackAwhiteDialog.add(slidersPanel);
		JButton apply = new JButton("Apply");
		Main.theme.affect(apply);
		apply.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				redStrength = red.getValue();
				greenStrength = green.getValue();
				blueStrength = blue.getValue();
				editBlackAwhiteDialog.dispose();
				parent.lastDrawn = null;
				Main.getBoard().repaint();
			}
		});
		editBlackAwhiteDialog.add(apply, BorderLayout.SOUTH);
		editBlackAwhiteDialog.pack();
		editBlackAwhiteDialog.setVisible(true);
	}
	public BlackAndWhiteEffect() {
		// TODO Auto-generated constructor stub
	}
	public BlackAndWhiteEffect(String[] data) {
		this.redStrength = Integer.parseInt(data[0]);
		this.greenStrength = Integer.parseInt(data[1]);
		this.blueStrength = Integer.parseInt(data[2]);
	}
	@Override
	public String encodeEffect() {
		return super.encodeEffect() + "|" + redStrength + "|" + greenStrength + "|" + blueStrength;
	}
}
