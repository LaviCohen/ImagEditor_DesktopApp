package drawables.shapes.abstractShapes;

import java.awt.BorderLayout;
import java.util.LinkedList;

import javax.swing.JLabel;
import javax.swing.JTextArea;

import gui.components.EditPanel;
import main.Main;
import operatins.changes.Change;
import operatins.changes.ChangeType;
import operatins.changes.ObjectChange;

public interface TextualShape {
	
	
	public String getText();
	public void setText(String s);
	
	public default EditPanel createTextPanel(String title) {
		JTextArea textArea = new JTextArea(getText());
		EditPanel textPanel = new EditPanel(new BorderLayout()) {
			private static final long serialVersionUID = 1L;


			@Override
			public LinkedList<Change> getChanges() {
				String text = textArea.getText();
				LinkedList<Change> changes = new LinkedList<>();
				if (!TextualShape.this.getText().equals(text)) {
					changes.add(new ObjectChange(ChangeType.TEXT_CHANGE, TextualShape.this.getText(), text));
				}
				return changes;
			}
		};
		textPanel.add(Main.theme.affect(new JLabel(title)), 
				Main.translator.getBeforeTextBorder());
		textPanel.add(textArea);
		return textPanel;
	}
}
