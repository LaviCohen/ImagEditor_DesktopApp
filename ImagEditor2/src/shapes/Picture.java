package shapes;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import effects.EffectsManger;
import gui.Theme;
import install.DefaultSettings;
import main.Main;

public class Picture extends Shape{
	
	BufferedImage image;
	public BufferedImage lastDrawn = null;
	
	//Picture size
	int width;
	int height;
	
	//Effects
	EffectsManger effectsManger = new EffectsManger(this);
	
	//Constructor
	public Picture(int x, int y, boolean visible, String name, BufferedImage img, int width, int height) {
		super(x, y, visible, name);
		this.image = img;
		this.width = width;
		this.height = height;
	}
	//Methods
	@Override
	public void draw(Graphics g) {
		if (!DefaultSettings.useMoreRAM) {
			//In case the setting has been changed while the program is running, so previous lastDrawn won't stuck in the memory
			lastDrawn = null;
			g.drawImage(getImageToDisplay(), x, y, getWidthOnBoard(), getHeightOnBoard(), null);
		}
		if (lastDrawn == null) {
			lastDrawn = getImageToDisplay();
		}
		g.drawImage(lastDrawn, x, y, getWidthOnBoard(), getHeightOnBoard(), null);
	}
	public BufferedImage getImageToDisplay() {
		BufferedImage displayImage = getScaledImage(image, getWidthOnBoard(), getHeightOnBoard());
    	effectsManger.getImage(displayImage);
    	return displayImage;
	}
	@Override
	public int getWidthOnBoard() {
		return (image.getWidth() * width)/100;
	}
	@Override
	public int getHeightOnBoard() {
		return (image.getHeight() * height)/100;
	}
	@Override
	public void edit() {
		JDialog editDialog = new JDialog(Main.f);
		editDialog.setLayout(new GridLayout(4, 1));
		editDialog.setTitle("Edit Picture");
		editDialog.setBackground(Theme.getBackgroundColor());
		JPanel positionPanel = new JPanel(new GridLayout(1, 4));
		positionPanel.setBackground(Theme.getBackgroundColor());
		JLabel xJLabel = new JLabel("X:");
		xJLabel.setForeground(Theme.getTextColor());
		positionPanel.add(xJLabel);
		JTextField xField = new JTextField(this.x + "");
		positionPanel.add(xField);
		JLabel yJLabel = new JLabel("Y:");
		yJLabel.setForeground(Theme.getTextColor());
		positionPanel.add(yJLabel);
		JTextField yField = new JTextField(this.y + "");
		positionPanel.add(yField);
		editDialog.add(positionPanel);
		JPanel sizePanel = new JPanel(new GridLayout(1, 4));
		sizePanel.setBackground(Theme.getBackgroundColor());
		JLabel widthLabel = new JLabel("width:");
		widthLabel.setForeground(Theme.getTextColor());
		sizePanel.add(widthLabel);
		JTextField widthField = new JTextField(this.width + "");
		sizePanel.add(widthField);
		JLabel heightLabel = new JLabel("height:");
		heightLabel.setForeground(Theme.getTextColor());
		sizePanel.add(heightLabel);
		JTextField heightField = new JTextField(this.height + "");
		sizePanel.add(heightField);
		editDialog.add(sizePanel);
		JPanel sourcePanel = new JPanel(new BorderLayout());
		sourcePanel.setBackground(Theme.getBackgroundColor());
		JLabel sourceLabel = new JLabel("Source:");
		sourceLabel.setForeground(Theme.getTextColor());
		sourcePanel.add(sourceLabel, Main.translator.getBeforeTextBorder());
		JTextField sourceField = new JTextField("don\'t change");
		sourceField.setEditable(false);
		sourceField.setBackground(Theme.getBackgroundColor());
		sourceField.setForeground(Theme.getTextColor());
		sourcePanel.add(sourceField);
		JButton browse = new JButton("Browse");
		browse.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = sourceField.getText().equals("don\'t change")?
						new JFileChooser(Main.install.getPath("Gallery")):new JFileChooser(new File(sourceField.getText()));
				fc.showOpenDialog(editDialog);
				File f = fc.getSelectedFile();
				sourceField.setText(f.getAbsolutePath());
			}
		});
		sourcePanel.add(browse, Main.translator.getAfterTextBorder());
		editDialog.add(sourcePanel);
		JButton apply = new JButton("Apply");
		final Picture cur = this;
		apply.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					lastDrawn = null;
					int x = Integer.parseInt(xField.getText());
					int y = Integer.parseInt(yField.getText());
					int width = Integer.parseInt(widthField.getText());
					int height = Integer.parseInt(heightField.getText());
					if (!sourceField.getText().equals("don\'t change")) {
						File f = new File(sourceField.getText());
						try {
							cur.image = readImage(f);
						} catch (Exception e2) {
							JOptionPane.showMessageDialog(editDialog, "Invalid File Destination",
									"ERROR", JOptionPane.ERROR_MESSAGE);
						}
					}
					cur.x = x;
					cur.y = y;
					cur.width = width;
					cur.height = height;
					Main.getShapeList().updateImage(cur);
					editDialog.dispose();
					Main.getBoard().repaint();
				} catch (Exception e2) {
					JOptionPane.showMessageDialog(Main.f, "Invalid input", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		JButton preview = new JButton("Preview");
		preview.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					lastDrawn = null;
					int x = Integer.parseInt(xField.getText());
					int y = Integer.parseInt(yField.getText());
					int width = Integer.parseInt(widthField.getText());
					int height = Integer.parseInt(heightField.getText());
					if (!sourceField.getText().equals("don\'t change")) {
						File f = new File(sourceField.getText());
						try {
							cur.image = readImage(f);
						} catch (Exception e2) {
							JOptionPane.showMessageDialog(editDialog, "Invalid File Destination",
									"ERROR", JOptionPane.ERROR_MESSAGE);
						}
					}
					cur.x = x;
					cur.y = y;
					cur.width = width;
					cur.height = height;
					Main.getShapeList().updateImage(cur);
					Main.getBoard().repaint();
				} catch (Exception e2) {
					JOptionPane.showMessageDialog(Main.f, "Invalid input", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		JPanel actionPanel = new JPanel(new BorderLayout());
		actionPanel.add(apply);
		actionPanel.add(preview, BorderLayout.EAST);
		editDialog.add(actionPanel);
		editDialog.pack();
		editDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		editDialog.setVisible(true);
	}
	public void editEffects() {
		effectsManger.edit();
		lastDrawn = null;
	}
	public static BufferedImage getScaledImage(Image srcImg, int width, int height){
	    BufferedImage resizedImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	    Graphics2D g2 = resizedImg.createGraphics();
	    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	    g2.drawImage(srcImg, 0, 0, width, height, null);
	    g2.dispose();
	    return resizedImg;
	}
	public static BufferedImage readImage(File source) {
		try {
			return ImageIO.read(source);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	public BufferedImage getImage() {
		return this.image;
	}
	public void setImage(BufferedImage img) {
		this.image = img;
	}
	public BufferedImage getLastDrawn() {
		return lastDrawn;
	}
	public void setLastDrawn(BufferedImage lastDrawn) {
		this.lastDrawn = lastDrawn;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public EffectsManger getEffectsManger() {
		return effectsManger;
	}
	public void setEffectsManger(EffectsManger effectsManger) {
		this.effectsManger = effectsManger;
	}
	public Picture copy() {
		BufferedImage image = null;
		if (this.width != 100 || this.height != 100) {
			int width = this.width;
			int height = this.height;
			this.width = 100;
			this.height = 100;
			image = getImageToDisplay();
			this.height = height;
			this.width = width;
		}else {
			if (lastDrawn != null) {
				image = lastDrawn;
			}else {
				image = getImageToDisplay();
			}
		}
		return new Picture(0, 0, true, "Copy of " + this.getName(), image, getWidth(), getHeight());		
	}
	public Picture(String[] data) throws NumberFormatException, IOException {
		this(Integer.parseInt(data[0]), Integer.parseInt(data[1]), Boolean.parseBoolean(data[2]),
				data[3], decodeSourceImage(data[4]), Integer.parseInt(data[5]), 
				Integer.parseInt(data[6]));
		this.effectsManger = new EffectsManger(data[7], this);
	}
	@Override
	public String encodeShape() {
		return super.encodeShape() + "," + encodeSourceImge(image) + "," + width + "," + height
				 + "," + effectsManger.encodeEffect();
	}
	public static String encodeSourceImge(BufferedImage bf) {
		StringBuilder sb = new StringBuilder();
		sb.append(bf.getWidth());sb.append('!');sb.append(bf.getHeight());
		for (int i = 0; i < bf.getWidth(); i++) {
			for (int j = 0; j < bf.getHeight(); j++) {
				sb.append('!');
				sb.append(bf.getRGB(i, j));
			}
		}
		return sb.toString();
	}
	public static BufferedImage decodeSourceImage(String s) {
		String[] data = s.split("!");
		BufferedImage bf = new BufferedImage(Integer.parseInt(data[0]), Integer.parseInt(data[1]),
				BufferedImage.TYPE_INT_ARGB);
		for (int i = 0; i < bf.getWidth(); i++) {
			for (int j = 0; j < bf.getHeight(); j++) {
				bf.setRGB(i, j, Integer.parseInt(data[i * bf.getHeight() + j + 2]));
			}
		}
		return bf;
	}
}