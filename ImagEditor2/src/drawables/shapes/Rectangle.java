package drawables.shapes;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.util.LinkedList;

import javax.swing.JCheckBox;

import drawables.shapes.abstractShapes.ColoredShape;
import drawables.shapes.abstractShapes.Shape;
import drawables.shapes.abstractShapes.StretchableShpae;
import gui.components.EditPanel;
import le.gui.components.LSlider;
import le.gui.dialogs.LDialogs;
import main.Main;
import operatins.changes.BooleanChange;
import operatins.changes.Change;
import operatins.changes.ChangeType;
import operatins.changes.NumericalChange;

public class Rectangle extends Shape implements ColoredShape, StretchableShpae{

	Color color;
	
	double width;
	double height;
	
	int roundWidth;
	int roundHeight;
	
	
	boolean isFilled;
	
	public Rectangle(double x, double y, boolean visible, String name, double width, double height, Color color) {
		super(x, y, visible, name);
		this.width = width;
		this.height = height;
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
	public EditPanel getEditPanel(boolean full, boolean vertical) {
		
		EditPanel positionPanel = createPositionPanel();
		EditPanel sizePanel = createSizePanel();
		EditPanel colorPanel = createColorPanel();
		LSlider roundWidthSlider = new LSlider("Round Width:", 0, (int) this.width, (int)(roundWidth > width ? width : roundWidth));
		LSlider roundHeightSlider = new LSlider("Round Height:", 0, (int) this.height, (int)(roundHeight > height ? height : roundHeight));
		JCheckBox isFilledCheckBox = new JCheckBox("Fill Rectangle", isFilled);
		EditPanel editPanel = new EditPanel(new GridLayout(7, 1)) {
			
			private static final long serialVersionUID = 1L;

			@Override
			public Object[] getData() {
				return null;
			}
			
			@Override
			public LinkedList<Change> getChanges() {
				try {
					LinkedList<Change> changes = new LinkedList<>();
					changes.addAll(positionPanel.getChanges());
					changes.addAll(sizePanel.getChanges());
					changes.addAll(colorPanel.getChanges());
					
					
					if (Rectangle.this.roundWidth != roundWidthSlider.getValue()) {
						changes.add(new NumericalChange(ChangeType.ROUND_WIDTH_CHANGE, roundWidthSlider.getValue() - Rectangle.this.roundWidth));
					}
					if (Rectangle.this.roundHeight != roundHeightSlider.getValue()) {
						changes.add(new NumericalChange(ChangeType.ROUND_HEIGHT_CHANGE, roundHeightSlider.getValue() - Rectangle.this.roundHeight));
					}
					if (Rectangle.this.isFilled != isFilledCheckBox.isSelected()) {
						changes.add(new BooleanChange(ChangeType.IS_FILLED_CHANGE, isFilledCheckBox.isSelected()));
					}
					
					return changes;
				} catch (Exception e2) {
					LDialogs.showMessageDialog(Main.f, "Invalid input", "Error", LDialogs.ERROR_MESSAGE);
					e2.printStackTrace();
				}
				return null;
			}
		};
		editPanel.add(positionPanel);
		editPanel.add(sizePanel);
		editPanel.add(colorPanel);
		editPanel.add(roundWidthSlider);
		editPanel.add(roundHeightSlider);
		editPanel.add(isFilledCheckBox);
		return editPanel;
	}
	public Rectangle(String[] data) {
		this(Double.parseDouble(data[0]), Double.parseDouble(data[1]),
				Boolean.parseBoolean(data[2]), data[3], Double.parseDouble(data[4]),
				Double.parseDouble(data[5]), new Color(Integer.parseInt(data[6])));
		this.isFilled = Boolean.parseBoolean(data[7]);
		this.roundWidth = Integer.parseInt(data[8]);
		this.roundHeight = Integer.parseInt(data[9]);
	}
	
	@Override
	public void invalidateSize() {
		Main.getLayersList().getLayerForShape(this).adjustTopSize(getWidthOnBoard(), getHeightOnBoard());
	}
	
	public Rectangle(String line) {
		this(line.split(","));
	}

	@Override
	public String encodeShape() {
		return super.encodeShape() + ", " + width + ", " + height + "," + color.getRGB() + "," + isFilled + "," + roundWidth
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

	@Override
	public int getWidthOnBoard() {
		return (int) this.width;
	}

	@Override
	public int getHeightOnBoard() {
		return (int) this.height;
	}
}