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

import drawables.shapes.Picture;
import le.gui.components.LSlider;
import main.Main;

public class BlackNWhiteEffect extends Effect{
	int redStrength;
	int greenStrength;
	int blueStrength;
	@Override
	public void affectImage(BufferedImage bf) {
		int width = bf.getWidth();
		int height = bf.getHeight();
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				bf.setRGB(i, j, getColor(bf.getRGB(i, j)));
			}
		}
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
		JDialog editBlackNwhiteDialog = new JDialog(Main.f);
		editBlackNwhiteDialog.getContentPane().setBackground(Main.theme.getBackgroundColor());
		editBlackNwhiteDialog.setTitle("Edit Black & White");
		editBlackNwhiteDialog.setLayout(new BorderLayout());
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
		editBlackNwhiteDialog.add(slidersPanel);
		JButton apply = new JButton("Apply");
		Main.theme.affect(apply);
		apply.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				redStrength = red.getValue();
				greenStrength = green.getValue();
				blueStrength = blue.getValue();
				editBlackNwhiteDialog.dispose();
				parent.invalidate();
				Main.getBoard().repaint();
			}
		});
		editBlackNwhiteDialog.add(apply, BorderLayout.SOUTH);
		editBlackNwhiteDialog.pack();
		editBlackNwhiteDialog.setVisible(true);
	}
	public BlackNWhiteEffect() {
		this.redStrength = 100;
		this.greenStrength = 100;
		this.blueStrength = 100;
	}
	public BlackNWhiteEffect(String[] data) {
		this.redStrength = Integer.parseInt(data[0]);
		this.greenStrength = Integer.parseInt(data[1]);
		this.blueStrength = Integer.parseInt(data[2]);
	}
	@Override
	public String encodeEffect() {
		return super.encodeEffect() + "_" + redStrength + "_" + greenStrength + "_" + blueStrength;
	}
}
