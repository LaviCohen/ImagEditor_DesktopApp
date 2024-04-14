package tools.adapters;

import java.awt.event.MouseEvent;
import java.util.LinkedList;

import drawables.shapes.abstractShapes.Shape;
import gui.components.Board;

public class GroupMouseAdapter extends BoardAdapter {

	private LinkedList<Shape> selected = new LinkedList<>();
	
	public GroupMouseAdapter(Board parent) {
		super(parent);
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON3) {
			rightClick(e);
			return;
		}
		Shape s = parent.getShapeAt(boardToPaperCoordinatesX(e.getX()), boardToPaperCoordinatesY(e.getY()));
		if (s != null) {
			if (!selected.contains(s)) {
				selected.add(s);
			} else {
				selected.remove(s);
			}
		}
	}

	public LinkedList<Shape> getSelected() {
		return selected;
	}
	
	
}
