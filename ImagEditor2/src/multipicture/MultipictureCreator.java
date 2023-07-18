package multipicture;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import javax.imageio.ImageIO;

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

	private static boolean loading = false;
	
	public static BufferedImage getMultipicture(BufferedImage source, File dir) {
		System.out.println("Start");
		loadFromDir(dir);
		System.out.println("Loaded");
		return create(source);
	}
	
	public static void loadFromDir(File dir) {
		if (loading) {
			return;
		}
		loading = true;
		int total = dir.listFiles().length;
		int count = 0;
		for(File f:dir.listFiles()) {
			try {
				load(f);
			} catch (Exception e) {}
			System.out.println("Load " + ++count + " of " + total);
		}
		loading = false;
	}

	public static BufferedImage create(BufferedImage source) {
		System.out.println(source.getWidth() + ", " + source.getHeight());
		BufferedImage ret = new BufferedImage(width * source.getWidth(), height * source.getHeight(),
				BufferedImage.TYPE_INT_RGB);
		Graphics2D g = ret.createGraphics();
		for (int i = 0; i < source.getWidth(); i++) {
			for (int j = 0; j < source.getHeight(); j++) {
				Color c = new Color(source.getRGB(i, j));
				g.drawImage(findClosest(c), i * width, j * height, null);
			}
		}
		return ret;
	}
	
	public static BufferedImage findClosest(Color c) {
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
	
	public static double getDistance(Color c1, Color c2) {
		return Math.sqrt(Math.pow(c1.getRed() - c2.getRed(), 2) + Math.pow(c1.getGreen() - c2.getGreen(), 2) + 
				Math.pow(c1.getBlue() - c2.getBlue(), 2));
	}
	
	public static void load(File imageFile) throws IOException {
		BufferedImage bf = PictureUtilities.getScaledImage(ImageIO.read(imageFile), width, height);
		loadeds.add(new Loaded(bf, getAverageColor(bf)));
	}
	public static Color getAverageColor(BufferedImage bf) {
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

	public static LinkedList<Loaded> getLoadeds() {
		return loadeds;
	}

	public static void setLoadeds(LinkedList<Loaded> loadeds) {
		MultipictureCreator.loadeds = loadeds;
	}

	public static int getWidth() {
		return width;
	}

	public static void setWidth(int width) {
		MultipictureCreator.width = width;
	}

	public static int getHeight() {
		return height;
	}

	public static void setHeight(int height) {
		MultipictureCreator.height = height;
	}

	public static double getDivFactor() {
		return divFactor;
	}

	public static void setDivFactor(double divFactor) {
		MultipictureCreator.divFactor = divFactor;
	}

	public static boolean isLoading() {
		return loading;
	}
}
