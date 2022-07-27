package effects;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import install.Resources;
import le.gui.components.LSlider;
import main.Main;
import shapes.Picture;

public class OutlineEffect extends Effect{

	int accuracy;
	
	int outlineRGB;
	int objectRGB;
	
	public OutlineEffect() {
		this.accuracy = 50;
		this.outlineRGB = Color.WHITE.getRGB();
		this.objectRGB = Color.BLACK.getRGB();
	}
	
	@Override
	public BufferedImage getImage(BufferedImage bf) {
		BufferedImage ret = new BufferedImage(bf.getWidth(), bf.getHeight(), BufferedImage.TYPE_INT_ARGB);
		for (int i = 0; i < bf.getWidth(); i++) {
			for (int j = 0; j < bf.getHeight(); j++) {
				int curRGB = bf.getRGB(i, j);
				boolean alike = true;
				if (i != 0) {
					alike = isAlike(curRGB, bf.getRGB(i - 1, j));
				}
				if (alike && j != 0) {
					alike = isAlike(curRGB, bf.getRGB(i, j - 1));
				}
				if (alike && i != bf.getWidth() - 1) {
					alike = isAlike(curRGB, bf.getRGB(i + 1, j));
				}
				if (alike && j != bf.getHeight() - 1) {
					alike = isAlike(curRGB, bf.getRGB(i, j + 1));
				}
				if (alike) {
					ret.setRGB(i, j, objectRGB);
				}else {
					ret.setRGB(i, j, outlineRGB);
				}
			}
		}
		return ret;
	}
	
	public boolean isAlike(int rgb1, int rgb2) {
		Color c1 = new Color(rgb1);
		Color c2 = new Color(rgb2);
		return accuracy * accuracy > (c1.getRed() - c2.getRed()) * (c1.getRed() - c2.getRed()) +
				(c1.getGreen() - c2.getGreen()) * (c1.getGreen() - c2.getGreen())
				+ (c1.getBlue() - c2.getBlue()) * (c1.getBlue() - c2.getBlue());
	}

	@Override
	public void edit(Picture parent) {
		JDialog editDialog = new JDialog(Main.f);
		editDialog.setTitle("Edit Putline Effect");
		editDialog.setLayout(new GridLayout(4, 1));
		LSlider accuracySlider = new LSlider("Accuracy:", 0, 100, accuracy);
		Main.theme.affect(accuracySlider);
		editDialog.add(accuracySlider);
		JPanel outlineColorPanel = new JPanel(new BorderLayout());
		outlineColorPanel.add(Main.theme.affect(new JLabel("Outline Color:")), Main.translator.getBeforeTextBorder());
		JLabel outlineColorLabel = new JLabel();
		outlineColorLabel.setOpaque(true);
		outlineColorLabel.setBackground(new Color(outlineRGB));
		outlineColorPanel.add(outlineColorLabel);
		JButton setOutlineColorButton = new JButton("Set Color");
		Main.theme.affect(setOutlineColorButton);
		setOutlineColorButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				outlineColorLabel.setBackground(JColorChooser.showDialog(editDialog, "Choose Outline Color", outlineColorLabel.getBackground()));
			}
		});
		outlineColorPanel.add(setOutlineColorButton, Main.translator.getAfterTextBorder());
		editDialog.add(outlineColorPanel);
		JPanel objectColorPanel = new JPanel(new BorderLayout());
		objectColorPanel.add(Main.theme.affect(new JLabel("Object Color:")), Main.translator.getBeforeTextBorder());
		JLabel objectColorLabel = new JLabel();
		objectColorLabel.setOpaque(true);
		objectColorLabel.setBackground(new Color(objectRGB));
		objectColorPanel.add(objectColorLabel);
		JButton setObjectColorButton = new JButton("Set Color");
		Main.theme.affect(setObjectColorButton);
		setObjectColorButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				objectColorLabel.setBackground(JColorChooser.showDialog(editDialog, "Choose Object Color", objectColorLabel.getBackground()));
			}
		});
		objectColorPanel.add(setObjectColorButton, Main.translator.getAfterTextBorder());
		editDialog.add(objectColorPanel);
		
		JButton apply = new JButton("Apply");
		Main.theme.affect(apply);
		apply.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				OutlineEffect.this.accuracy = accuracySlider.getValue();
				OutlineEffect.this.outlineRGB = outlineColorLabel.getBackground().getRGB();
				OutlineEffect.this.objectRGB = objectColorLabel.getBackground().getRGB();
				parent.lastDrawn = null;
				Main.getBoard().repaint();
				editDialog.dispose();
			}
		});
		editDialog.add(apply);
		editDialog.pack();
		editDialog.setVisible(true);
	}
	public static void maikjnkn(String[] args) {
		JFrame f = new JFrame();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Resources.init();
		f.add(new JLabel(new ImageIcon(new OutlineEffect().getImage(Resources.defaultImage))));
		f.pack();
		f.setVisible(true);
	}
}
