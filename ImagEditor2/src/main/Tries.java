package main;

import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import drawables.shapes.Picture;

public class Tries {
	public static void main(String[] args) throws IOException {
		BufferedImage bf = ImageIO.read(new File("C:/Users/yonicohen/Desktop/img.jpg"));
		BufferedImage bf2 = Picture.decodeSourceImage(Picture.encodeSourceImge(bf));
		JFrame f = new JFrame();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.add(new JLabel(new ImageIcon(bf)), BorderLayout.WEST);
		f.add(new JLabel(new ImageIcon(bf2)), BorderLayout.EAST);
		f.pack();
		f.setVisible(true);
		
	}
}
