package drawables.shapes;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

import javax.swing.JCheckBox;
import javax.swing.JDialog;

import drawables.shapes.abstractShapes.ColoredShape;
import drawables.shapes.abstractShapes.StretchableShpae;
import gui.components.EditPanel;
import le.gui.components.LSlider;
import le.gui.dialogs.LDialogs;
import main.Main;
import operatins.ChangesOperation;
import operatins.OperationsManager;
import operatins.changes.BooleanChange;
import operatins.changes.Change;
import operatins.changes.NumericalChange;
import operatins.changes.ObjectChange;

public class Rectangle extends StretchableShpae implements ColoredShape{

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
		EditPanel positionPanel = createPositionPanel();
		editDialog.add(positionPanel);
		EditPanel sizePanel = createSizePanel();
		editDialog.add(sizePanel);
		EditPanel colorPanel = createColorPanel();
		editDialog.add(colorPanel);
		LSlider roundWidthSlider = new LSlider("Round Width:", 0, (int) this.width, roundWidth);
		editDialog.add(roundWidthSlider);
		LSlider roundHeightSlider = new LSlider("Round Height:", 0, (int) this.height, roundHeight);
		editDialog.add(roundHeightSlider);
		JCheckBox isFilledCheckBox = new JCheckBox("Fill Rectangle", isFilled);
		editDialog.add(isFilledCheckBox);
		ActionListener actionListener =  new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Object[] positionData = positionPanel.getData();
					double x = (Double) positionData[0];
					double y = (Double) positionData[1];
					Object[] sizeData = sizePanel.getData();
					double width = (Double) sizeData[0];
					double height = (Double) sizeData[1];
					Color color = (Color) colorPanel.getData()[0];
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
				
				if (e.getActionCommand().equals("Apply & Close")) {
					editDialog.dispose();
				}
			}
		};
		editDialog.add(createActionPanel(actionListener));
		Main.theme.affect(editDialog);
		editDialog.pack();
		moveDialogToCorrectPos(editDialog);
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
	
	public static Rectangle createNewDefaultRectangle() {
		return new Rectangle(0, 0, true, null, 100, 100, Color.BLUE);
	}

}