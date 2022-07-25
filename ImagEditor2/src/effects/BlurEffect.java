package effects;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.JButton;
import javax.swing.JDialog;

import le.gui.components.LSlider;
import main.Main;

public class BlurEffect extends Effect{
	int radius = 3;
	@Override
	public BufferedImage getImage(BufferedImage bf) {
//		BufferedImage bufferedImage = new BufferedImage(bf.getWidth(), bf.getHeight(), BufferedImage.TYPE_INT_ARGB);
		for (int i = 0; i < bf.getWidth(); i++) {
			for (int j = 0; j < bf.getHeight(); j++) {
				bf.setRGB(i, j, getAverageColor(bf, i, j, radius).getRGB());
			}
		}
		return bf;
	}
	@Override
	public void edit() {
		JDialog editDialog = new JDialog(Main.f);
		editDialog.getContentPane().setBackground(Main.theme.getBackgroundColor());
		editDialog.setLayout(new GridLayout(2, 1));
		LSlider radiusSlider = new LSlider("Radius:", 1, 9, radius);
		Main.theme.affect(radiusSlider);
		editDialog.add(radiusSlider);
		JButton apply = new JButton("Apply");
		Main.theme.affect(apply);
		apply.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				radius = radiusSlider.getValue();
				editDialog.dispose();
			}
		});
		editDialog.add(apply);
		editDialog.pack();
		editDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		editDialog.setVisible(true);
	}
	public static Color getAverageColor(BufferedImage bf, int x, int y, int radius) {
		Color[] colors = new Color[radius * radius];
		int halfRad = radius/2;
		for (int i = 0; i < radius; i++) {
			for (int j = 0; j < radius; j++) {
				colors[i*3 + j] = getColor(bf, x - halfRad + j, y - halfRad + i);
			}
		}
		return getAverageColor(colors);
	}
	public static Color getColor(BufferedImage bf, int x, int y) {
		if (x < 0 || y < 0 || x >= bf.getWidth() || y >= bf.getHeight()) {
			return null;
		}
		return new Color(bf.getRGB(x, y));
	}
	public static Color getAverageColor(Color[] colors) {
		int count = 0;
		int redCount = 0;
		int greenCount = 0;
		int blueCount = 0;
		int alphaCount = 0;
		for (int i = 0; i < colors.length; i++) {
			if (colors[i] != null) {
				count++;
				redCount += colors[i].getRed();
				greenCount += colors[i].getGreen();
				blueCount += colors[i].getBlue();
				alphaCount += colors[i].getAlpha();
			}
		}
		if (count == 0) {
			return Color.WHITE;
		}
		return new Color(redCount/count, greenCount/count, blueCount/count, alphaCount/count);
	}
	public BlurEffect(String[] data) {
		this.radius = Integer.parseInt(data[0]);
	}
	public BlurEffect() {
		// TODO Auto-generated constructor stub
	}
	@Override
	public String encodeEffect() {
		return super.encodeEffect() + "|" + radius;
	}
}