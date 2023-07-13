package drawables.shapes.abstractShapes;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JLabel;

import gui.components.EditPanel;
import main.Main;

public interface ColoredShape {

	public Color getColor();
	public void setColor(Color c);
	
	public default EditPanel createColorPanel() {
		JLabel colorLabel = new JLabel();
		colorLabel.setOpaque(true);
		colorLabel.setBackground(getColor());
		JButton setColorButton = new JButton("Set Color");
		Main.theme.affect(setColorButton);
		setColorButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				colorLabel.setBackground(JColorChooser.showDialog(null, "Choose Rectangle color", colorLabel.getBackground()));
			}
		});
		EditPanel colorPanel = new EditPanel(new BorderLayout()) {
			private static final long serialVersionUID = 1L;

			@Override
			public Object[] getData() {
				return new Color[] {colorLabel.getBackground()};
			}
		};
		colorPanel.add(Main.theme.affect(new JLabel("Color:")), Main.translator.getBeforeTextBorder());
		colorPanel.add(colorLabel);
		colorPanel.add(setColorButton, Main.translator.getAfterTextBorder());
		
		return colorPanel;
	}
	
}
