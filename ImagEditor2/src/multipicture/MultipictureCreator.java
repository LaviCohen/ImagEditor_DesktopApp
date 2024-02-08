package multipicture;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;  
import java.util.LinkedList;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import install.Preferences;
import le.gui.dialogs.LDialogs;
import le.utils.PictureUtilities;
import main.Main;

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

	private static boolean loading = false;
	
	public static void openDialog() {
		JDialog multiPictureDialog = new JDialog(Main.f, "Create Multi-Picture");
		multiPictureDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		multiPictureDialog.setLayout(new GridLayout(5, 1));
		
		JPanel dirPanel = new JPanel(new BorderLayout());
		dirPanel.add(new JLabel("Directory:"), Main.translator.getBeforeTextBorder());
		JTextField dirField = new JTextField(Main.install.getPath("Gallery"));
		dirField.setEditable(false);
		if (Main.currentProject.folder != null) {
			dirField.setText(Main.currentProject.folder);
		}
		dirPanel.add(dirField);
		multiPictureDialog.add(dirPanel);
		JButton browse = new JButton("Browse");
		browse.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = dirField.getText().equals("")?
						new JFileChooser(Main.install.getPath("Gallery")):new JFileChooser(new File(dirField.getText()));
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				fc.showOpenDialog(multiPictureDialog);
				File f = fc.getSelectedFile();
				dirField.setText(f.getAbsolutePath());
			}
		});
		dirPanel.add(browse, Main.translator.getAfterTextBorder());
		JButton load = new JButton("Load Pictures from Directory");
		load.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						File dir = new File(dirField.getText());
						int total = dir.listFiles().length;
						String path = Main.install.getPath("Data\\MultiPictures") + "\\";
						int prev = Main.install.getFile("Data\\MultiPictures").list().length;
						int count = 0;
						for(File imageFile:dir.listFiles()) {
							try {
								ImageIO.write(PictureUtilities.getScaledImage(ImageIO.read(imageFile), 
										Preferences.mpPixelSize, Preferences.mpPixelSize)
							, "png", new File(path + (count + prev + 1) + ".png"));
								count++;
								System.out.println("Load " + count + " of " + total);
							} catch (Exception e) {
								total--;
							}
						}
					}
				}).start();
			}
		});
		multiPictureDialog.add(load);
		JPanel sourcePanel = new JPanel(new BorderLayout());
		sourcePanel.add(new JLabel("Source:"), Main.translator.getBeforeTextBorder());
		JTextField sourceField = new JTextField();
		sourceField.setEditable(false);
		if (Main.currentProject.folder != null) {
			sourceField.setText(Main.currentProject.folder);
		}
		sourcePanel.add(sourceField);
		multiPictureDialog.add(sourcePanel);
		JButton browseSource = new JButton("Browse");
		browseSource.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = sourceField.getText().equals("")?
						new JFileChooser(Main.install.getPath("Gallery")):new JFileChooser(new File(sourceField.getText()));
				fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				fc.showOpenDialog(multiPictureDialog);
				File f = fc.getSelectedFile();
				if (f != null) {
					sourceField.setText(f.getAbsolutePath());
				}
			}
		});
		sourcePanel.add(browseSource, Main.translator.getAfterTextBorder());
		JPanel namePanel = new JPanel(new BorderLayout());
		namePanel.add(new JLabel("Name:"), Main.translator.getBeforeTextBorder());
		JTextField nameField = new JTextField("multipicture");
		if (Main.currentProject.name != null) {
			nameField.setText(Main.currentProject.name);
		}
		namePanel.add(nameField);
		JLabel suffixLabel = new JLabel("<html><t/>.jpg</html>");
		namePanel.add(suffixLabel, Main.translator.getAfterTextBorder());
		multiPictureDialog.add(namePanel);
		
		JButton create = new JButton("Create");
		create.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						if (!MultipictureCreator.isLoading()) {
							try {
								MultipictureCreator.loadFromDir(Main.install.getFile("Data\\MultiPictures"));
								ImageIO.write(MultipictureCreator.create(
										ImageIO.read(new File(sourceField.getText()))), "jpg", 
										new File(Main.install.getPath("Gallery") + "\\" + nameField.getText() + ".jpg"));
							} catch (IOException e1) {
								e1.printStackTrace();
							}
							LDialogs.showMessageDialog(multiPictureDialog, "Picture has been created successfuly!");
						
						} else {
							LDialogs.showMessageDialog(multiPictureDialog, "Pictures are still loading...", "Can't Preform Actiom", LDialogs.WARNING_MESSAGE);
						}		
					}
				}).start();

			}
		});
		multiPictureDialog.add(create);
		
		multiPictureDialog.addWindowListener(new WindowListener() {
			
			@Override
			public void windowOpened(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowIconified(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowDeiconified(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowDeactivated(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowClosing(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowClosed(WindowEvent e) {
				MultipictureCreator.getLoadeds().clear();
			}
			
			@Override
			public void windowActivated(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		multiPictureDialog.pack();
		
		if (Main.install.getFile("Data\\MultiPictures").list().length != 0) {
			int option = LDialogs.showConfirmDialog(Main.f, "<html>There are pictures from previous work."
					+ "<br/>Do tou want to delete them and restart from zero?</html>",
					"Choose Option");
			if (option == LDialogs.YES_OPTION) {
				for(File f:Main.install.getFile("Data\\MultiPictures").listFiles()) {
					f.delete();
				}
			}
		}
		multiPictureDialog.setVisible(true);
	}
	
	public static BufferedImage getMultipicture(BufferedImage source, File dir) {
		System.out.println("Start");
		loadFromDir(dir);
		System.out.println("Loaded");
		return create(source);
	}
	
	public static void loadFromDir(File dir) {
		int total = dir.listFiles().length;
		int count = 0;
		for(File f:dir.listFiles()) {
			try {
				load(f);
			} catch (Exception e) {}
			System.out.println("Load " + ++count + " of " + total);
		}
	}

	public static BufferedImage create(BufferedImage source) {
		source = PictureUtilities.getScaledImage(source, Preferences.mpSourceWidth, (int) (source.getHeight() / ((double)source.getWidth() / Preferences.mpSourceWidth)));
		System.out.println(source.getWidth() + ", " + source.getHeight());
		BufferedImage ret = null;
		try {
			ret = new BufferedImage(Preferences.mpPixelSize * source.getWidth(), Preferences.mpPixelSize * source.getHeight(),
					BufferedImage.TYPE_INT_RGB);
		} catch (OutOfMemoryError e) {
			if (LDialogs.YES_OPTION == LDialogs.showConfirmDialog(Main.f, 
					"<html>The output picture is too big.<br/>"
					+ "Do you want to try to crate it in special preformence mode?</html>")) {
				ret = null;
				System.gc();
				System.out.println("Running");
				createLowRAM(source);
			} else {
				throw e;
			}
		}
		Graphics2D g = ret.createGraphics();
		for (int i = 0; i < source.getWidth(); i++) {
			for (int j = 0; j < source.getHeight(); j++) {
				Color c = new Color(source.getRGB(i, j));
				g.drawImage(findClosest(c), i * Preferences.mpPixelSize, j * Preferences.mpPixelSize, null);
			}
		}
		return ret;
	}
	
	public static BufferedImage createLowRAM(BufferedImage source) {
		//Creating  temporary folder
		String path = Main.install.getPath("Data//MultiPictures//temp");
		new File(path).mkdir();
		int subdivide = 10;
		int width = Preferences.mpSourceWidth - (Preferences.mpSourceWidth % subdivide);
		int height = (int) (source.getHeight() / ((double)source.getWidth() / width));
		height -= (height % subdivide);
		System.out.println("Creating source");
		source = PictureUtilities.getScaledImage(source, width,
				height);
		System.out.println("Source created");
		for (int i = 0; i < subdivide; i++) {
			for (int j = 0; j < subdivide; j++) {
				System.out.println("Creating temp");
				BufferedImage temp = new BufferedImage(
						width / subdivide * Preferences.mpPixelSize, height / subdivide * Preferences.mpPixelSize, BufferedImage.TYPE_INT_RGB);
				System.out.println("Temp created");
				Graphics2D g = temp.createGraphics();
				for (int x = 0; x < source.getWidth() / subdivide; x++) {
					for (int y = 0; y < source.getHeight() / subdivide; y++) {	
						Color c = new Color(source.getRGB(x + i * (width / subdivide), y + j * (height / subdivide)));
						g.drawImage(findClosest(c), x * Preferences.mpPixelSize, y * Preferences.mpPixelSize, null);
					}
				}
				try {
					ImageIO.write(temp, "jpg", new File(path + "\\" + i + j + ".jpg"));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		loadeds.clear();
		System.gc();
		System.out.println("Cleared");
		LDialogs.showMessageDialog(null, path);
		System.out.println("Creating");
		BufferedImage ret = new BufferedImage(Preferences.mpPixelSize * width, Preferences.mpPixelSize * height,
				BufferedImage.TYPE_INT_RGB);
		System.out.println("Created");
		Graphics2D g = ret.createGraphics();
		for (int i = 0; i < subdivide; i++) {
			for (int j = 0; j < subdivide; j++) {
				try {
					g.drawImage(ImageIO.read(new File(path + "\\" + i + j + ".jpg")),
							i * (width / subdivide), j * (width / subdivide), null);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return ret;
	}
	
	public static BufferedImage findClosest(Color c) {
		double minDistance = 0;
		Loaded closest = null;
		for (Loaded loaded : loadeds) {
			double distance = getDistance(c, loaded.avg) + loaded.times * Preferences.mpDivFactor;
			if (distance < minDistance || closest == null) {
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
		BufferedImage bf = ImageIO.read(imageFile);
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

	public static boolean isLoading() {
		return loading;
	}
}
