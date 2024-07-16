package drawables.shapes.abstractShapes;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JLabel;

import gui.components.EditPanel;
import le.gui.ColorTheme;
import main.Main;
import operatins.changes.Change;
import operatins.changes.ChangeType;
import operatins.changes.ObjectChange;

public interface ColoredShape {

	public Color getColor();
	public void setColor(Color c);
	
	public default EditPanel createColorPanel() {
		JLabel colorLabel = new JLabel();
		colorLabel.setOpaque(true);
		colorLabel.setBackground(getColor());
		colorLabel.setName(ColorTheme.DONT_AFFECT);
		JButton setColorButton = new JButton("Set Color");
		setColorButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Color c = JColorChooser.showDialog(null, "Choose Rectangle color", colorLabel.getBackground());
				if (c != null) {
					colorLabel.setBackground(c);
				}
			}
		});
		EditPanel colorPanel = new EditPanel(new BorderLayout()) {
			private static final long serialVersionUID = 1L;


			@Override
			public LinkedList<Change> getChanges() {
				LinkedList<Change> changes = new LinkedList<>();
				Color color = colorLabel.getBackground();
				if (!ColoredShape.this.getColor().equals(color)) {
					changes.add(new ObjectChange(ChangeType.COLOR_CHANGE, ColoredShape.this.getColor(),
							color));
				}
				return changes;
			}
		};
		colorPanel.add(new JLabel("Color:"), Main.translator.getBeforeTextBorder());
		colorPanel.add(colorLabel);
		colorPanel.add(setColorButton, Main.translator.getAfterTextBorder());
		return colorPanel;
	}
}
