package tools.adapters;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.MouseEvent;

import drawables.Layer;
import drawables.shapes.Rectangle;
import gui.components.Board;
import main.Main;
import operatins.AddLayerOperation;
import operatins.OperationsManager;

public class RectMouseAdapter extends BoardAdapter{
	
	public RectMouseAdapter(Board parent) {
		super(parent);
	}
	
	
	public Rectangle currentRect;
	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON3) {
			rightClick(e);
			return;
		}
		int x = boardToPaperCoordinatesX(e.getX());
		int y = boardToPaperCoordinatesY(e.getY());
		
		currentRect = new Rectangle(x, y, true, "Rect", 1, 1, Color.BLUE);
		
		Main.getBoard().addLayer(new Layer(currentRect));
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		Point onPaperCoordinates = boardToPaperCoordinatesPoint(e.getPoint());
		
		currentRect.setWidth(onPaperCoordinates.getX() - currentRect.getX());
		currentRect.setHeight(onPaperCoordinates.getY() - currentRect.getY());
		currentRect.invalidateSize();
		
		parent.repaint();
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		OperationsManager.addOperation(new AddLayerOperation(Main.getLayersList().getLayerForShape(currentRect)));
		currentRect = null;
	}
}
