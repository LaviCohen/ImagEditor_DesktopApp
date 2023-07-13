package drawables.shapes;

import java.awt.BorderLayout;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JTextField;

import drawables.Layer;
import drawables.shapes.abstractShapes.StretchableShpae;
import effects.EffectsManager;
import gui.components.EditPanel;
import install.DefaultSettings;
import le.gui.dialogs.LDialogs;
import le.utils.PictureUtilities;
import main.Main;
import operatins.ChangesOperation;
import operatins.OperationsManager;
import operatins.changes.Change;
import operatins.changes.NumericalChange;
import operatins.changes.ObjectChange;

public class Picture extends StretchableShpae{
	
	public static final int MINIMUM = 5;
	
	//Source Image
	BufferedImage image;
	
	//Last Drawn Image, For CPU Saving
	private BufferedImage lastDrawn = null;
	
	//Cut Variables
	double cutFromLeft = 0;
	double cutFromTop = 0;
	double cutFromRight = 0;
	double cutFromBottom = 0;
	
	//Is the Picture currently cut
	private boolean isCutting;
	
	//Effects
	EffectsManager effectsManger = new EffectsManager(this);
	
	//Constructor
	public Picture(double x, double y, boolean visible, String name, double width, double height, BufferedImage img) {
		super(x, y, visible, name, width, height);
		this.image = img;
	}
	//Methods
	@Override
	public void draw(Graphics2D g) {
		if (!DefaultSettings.useMoreRAM) {
			//In case the setting has been changed while the program is running, so previous lastDrawn won't stuck in the memory
			invalidate();
			g.drawImage(getImageToDisplay(), (int)x, (int)y, getWidthOnBoard(), getHeightOnBoard(), null);
			return;
		}
		if (lastDrawn == null) {
			lastDrawn = getImageToDisplay();
		}
		g.drawImage(lastDrawn, (int)x, (int)y, getWidthOnBoard(), getHeightOnBoard(), null);
	}
	public BufferedImage getImageToDisplay() {
		BufferedImage displayImage =  new BufferedImage((int)getCutWidth(), (int)getCutHeight(), BufferedImage.TYPE_INT_ARGB_PRE);
		displayImage.createGraphics().drawImage(image.getSubimage(
				(int)cutFromLeft, (int)cutFromTop, (int)getCutWidth(), (int)getCutHeight()),
				0, 0, null);
		if (getWidthOnBoard() * getHeightOnBoard() > image.getHeight() * image.getWidth()) {
			effectsManger.affectImage(displayImage);
			displayImage = PictureUtilities.getScaledImage(displayImage, getWidthOnBoard(), getHeightOnBoard());
		} else {
			displayImage = PictureUtilities.getScaledImage(displayImage, getWidthOnBoard(), getHeightOnBoard());
			effectsManger.affectImage(displayImage);
		}
		return displayImage;
	}
	public double getCutHeight() {
		return image.getHeight() - cutFromTop - cutFromBottom;
	}
	public double getCutWidth() {
		return image.getWidth() - cutFromLeft - cutFromRight;
	}
	@Override
	public void edit() {
		JDialog editDialog = new JDialog(Main.f);
		editDialog.setLayout(new GridLayout(4, 1));
		editDialog.setTitle("Edit Picture");
		EditPanel positionPanel = createPositionPanel();
		editDialog.add(positionPanel);
		JPanel sizePanel = new JPanel(new BorderLayout());
		EditPanel heightNwidthPanel = createSizePanel();
		sizePanel.add(heightNwidthPanel);
		JButton toNaturalImageSizeButton = new JButton("To Natural Image Size");
		Main.theme.affect(toNaturalImageSizeButton);
		toNaturalImageSizeButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JTextField widthField = (JTextField) heightNwidthPanel.getComponent(1);
				JTextField heightField = (JTextField) heightNwidthPanel.getComponent(3);
				widthField.setText(Picture.this.image.getWidth() + "");
				heightField.setText(Picture.this.image.getHeight() + "");
			}
		});
		sizePanel.add(toNaturalImageSizeButton, Main.translator.getAfterTextBorder());
		editDialog.add(sizePanel);
		JPanel sourcePanel = new JPanel(new BorderLayout());
		sourcePanel.add(Main.theme.affect(new JLabel("Source:")),
				Main.translator.getBeforeTextBorder());
		JTextField sourceField = new JTextField("don\'t change");
		sourceField.setBackground(Main.theme.getBackgroundColor());
		sourceField.setForeground(Main.theme.getTextColor());
		sourceField.setEditable(false);
		sourcePanel.add(sourceField);
		JButton browse = new JButton("Browse");
		Main.theme.affect(browse);
		browse.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = sourceField.getText().equals("don\'t change")?
						new JFileChooser(Main.install.getPath("Gallery")):new JFileChooser(new File(sourceField.getText()));
				fc.showOpenDialog(editDialog);
				File f = fc.getSelectedFile();
				if (f != null) {
					sourceField.setText(f.getAbsolutePath());
				}
			}
		});
		sourcePanel.add(browse, Main.translator.getAfterTextBorder());
		editDialog.add(sourcePanel);
		ActionListener actionListener = new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Object[] positionData = positionPanel.getData();
					double x = (Double) positionData[0];
					double y = (Double) positionData[1];
					Object[] sizeData = heightNwidthPanel.getData();
					double width = (Double) sizeData[0];
					double height = (Double) sizeData[1];
					BufferedImage image = null;
					if (!sourceField.getText().equals("don\'t change")) {
						File f = new File(sourceField.getText());
						try {
							image = readImage(f);
						} catch (Exception e2) {
							LDialogs.showMessageDialog(editDialog, "Invalid File Destination",
									"ERROR", LDialogs.ERROR_MESSAGE);
						}
					}
					LinkedList<Change> changes = new LinkedList<>();
					if (Picture.this.x != x) {
						changes.add(new NumericalChange(Change.X_CHANGE, x - Picture.this.x));
					}
					if (Picture.this.y != y) {
						changes.add(new NumericalChange(Change.Y_CHANGE, y - Picture.this.y));
					}
					if (Picture.this.width != width) {
						changes.add(new NumericalChange(Change.WIDTH_CHANGE, width - Picture.this.width));
					}
					if (Picture.this.height != height) {
						changes.add(new NumericalChange(Change.HEIGHT_CHANGE, height - Picture.this.height));
					}
					if (image != null) {
						changes.add(new ObjectChange(Change.SRC_IMAGE_CHANGE, Picture.this.image, image));
					}
					
					if (!changes.isEmpty()) {
						OperationsManager.operate(new ChangesOperation(Picture.this, changes));
						invalidate();
						Main.getLayersList().updateImage(Picture.this);
						Main.getBoard().repaint();
					}
				} catch (Exception e2) {
					LDialogs.showMessageDialog(Main.f, "Invalid input", "Error", LDialogs.ERROR_MESSAGE);
				}
				
				if (e.getActionCommand().equals("Apply & Close")) {
					editDialog.dispose();
				}
			}
		};
		editDialog.add(createActionPanel(actionListener));
		editDialog.pack();
		editDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		editDialog.setVisible(true);
	}
	public void editEffects() {
		effectsManger.edit(this);
	}
	public void invalidate() {
		lastDrawn = null;
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
		invalidate();
		this.image = img;
	}
	public BufferedImage getLastDrawn() {
		return lastDrawn;
	}
	public void setLastDrawn(BufferedImage lastDrawn) {
		this.lastDrawn = lastDrawn;
	}
	public EffectsManager getEffectsManger() {
		return effectsManger;
	}
	public void setEffectsManger(EffectsManager effectsManger) {
		this.effectsManger = effectsManger;
	}
	public double getCutFromLeft() {
		return cutFromLeft;
	}
	public void setCutFromLeft(double cutFromLeft) {
		if (cutFromLeft < 0 || cutFromLeft + this.cutFromRight > image.getWidth() - MINIMUM) {
			return;
		}
		double diff = (cutFromLeft - this.cutFromLeft) * getWidthStretchRatio();
		this.x += diff;
		this.width -= diff;
		this.cutFromLeft = cutFromLeft;
	}
	public double getCutFromTop() {
		return cutFromTop;
	}
	public void setCutFromTop(double cutFromTop) {
		if (cutFromTop < 0 || cutFromTop + this.cutFromBottom > image.getHeight() - MINIMUM) {
			return;
		}
		double diff = (cutFromTop - this.cutFromTop) * getHeightStretchRatio();
		this.y += diff;
		this.height -= diff;
		this.cutFromTop = cutFromTop;
	}
	public double getCutFromRight() {
		return cutFromRight;
	}
	public void setCutFromRight(double cutFromRight) {
		if (cutFromRight < 0 || this.cutFromLeft + cutFromRight > image.getWidth() - MINIMUM) {
			return;
		}
		double diff = (cutFromRight - this.cutFromRight) * getWidthStretchRatio();
		this.width -= diff;
		this.cutFromRight = cutFromRight;
	}
	public double getCutFromBottom() {
		return cutFromBottom;
	}
	public void setCutFromBottom(double cutFromBottom) {
		if (cutFromBottom < 0 || this.cutFromTop + cutFromBottom > image.getHeight() - MINIMUM) {
			return;
		}
		double diff = (cutFromBottom - this.cutFromBottom) * getHeightStretchRatio();
		this.height -= diff;
		this.cutFromBottom = cutFromBottom;
	}
	public Picture copy() {
		if (lastDrawn == null) {
			lastDrawn = getImageToDisplay();
		}
		return new Picture(0, 0, true, "Copy of " + this.getName(), getWidthOnBoard(), getHeightOnBoard(), lastDrawn);		
	}
	public Picture(String[] data) throws NumberFormatException, IOException {
		this(Double.parseDouble(data[0]), Double.parseDouble(data[1]), Boolean.parseBoolean(data[2]),
				data[3], Double.parseDouble(data[4]), Double.parseDouble(data[5]),
				decodeSourceImage(data[6]));
		this.effectsManger = new EffectsManager(data[7], this);
	}
	public Picture(String line) throws NumberFormatException, IOException {
		this(line.split(","));
	}
	@Override
	public String encodeShape() {
		return super.encodeShape() + "," + encodeSourceImge(image) + "," + effectsManger.encodeEffect();
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
		BufferedImage bf = new BufferedImage((int)Double.parseDouble(data[0]), (int)Double.parseDouble(data[1]),
				BufferedImage.TYPE_INT_ARGB);
		for (int i = 0; i < bf.getWidth(); i++) {
			for (int j = 0; j < bf.getHeight(); j++) {
				bf.setRGB(i, j, Integer.parseInt(data[i * bf.getHeight() + j + 2]));
			}
		}
		return bf;
	}
	@Override
	public JPopupMenu getPopupMenuForShape() {
		JPopupMenu popup = super.getPopupMenuForShape();
		popup.add(new JSeparator());
		JMenuItem editEffects = new JMenuItem("Edit Effects");
		Main.theme.affect(editEffects);
		editEffects.addActionListener(new ActionListener() {
	
			@Override
			public void actionPerformed(ActionEvent e) {
				Picture.this.editEffects();
			}
		});
		popup.add(editEffects);
		JMenuItem cut = new JMenuItem(isCutting?"Stop Cut":"Cut");
		Main.theme.affect(cut);
		cut.addActionListener(new ActionListener() {
	
			@Override
			public void actionPerformed(ActionEvent e) {
				Picture.this.setCutting(!Picture.this.isCutting);
			}
		});
		popup.add(cut);
		JMenuItem cancelCut = new JMenuItem("Cancel Cut");
		Main.theme.affect(cancelCut);
		cancelCut.addActionListener(new ActionListener() {
	
			@Override
			public void actionPerformed(ActionEvent e) {
				Picture.this.setCutFromLeft(0);
				Picture.this.setCutFromTop(0);
				Picture.this.setCutFromRight(0);
				Picture.this.setCutFromBottom(0);
				invalidate();
				Main.getBoard().repaint();
			}
		});
		popup.add(cancelCut);
		JMenuItem copy = new JMenuItem("Copy as Image");
		Main.theme.affect(copy);
		copy.addActionListener(new ActionListener() {
	
			@Override
			public void actionPerformed(ActionEvent e) {
				Main.getBoard().addLayer(new Layer(Picture.this.copy()));
			}
		});
		popup.add(copy);
		return popup;
	}
	public double getWidthStretchRatio() {
		return width / getCutWidth();
	}
	public double getHeightStretchRatio() {
		return height / getCutHeight();
	}
	public boolean isCutting() {
		return isCutting;
	}
	public void setCutting(boolean isCutting) {
		this.isCutting = isCutting;
	}
	public void addToCutFromLeft(int diff) {
		setCutFromLeft(getCutFromLeft() + 1/getWidthStretchRatio() * diff);
	}
	public void addToCutFromTop(int diff) {
		setCutFromTop(getCutFromTop() + 1/getHeightStretchRatio() * diff);
	}
	public void addToCutFromRight(int diff) {
		setCutFromRight(getCutFromRight() + 1/getWidthStretchRatio() * diff);
	}
	public void addToCutFromBottom(int diff) {
		setCutFromBottom(getCutFromBottom() + 1/getHeightStretchRatio() * diff);
	}
}