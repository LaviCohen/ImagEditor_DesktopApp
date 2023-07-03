package install;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import drawables.shapes.Picture;

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
			editIcon = getIcon(ImageIO.read(
					Resources.class.getResourceAsStream("/images/edit.jpg")), 50, 50);
			removeIcon = getIcon(ImageIO.read(
					Resources.class.getResourceAsStream("/images/remove.jpg")), 50, 50);
			up_layerIcon = getIcon(ImageIO.read(
					Resources.class.getResourceAsStream("/images/up-layer.png")), 50, 50);
			down_layerIcon = getIcon(ImageIO.read(
					Resources.class.getResourceAsStream("/images/down-layer.png")), 50, 50);
			showIcon = getIcon(ImageIO.read(
					Resources.class.getResourceAsStream("/images/show.png")), 70, 40);
			hideIcon = getIcon(ImageIO.read(
					Resources.class.getResourceAsStream("/images/hide.png")), 70, 40);
			pickerIcon = getIcon(ImageIO.read(
					Resources.class.getResourceAsStream("/images/picker.png")), 32, 32);
			brushIcon = getIcon(ImageIO.read(
					Resources.class.getResourceAsStream("/images/brush.png")), 32, 32);
			maleShadow = getIcon(ImageIO.read(
					Resources.class.getResourceAsStream("/images/maleShadow.png")), 150, 150);
			femaleShadow = getIcon(ImageIO.read(
					Resources.class.getResourceAsStream("/images/femaleShadow.png")), 150, 150);
			noneShadow = getIcon(ImageIO.read(
					Resources.class.getResourceAsStream("/images/noneShadow.png")), 150, 150);
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
	public static ImageIcon getIcon(Image image, int w, int h) {
		return new ImageIcon(Picture.getScaledImage(image, w, h));
	}
}
