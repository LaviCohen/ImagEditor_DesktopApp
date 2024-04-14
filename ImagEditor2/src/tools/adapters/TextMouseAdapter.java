package tools.adapters;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseEvent;

import drawables.Layer;
import drawables.shapes.Text;
import gui.components.Board;
import le.gui.dialogs.LDialogs;
import operatins.AddLayerOperation;
import operatins.OperationsManager;

public class TextMouseAdapter extends BoardAdapter{

	public TextMouseAdapter(Board parent) {
		super(parent);
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON3) {
			rightClick(e);
			return;
		}
		int x = boardToPaperCoordinatesX(e.getX());
		int y = boardToPaperCoordinatesY(e.getY());
		
		String text = LDialogs.showInputDialog(parent, "Enter Text:");
		
		Text t = new Text(
				x, y, true, null, Color.BLACK, new Font("Arial", Font.PLAIN, 20), text);
		OperationsManager.operate(new AddLayerOperation(new Layer(t)));
	}

}
