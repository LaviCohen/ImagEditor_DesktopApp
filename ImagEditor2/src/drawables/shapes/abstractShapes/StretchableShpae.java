package drawables.shapes.abstractShapes;

import java.awt.GridLayout;
import java.util.LinkedList;

import javax.swing.JLabel;
import javax.swing.JTextField;

import gui.components.EditPanel;
import main.Main;
import operatins.changes.Change;
import operatins.changes.NumericalChange;

public abstract class StretchableShpae extends Shape{
	
	protected double width;
	protected double height;
	
	public StretchableShpae(double x, double y, boolean visible, String name, double width, double height) {
		super(x, y, visible, name);
		this.width = width;
		this.height = height;
	}

	public void strecthBy(int widthDiff, int heightDiff) {
		this.width += widthDiff;
		this.height += heightDiff;
		Main.getLayersList().getLayerForShape(this).adjustTopSize((int)this.width, (int)this.height);
	}
	
	@Override
	public String encodeShape() {
		return super.encodeShape() + "," + width + "," + height;
	}
	
	@Override
	public int getWidthOnBoard() {
		return (int)getWidth();
	}
	@Override
	public int getHeightOnBoard() {
		return (int)getHeight();
	}
	public double getWidth() {
		return width;
	}
	public void setWidth(double width) {
		this.width = width;
		Main.getLayersList().getLayerForShape(this).adjustTopSize((int)this.width, (int)this.height);
	}
	public double getHeight() {
		return height;
	}
	public void setHeight(double height) {
		this.height = height;
		Main.getLayersList().getLayerForShape(this).adjustTopSize((int)this.width, (int)this.height);
	}
	
	protected EditPanel createSizePanel() {
		JTextField widthField = new JTextField(this.width + "");
		JTextField heightField = new JTextField(this.height + "");
		EditPanel sizePanel = new EditPanel(new GridLayout(1, 4)) {
			
			private static final long serialVersionUID = 1L;

			@Override
			public Object[] getData() {
				return new Double[] {Double.parseDouble(widthField.getText()), 
						Double.parseDouble(heightField.getText())};
			}

			@Override
			public LinkedList<Change> getChanges() {
				Object[] sizeData = getData();
				double width = (Double) sizeData[0];
				double height = (Double) sizeData[1];
				LinkedList<Change> changes = new LinkedList<>();
				if (StretchableShpae.this.width != width) {
					changes.add(new NumericalChange(Change.WIDTH_CHANGE, width - StretchableShpae.this.width));
				}
				if (StretchableShpae.this.height != height) {
					changes.add(new NumericalChange(Change.HEIGHT_CHANGE, height - StretchableShpae.this.height));
				}
				
				return changes;
			}
		};
		sizePanel.add(new JLabel("Width:"));
		sizePanel.add(widthField);
		sizePanel.add(new JLabel("Height:"));
		sizePanel.add(heightField);
		return sizePanel;
	}
}
