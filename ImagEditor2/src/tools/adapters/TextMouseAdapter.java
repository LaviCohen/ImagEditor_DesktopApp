package tools.adapters;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseEvent;

import drawables.Layer;
import drawables.shapes.Text;
import gui.components.board.Board;
import operatins.AddLayerOperation;
import operatins.OperationsManager;

public class TextMouseAdapter extends BoardMouseAdapter {

	public TextMouseAdapter(Board parent) {
		super(parent);
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		int x = boardToPaperCoordinatesX(e.getX());
		int y = boardToPaperCoordinatesY(e.getY());
		
		Text t = new Text(
				x, y, true, null, Color.BLACK, new Font("Arial", Font.PLAIN, 20), "Text");
		OperationsManager.operate(new AddLayerOperation(new Layer(t)));
		t.edit();
	}

}
