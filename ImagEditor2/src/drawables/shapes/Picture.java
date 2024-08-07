package drawables.shapes;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JTextField;

import drawables.Layer;
import drawables.shapes.abstractShapes.Shape;
import drawables.shapes.abstractShapes.StretchableShpae;
import effects.EffectsManager;
import gui.components.EditPanel;
import install.Preferences;
import install.Resources;
import le.gui.components.LSlider;
import le.gui.dialogs.LDialogs;
import le.utils.PictureUtilities;
import main.Main;
import operatins.changes.BooleanChange;
import operatins.changes.Change;
import operatins.changes.ChangeType;
import operatins.changes.NumericalChange;
import operatins.changes.ObjectChange;

public class Picture extends Shape implements StretchableShpae {

	public static final int MINIMUM = 5;

	//Size
	private double width;
	private double height;
	
	// Preview
	private boolean isPreview = false;
	private File source = null;

	// Source Image
	BufferedImage image;

	// Last Drawn Image, For CPU Saving
	private BufferedImage lastDrawn = null;

	// Cut Variables
	double cutFromLeft = 0;
	double cutFromTop = 0;
	double cutFromRight = 0;
	double cutFromBottom = 0;
	
	// Is the Picture currently cut
	private boolean isCutting;
	
	//Rotation
	double rotation;

	//Transparency
	private double transparency = 0;

	// Effects
	EffectsManager effectsManger = new EffectsManager(this);

	// Constructor
	public Picture(double x, double y, boolean visible, String name, double width, double height, double rotation,
			File src) {
		this(x, y, visible, name, width, height, rotation, getImageFromFile(src));
		if(usedPreview) {
			this.source = src;
			this.isPreview = true;
			usedPreview = false;
		}
	}

	// Constructor
	public Picture(double x, double y, boolean visible, String name, double width, double height, double rotation,
			BufferedImage img) {
		super(x, y, visible, name);
		this.width = width;
		this.height = height;
		this.rotation = rotation;
		this.image = convertToARGB(img);
	}
	
	//Turn to true if used preview while reading
	private static boolean usedPreview = false;
	
