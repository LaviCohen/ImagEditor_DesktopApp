package drawables.shapes;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import drawables.shapes.abstractShapes.StretchableShpae;
import le.gui.components.LSlider;
import le.gui.dialogs.LDialogs;
import main.Main;
import operatins.ChangesOperation;
import operatins.OperationsManager;
import operatins.changes.BooleanChange;
import operatins.changes.Change;
import operatins.changes.NumericalChange;
import operatins.changes.ObjectChange;

public class Rectangle extends StretchableShpae{

	Color color;
	
	int roundWidth;
	int roundHeight;
	
	
	boolean isFilled;
	
	public Rectangle(double x, double y, boolean visible, String name, double width, double height, Color color) {
		super(x, y, visible, name, width, height);
		this.color = color;
		this.isFilled = true;
		this.roundWidth = 0;
		this.roundHeight = 0;
	}

	@Override
	public void draw(Graphics2D g) {
		g.setColor(color);
		if (isFilled) {
			g.fillRoundRect((int)x, (int)y, (int)width, (int)height, roundWidth, roundHeight);
		}else {	
			g.drawRoundRect((int)x, (int)y, (int)width, (int)height, roundWidth, roundHeight);
		}
	}

	@Override
	public void edit() {
		JDialog editDialog = new JDialog(Main.f);
		editDialog.setLayout(new GridLayout(7, 1));
		editDialog.setTitle("Edit Rectangle");
		JPanel positionPanel = new JPanel(new GridLayout(1, 4));
		positionPanel.add(Main.theme.affect(new JLabel("X:")));
		JTextField xField = new JTextField(this.x + "");
		Main.theme.affect(xField);
		positionPanel.add(xField);
		positionPanel.add(Main.theme.affect(new JLabel("Y:")));
		JTextField yField = new JTextField(this.y + "");
		Main.theme.affect(yField);
		positionPanel.add(yField);
		editDialog.add(positionPanel);
		JPanel sizePanel = new JPanel(new GridLayout(1, 4));
		sizePanel.add(Main.theme.affect(new JLabel("Width:")));
		JTextField widthField = new JTextField(this.width + "");
		Main.theme.affect(widthField);
		sizePanel.add(widthField);
		sizePanel.add(Main.theme.affect(new JLabel("Height:")));
		JTextField heightField = new JTextField(this.height + "");
		Main.theme.affect(heightField);
		sizePanel.add(heightField);
		editDialog.add(sizePanel);
		JPanel colorPanel = new JPanel(new BorderLayout());
		colorPanel.add(Main.theme.affect(new JLabel("Color:")), Main.translator.getBeforeTextBorder());
		JLabel colorLabel = new JLabel();
		colorLabel.setOpaque(true);
		colorLabel.setBackground(color);
		colorPanel.add(colorLabel);
		JButton setColorButton = new JButton("Set Color");
		Main.theme.affect(setColorButton);
		setColorButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				colorLabel.setBackground(JColorChooser.showDialog(editDialog, "Choose Rectangle color", colorLabel.getBackground()));
			}
		});
		colorPanel.add(setColorButton, Main.translator.getAfterTextBorder());
		editDialog.add(colorPanel);
		LSlider roundWidthSlider = new LSlider("Round Width:", 0, (int) this.width, roundWidth);
		Main.theme.affect(roundWidthSlider);
		editDialog.add(roundWidthSlider);
		LSlider roundHeightSlider = new LSlider("Round Height:", 0, (int) this.height, roundHeight);
		Main.theme.affect(roundHeightSlider);
		editDialog.add(roundHeightSlider);
		JCheckBox isFilledCheckBox = new JCheckBox("Fill Rectangle", isFilled);
		Main.theme.affect(isFilledCheckBox);
		editDialog.add(isFilledCheckBox);
		JButton apply = new JButton("Apply");
		JButton preview = new JButton("Preview");
		Main.theme.affect(apply);
		apply.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				preview.getActionListeners()[0].actionPerformed(new ActionEvent(apply, 0, "apply"));
				editDialog.dispose();
			}
		});
		Main.theme.affect(preview);
		preview.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					double x = Double.parseDouble(xField.getText());
					double y = Double.parseDouble(yField.getText());
					double width = Double.parseDouble(widthField.getText());
					double height = Double.parseDouble(heightField.getText());
					Color color = colorLabel.getBackground();
					LinkedList<Change> changes = new LinkedList<>();
					if (Rectangle.this.x != x) {
						changes.add(new NumericalChange(Change.X_CHANGE, x - Rectangle.this.x));
					}
					if (Rectangle.this.y != y) {
						changes.add(new NumericalChange(Change.Y_CHANGE, y - Rectangle.this.y));
					}
					if (Rectangle.this.width != width) {
						changes.add(new NumericalChange(Change.WIDTH_CHANGE, width - Rectangle.this.width));
					}
					if (Rectangle.this.height != height) {
						changes.add(new NumericalChange(Change.HEIGHT_CHANGE, height - Rectangle.this.height));
					}
					if (!Rectangle.this.color.equals(color)) {
						changes.add(new ObjectChange(Change.RECTANGLE_COLOR_CHANGE, Rectangle.this.color, color));
					}
					if (Rectangle.this.roundWidth != roundWidthSlider.getValue()) {
						changes.add(new NumericalChange(Change.ROUND_WIDTH_CHANGE, roundWidthSlider.getValue() - Rectangle.this.roundWidth));
					}
					if (Rectangle.this.roundHeight != roundHeightSlider.getValue()) {
						changes.add(new NumericalChange(Change.ROUND_HEIGHT_CHANGE, roundHeightSlider.getValue() - Rectangle.this.roundHeight));
					}
					if (Rectangle.this.isFilled != isFilledCheckBox.isSelected()) {
						changes.add(new BooleanChange(Change.IS_FILLED_CHANGE, isFilledCheckBox.isSelected()));
					}
					
					if (!changes.isEmpty()) {
						OperationsManager.operate(new ChangesOperation(Rectangle.this, changes));
						Main.getLayersList().updateImage(Rectangle.this);
						Main.getBoard().repaint();
					}
				} catch (Exception e2) {
					LDialogs.showMessageDialog(Main.f, "Invalid input", "Error", LDialogs.ERROR_MESSAGE);
					e2.printStackTrace();
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
	public Rectangle(String[] data) {
		this(Double.parseDouble(data[0]), Double.parseDouble(data[1]),
				Boolean.parseBoolean(data[2]), data[3], Double.parseDouble(data[4]),
				Double.parseDouble(data[5]), new Color(Integer.parseInt(data[6])));
		this.isFilled = Boolean.parseBoolean(data[7]);
		this.roundWidth = Integer.parseInt(data[8]);
		this.roundHeight = Integer.parseInt(data[9]);
	}
	public Rectangle(String line) {
		this(line.split(","));
	}

	@Override
	public String encodeShape() {
		return super.encodeShape() + "," + color.getRGB() + "," + isFilled + "," + roundWidth
				 + "," + roundHeight;
	}
	
	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public int getRoundWidth() {
		return roundWidth;
	}

	public void setRoundWidth(int roundWidth) {
		this.roundWidth = roundWidth;
	}

	public int getRoundHeight() {
		return roundHeight;
	}

	public void setRoundHeight(int roundHeight) {
		this.roundHeight = roundHeight;
	}

	public boolean isFilled() {
		return isFilled;
	}

	public void setFilled(boolean isFilled) {
		this.isFilled = isFilled;
	}

}