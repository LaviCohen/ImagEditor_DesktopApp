package drawables.shapes.abstractShapes;

import java.awt.GridLayout;
import java.util.LinkedList;

import javax.swing.JLabel;
import javax.swing.JTextField;

import gui.components.EditPanel;
import operatins.changes.Change;
import operatins.changes.ChangeType;
import operatins.changes.NumericalChange;

public interface StretchableShpae{
	
	
	public default void strecthBy(int widthDiff, int heightDiff) {
		setWidth(getWidth() + widthDiff);
		setHeight(getHeight() + heightDiff);
		invalidateSize();
	}
	
	public double getWidth();
	public void setWidth(double width);
	public double getHeight();
	public void setHeight(double height);
	
	public void invalidateSize();
	
	public default EditPanel createSizePanel() {
		JTextField widthField = new JTextField(this.getWidth() + "");
		JTextField heightField = new JTextField(this.getHeight() + "");
		EditPanel sizePanel = new EditPanel(new GridLayout(1, 4)) {
			private static final long serialVersionUID = 1L;

			@Override
			public LinkedList<Change> getChanges() {
				double width = Double.parseDouble(widthField.getText());
				double height = Double.parseDouble(heightField.getText());
				LinkedList<Change> changes = new LinkedList<>();
				if (StretchableShpae.this.getWidth() != width) {
					changes.add(new NumericalChange(ChangeType.WIDTH_CHANGE, width - StretchableShpae.this.getWidth()));
				}
				if (StretchableShpae.this.getHeight() != height) {
					changes.add(new NumericalChange(ChangeType.HEIGHT_CHANGE, height - StretchableShpae.this.getHeight()));
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