	//Constructor Utility Method
	public static BufferedImage getImageFromFile(File src) {
		if (Preferences.usePreviewPictures) {
			BufferedImage bf = null;
			try {
				bf = ImageIO.read(src);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (bf.getWidth() * bf.getHeight() < 200_000) {
				return bf;
			} else {
				int imageWidth = 1000;
				int imageHeight = (int) (((double) bf.getHeight()) / bf.getWidth() * 1000);
				usedPreview = true;
				return PictureUtilities.getScaledImage(bf, imageWidth, imageHeight);
			}
		} else {
			try {
				return convertToARGB(ImageIO.read(src));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

	// Methods
	@Override
	public void draw(Graphics2D g) {
		if (Main.getBoard().isExportPaintMode() && isPreview) {
			//In case the program uses only preview while editing, the code here gives the full
			//picture in maximum resolution.
			BufferedImage displayImage = new BufferedImage((int) getCutWidth(), (int) getCutHeight(),
					BufferedImage.TYPE_INT_ARGB_PRE);
			try {
				BufferedImage real = ImageIO.read(source);
				double previewProportion = real.getWidth() / 1000.0;
				displayImage.createGraphics()
						.drawImage(real.getSubimage((int) (cutFromLeft * previewProportion),
								(int) (cutFromTop * previewProportion), (int) (getCutWidth() * previewProportion),
								(int) (getCutHeight() * previewProportion)), 0, 0, null);
				if (getWidth() * getHeight() > image.getHeight() * image.getWidth()) {
					effectsManger.affectImage(displayImage);
					displayImage = PictureUtilities.getScaledImage(displayImage, (int)getWidth(),
							(int)getHeight());
				} else {
					displayImage = PictureUtilities.getScaledImage(displayImage, (int)getWidth(),
							(int)getHeight());
					effectsManger.affectImage(displayImage);
				}
				displayImage = PictureUtilities.rotateImageByDegrees(displayImage, rotation);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (isCutting && Preferences.showCutPartsOfPicture && Main.getLayersList().getSelectedShape() == this) {
			//Displaying the whole picture behind the current one
			float[] scales = { 1f, 1f, 1f, 0.3f };
			float[] offsets = new float[4];
			RescaleOp rop = new RescaleOp(scales, offsets, null);
			double horzCut = (cutFromLeft + cutFromRight);
			double vertCut = (cutFromTop + cutFromBottom);
			double horzRatio = width / (image.getWidth() - horzCut);
			double vertRatio = height /  (image.getHeight() - vertCut);
			int nonCutOnBoardWidth = (int)(getWidthOnBoard() + horzCut * horzRatio);
			int nonCutOnBoardHeight = (int)(getHeightOnBoard() + vertCut * vertRatio);
			g.drawImage(PictureUtilities.getScaledImage(
						image, nonCutOnBoardWidth, nonCutOnBoardHeight),
					rop,(int) (x - cutFromLeft * horzRatio), (int) (y - cutFromTop * vertRatio));
		}
		if (!Preferences.useMoreRAM) {
			// In case the setting has been changed while the program is running, so
			// previous lastDrawn won't stuck in the memory
			invalidate();
            g.setComposite(AlphaComposite.SrcOver.derive((float)(1 - transparency)));
			g.drawImage(getImageToDisplay(), (int) x, (int) y, null);
            g.setComposite(AlphaComposite.SrcOver.derive((float)(1)));
			return;
		}
		if (lastDrawn == null) {
			lastDrawn = getImageToDisplay();
		}
        g.setComposite(AlphaComposite.SrcOver.derive((float)(1 - transparency)));
		g.drawImage(lastDrawn, (int) x, (int) y, null);
        g.setComposite(AlphaComposite.SrcOver.derive((float)(1)));
	}

	public BufferedImage getImageToDisplay() {
		BufferedImage displayImage = new BufferedImage((int) getCutWidth(), (int) getCutHeight(),
				BufferedImage.TYPE_INT_ARGB);
		displayImage.createGraphics().drawImage(
				image.getSubimage((int) cutFromLeft, (int) cutFromTop, (int) getCutWidth(), (int) getCutHeight()), 0, 0,
				null);
		if ((int) getWidth() * (int) getHeight() > image.getHeight() * image.getWidth()) {
			effectsManger.affectImage(displayImage);
			displayImage = PictureUtilities.getScaledImage(displayImage, (int) getWidth(),
					(int) getHeight());
		} else {
			displayImage = PictureUtilities.getScaledImage(displayImage, (int) getWidth(),
					(int) getHeight());
			effectsManger.affectImage(displayImage);
		}
		displayImage = PictureUtilities.rotateImageByDegrees(displayImage, rotation);
		return displayImage;
	}
	
	public static BufferedImage convertToARGB(BufferedImage tmp) {
		BufferedImage ret = new BufferedImage(tmp.getWidth(), tmp.getHeight(), 
				BufferedImage.TYPE_INT_ARGB);
		ret.createGraphics().drawImage(tmp, 0, 0, null);
		return ret;
	}

	public double getCutHeight() {
		return image.getHeight() - cutFromTop - cutFromBottom;
	}

	public double getCutWidth() {
		return image.getWidth() - cutFromLeft - cutFromRight;
	}

	@Override
	public EditPanel getEditPanel(boolean dialog){
		//Position Panel creation
		EditPanel positionPanel = createPositionPanel();
		//Size Panel creation
		JPanel sizePanel = new JPanel(new BorderLayout());
		EditPanel heightNwidthPanel = createSizePanel();
		sizePanel.add(heightNwidthPanel);
		JButton toNaturalImageSizeButton = new JButton("To Natural Image Size");
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
		//Rotation Slider creation
		LSlider rotationSlider = new LSlider("Rotation: ", 0, 360, rotation, 0.1);
		//Transparency Slider creation
		LSlider transparencySlider = new LSlider("Trancparency: ", 0, 1, transparency, 0.05);
		//Source Panel creation
		JPanel sourcePanel = new JPanel(new BorderLayout());
		sourcePanel.add(new JLabel("Source:"),
				Main.translator.getBeforeTextBorder());
		JTextField sourceField = new JTextField("don\'t change");
		sourceField.setBackground(Main.theme.getBackgroundColor());
		sourceField.setForeground(Main.theme.getTextColor());
		sourceField.setEditable(false);
		sourcePanel.add(sourceField);
		JButton browse = new JButton("Browse");
		browse.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = sourceField.getText().equals("don\'t change")?
						new JFileChooser(Main.install.getPath("Gallery")):new JFileChooser(new File(sourceField.getText()));
				fc.showOpenDialog(sourcePanel.getParent());
				File f = fc.getSelectedFile();
				if (f != null) {
					sourceField.setText(f.getAbsolutePath());
				}
			}
		});
		sourcePanel.add(browse, Main.translator.getAfterTextBorder());
		GridLayout gl = new GridLayout(dialog ? 5 : 1, dialog ? 1 : 5);
		gl.setHgap(10);
		gl.setVgap(5);
		EditPanel editPanel = new EditPanel(gl) {
			private static final long serialVersionUID = 1L;
			
			@Override
			public LinkedList<Change> getChanges() {
				try {
					double rotation = rotationSlider.getValue();
					double transparency = transparencySlider.getValue();
					BufferedImage image = null;
					if (!sourceField.getText().equals("don\'t change")) {
						File f = new File(sourceField.getText());
						try {
							image = readImage(f);
						} catch (Exception e2) {
							LDialogs.showMessageDialog(sourcePanel.getParent(),
									"Invalid File Destination", "ERROR", LDialogs.ERROR_MESSAGE);
						}
					}
					LinkedList<Change> changes = new LinkedList<>();
					if (dialog || Preferences.showPositionOnTop) {
						changes.addAll(positionPanel.getChanges());
					}
					if (dialog || Preferences.showSizeOnTop) {
						changes.addAll(heightNwidthPanel.getChanges());
					}
					if (Picture.this.rotation != rotation) {
						changes.add(new NumericalChange(ChangeType.ROTATION_CHANGE, rotation - Picture.this.rotation));
					}
					if (Picture.this.transparency != transparency) {
						changes.add(new NumericalChange(ChangeType.TRANSPARENCY_CHANGE, transparency - Picture.this.transparency));
					}
					if (image != null) {
						if (Preferences.usePreviewPictures) {
							File src = new File(sourceField.getText());
							BufferedImage bf = null;
							try {
								bf = ImageIO.read(src);
							} catch (IOException e4) {
								// TODO Auto-generated catch block
								e4.printStackTrace();
							}
							if (bf.getWidth() * bf.getHeight() < 2_000_000) {
								Picture.this.image = bf;
							} else {
								int imageWidth = 1000;
								int imageHeight = (int) (((double)bf.getHeight())/bf.getWidth() * 1000);
								image = PictureUtilities.getScaledImage(bf, imageWidth, imageHeight);
								changes.add(new ObjectChange(ChangeType.SRC_PREVIEW_CHANGE, Picture.this.source, src));
								if (!Picture.this.isPreview) {
									changes.add(new BooleanChange(ChangeType.PREVIEW_CHANGE, true));
								}
							}	
							changes.add(new ObjectChange(ChangeType.SRC_IMAGE_CHANGE, Picture.this.image, image));
						}
					}
					return changes;
					
				} catch (Exception e2) {
					LDialogs.showMessageDialog(Main.f, "Invalid input", "Error", LDialogs.ERROR_MESSAGE);
				}
				return null;
			}
		};
		if (dialog || Preferences.showPositionOnTop) {
			editPanel.add(positionPanel);
		}
		if (dialog || Preferences.showSizeOnTop) {
			editPanel.add(sizePanel);
		}
		editPanel.add(rotationSlider);
		editPanel.add(transparencySlider);
		editPanel.add(sourcePanel);
		return editPanel;
	}

	public void editEffects() {
		effectsManger.edit(this);
	}

	public void invalidate() {
		lastDrawn = null;
	}

	@Override
	public int getHeightOnBoard() {
		return (int) Math.floor((int) getHeight() * Math.cos(Math.toRadians(rotation))
				+ (int) getWidth() * Math.sin(Math.toRadians(rotation)));
	}

	@Override
	public int getWidthOnBoard() {
		return (int) Math.floor((int) getHeight() * Math.sin(Math.toRadians(rotation))
				+ (int) getWidth() * Math.cos(Math.toRadians(rotation)));
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
	
	@Override
	public double getWidth() {
		return this.width;
	}
	
	@Override
	public void setWidth(double width) {
		this.width = width;
	}
	
	@Override
	public double getHeight() {
		return this.height;
	}
	
	@Override
	public void setHeight(double height) {
		this.height = height;
	}

	public double getCutFromLeft() {
		return cutFromLeft;
	}

	public void setCutFromLeft(double cutFromLeft) {
		if(cutFromLeft < 0) {
			cutFromLeft = 0;
		}
		if (this.cutFromRight + cutFromLeft > image.getWidth() - MINIMUM) {
			cutFromLeft = image.getWidth() - MINIMUM - this.cutFromRight;
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
		if(cutFromTop < 0) {
			cutFromTop = 0;
		}
		if (this.cutFromBottom + cutFromTop > image.getWidth() - MINIMUM) {
			cutFromTop = image.getWidth() - MINIMUM - this.cutFromBottom;
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
		if(cutFromRight < 0) {
			cutFromRight = 0;
		}
		if (this.cutFromLeft + cutFromRight > image.getWidth() - MINIMUM) {
			cutFromRight = image.getWidth() - MINIMUM - this.cutFromLeft;
		}
		double diff = (cutFromRight - this.cutFromRight) * getWidthStretchRatio();
		this.width -= diff;
		this.cutFromRight = cutFromRight;
	}

	public double getCutFromBottom() {
		return cutFromBottom;
	}

	public void setCutFromBottom(double cutFromBottom) {
		if(cutFromBottom < 0) {
			cutFromBottom = 0;
		}
		if (this.cutFromTop + cutFromBottom > image.getWidth() - MINIMUM) {
			cutFromBottom = image.getWidth() - MINIMUM - this.cutFromTop;
		}
		double diff = (cutFromBottom - this.cutFromBottom) * getHeightStretchRatio();
		this.height -= diff;
		this.cutFromBottom = cutFromBottom;
	}

	public Picture copy() {
		if (lastDrawn == null) {
			lastDrawn = getImageToDisplay();
		}
		return new Picture(0, 0, true, "Copy of " + this.getName(), getWidthOnBoard(), getHeightOnBoard(), 0,
				lastDrawn);
	}

	public Picture(String[] data) throws NumberFormatException, IOException {
		this(Double.parseDouble(data[0]), Double.parseDouble(data[1]), Boolean.parseBoolean(data[2]), data[3],
				Double.parseDouble(data[4]), Double.parseDouble(data[5]), Double.parseDouble(data[6]),
				decodeSourceImage(data[12]));
		this.transparency = Double.parseDouble(data[7]);
		this.cutFromLeft = Double.parseDouble(data[8]);
		this.cutFromTop = Double.parseDouble(data[9]);
		this.cutFromRight = Double.parseDouble(data[10]);
		this.cutFromBottom = Double.parseDouble(data[11]);
		this.effectsManger = new EffectsManager(data[13], this);
	}

	public Picture(String line) throws NumberFormatException, IOException {
		this(line.split(","));
	}

	@Override
	public String encodeShape() {
		return super.encodeShape() + ", " + width + ", " + height + "," + rotation + "," + transparency + "," + cutFromLeft + "," + cutFromTop + "," + cutFromRight + ","
				+ cutFromBottom + "," + encodeSourceImge(image) + "," + effectsManger.encodeEffect();
	}

	public static String encodeSourceImge(BufferedImage bf) {
		StringBuilder sb = new StringBuilder();
		sb.append(bf.getWidth());
		sb.append('!');
		sb.append(bf.getHeight());
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
		BufferedImage bf = new BufferedImage((int) Double.parseDouble(data[0]), (int) Double.parseDouble(data[1]),
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
		JMenuItem cut = new JMenuItem(isCutting ? "Stop Cut" : "Cut");
		Main.theme.affect(cut);
		cut.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (Picture.this.isCutting) {
					stopCutting();
				} else {
					startCutting();
				}
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
	
	@Override
	public void invalidateSize() {
		Main.getLayersList().getLayerForShape(this).adjustTopSize(getWidthOnBoard(), getHeightOnBoard());
	}
	
	public void startCutting() {
		isCutting = true;
	}
	
	public void stopCutting() {
		isCutting = false;
	}

	public double getWidthStretchRatio() {
		return width / getCutWidth();
	}

	public double getHeightStretchRatio() {
		return height / getCutHeight();
	}

	public double getRotation() {
		return rotation;
	}

	public void setRotation(double rotation) {
		this.rotation = rotation;
	}

	public double getTransparency() {
		return transparency;
	}

	public void setTransparency(double transparency) {
		this.transparency = transparency;
	}

	public boolean isCutting() {
		return isCutting;
	}

	public void setCutting(boolean isCutting) {
		this.isCutting = isCutting;
	}

	public void addToCutFromLeft(int diff) {
		setCutFromLeft(getCutFromLeft() + (1 / getWidthStretchRatio()) * diff);
	}

	public void addToCutFromTop(int diff) {
		setCutFromTop(getCutFromTop() + (1 / getHeightStretchRatio()) * diff);
	}

	public void addToCutFromRight(int diff) {
		setCutFromRight(getCutFromRight() + (1 / getWidthStretchRatio()) * diff);
	}

	public void addToCutFromBottom(int diff) {
		setCutFromBottom(getCutFromBottom() + (1 / getHeightStretchRatio()) * diff);
	}

	public boolean isPreview() {
		return isPreview;
	}

	public void setPreview(boolean isPreview) {
		this.isPreview = isPreview;
	}

	public File getSource() {
		return source;
	}

	public void setSource(File source) {
		this.source = source;
	}

	public static Picture createNewDefaultPicture() {
		return new Picture(0, 0, true, null, 150, 50, 0, Resources.defaultImage);
	}
}