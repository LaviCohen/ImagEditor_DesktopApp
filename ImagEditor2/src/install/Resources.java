package install;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import le.utils.PictureUtilities;

/**
 * The resources class holds all the resources used in the program.
 * It is loading them once, to prevent disk request every time  they are being used.
 * */
public class Resources {
	public static ImageIcon editIcon;
	public static ImageIcon removeIcon;
	public static ImageIcon up_layerIcon;
	public static ImageIcon down_layerIcon;
	public static ImageIcon showIcon;
	public static ImageIcon hideIcon;
	public static ImageIcon pickerIcon;
	public static ImageIcon brushIcon;
	public static ImageIcon eraserIcon;
	public static ImageIcon textIcon;
	public static ImageIcon rectIcon;
	public static ImageIcon groupIcon;
	public static ImageIcon ungroupIcon;
	public static ImageIcon logo;
	public static ImageIcon maleShadow;
	public static ImageIcon femaleShadow;
	public static ImageIcon noneShadow;
	public static ImageIcon loading;
	public static int iconsWidth = 50;
	public static int iconsHeight = 50;
	public static BufferedImage defaultImage;
	public static void init(){
		System.out.println("loading images");
		try {
			editIcon = getIcon("/images/edit.jpg", 50, 50);
			removeIcon = getIcon("/images/remove.jpg", 50, 50);
			up_layerIcon = getIcon("/images/up-layer.png", 50, 50);
			down_layerIcon = getIcon("/images/down-layer.png", 50, 50);
			showIcon = getIcon("/images/show.png", 70, 40);
			hideIcon = getIcon("/images/hide.png", 70, 40);
			pickerIcon = getIcon("/images/picker.png", 32, 32);
			brushIcon = getIcon("/images/brush.png", 32, 32);
			eraserIcon = getIcon("/images/eraser.png", 32, 32);
			textIcon = getIcon("/images/text.png", 32, 32);
			rectIcon = getIcon("/images/rect_tool.png", 32, 32);
			groupIcon = getIcon("/images/group.png", 32, 32);
			ungroupIcon = getIcon("/images/ungroup.png", 32, 32);
			maleShadow = getIcon("/images/maleShadow.png", 150, 150);
			femaleShadow = getIcon("/images/femaleShadow.png", 150, 150);
			noneShadow = getIcon("/images/noneShadow.png", 150, 150);
			
			
			logo = new ImageIcon(ImageIO.read(
					Resources.class.getResourceAsStream("/images/logo.png")));
			
			loading = new ImageIcon(Resources.class.getResource("/images/loading.gif"));
			
			defaultImage = ImageIO.read(Resources.class.getResource("/images/default.jpg"));
			
		} catch (IOException e) {
			System.out.println("Error in loading images");
			e.printStackTrace();
		}
		System.out.println("All images loaded successfully");
	}
	public static ImageIcon getIcon(String path, int w, int h) throws IOException {
		return getIcon(ImageIO.read(
				Resources.class.getResourceAsStream(path)), w, h);
	}
	public static ImageIcon getIcon(Image image, int w, int h) {
		if (!Preferences.darkMode) {
			return new ImageIcon(PictureUtilities.getScaledImage(image, w, h));
		} else {
			return new ImageIcon(swapColors(PictureUtilities.getScaledImage(image, w, h)));
		}
	}
	public static BufferedImage swapColors(BufferedImage bufferedImage) {
		BufferedImage ret = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_INT_ARGB_PRE);
		for (int i = 0; i < bufferedImage.getWidth(); i++) {
			for (int j = 0; j < bufferedImage.getHeight(); j++) {
				ret.setRGB(i, j, swapRGB(bufferedImage.getRGB(i, j)));
			}
		}
		return ret;
	}
	public static int swapRGB(int rgb) {
		Color c = new Color(rgb, true);
		return new Color(255 - c.getRed(), 255 - c.getRed(), 255 - c.getRed(), c.getAlpha())
				.getRGB();
	}
}
