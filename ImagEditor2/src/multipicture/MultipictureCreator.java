package multipicture;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import le.utils.PictureUtilities;

public class MultipictureCreator {


	public static class Loaded{
		public BufferedImage image;
		public Color avg;
		public int times;
		public Loaded(BufferedImage image, Color avg) {
			super();
			this.image = image;
			this.avg = avg;
			times = 0;
		}
	}
	private static LinkedList<Loaded> loadeds = new LinkedList<>();
	
	private static int width = 50;
	private static int height = 50;

	private static double divFactor = 0.3;
	
	public static BufferedImage getMultipicture(BufferedImage source, File dir) {
		System.out.println("Start");
		int i = 0;
		for(File f:dir.listFiles()) {
			try {
				load(f);
			} catch (Exception e) {}
			System.out.println("Load " + ++i);
		}
		System.out.println("Loaded");
		return create(source);
	}
	
	private static BufferedImage create(BufferedImage source) {
		System.out.println(source.getWidth() + ", " + source.getHeight());
		BufferedImage ret = new BufferedImage(width * source.getWidth(), height * source.getHeight(),
				BufferedImage.TYPE_INT_RGB);
		Graphics2D g = ret.createGraphics();
		int count = 0;
		for (int i = 0; i < source.getWidth(); i++) {
			for (int j = 0; j < source.getHeight(); j++) {
				Color c = new Color(source.getRGB(i, j));
				g.drawImage(findClosest(c), i * width, j * height, null);
				System.out.println("Found " + ++count);
			}
		}
		return ret;
	}
	
	private static BufferedImage findClosest(Color c) {
		double minDistance = 10000;
		Loaded closest = null;
		for (Loaded loaded : loadeds) {
			double distance = getDistance(c, loaded.avg) + loaded.times * divFactor ;
			if (distance < minDistance) {
				minDistance = distance;
				closest = loaded;
			}
		}
		closest.times++;
		return closest.image;
	}
	
	private static double getDistance(Color c1, Color c2) {
		return Math.abs(c1.getRed() - c2.getRed()) + Math.abs(c1.getGreen() - c2.getGreen()) + 
				Math.abs(c1.getBlue() - c2.getBlue());
	}
	
	private static void load(File imageFile) throws IOException {
		BufferedImage bf = PictureUtilities.getScaledImage(ImageIO.read(imageFile), width, height);
		loadeds.add(new Loaded(bf, getAverageColor(bf)));
	}
	private static Color getAverageColor(BufferedImage bf) {
		int red = 0, green = 0, blue = 0;
		for (int i = 0; i < bf.getWidth(); i++) {
			for (int j = 0; j < bf.getHeight(); j++) {
				Color c = new Color(bf.getRGB(i, j));
				red += c.getRed();
				green += c.getGreen();
				blue += c.getBlue();
			}
		}
		int count = bf.getWidth() * bf.getHeight();
		return new Color(red / count, green / count, blue / count);
	}
	public static void main(String[] args) {
		File dir = new File("");
		try {
			BufferedImage source = ImageIO.read(new File(""));
			source = PictureUtilities.getScaledImage(source, 300, 140);
			BufferedImage result = getMultipicture(source, dir);
			ImageIO.write(result, "png", new File(""));
			JFrame f = new JFrame();
			f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			f.add(new JLabel(new ImageIcon(PictureUtilities.getScaledImage(result, 1500, 700))));
			f.pack();
			f.setVisible(true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
