package tools.adapters;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;

import drawables.Layer;
import gui.components.board.Board;
import main.Main;

public class EraserMouseAdapter extends BoardAdapter {

	protected static int eraserSize = 5;
	
	public EraserMouseAdapter(Board parent) {
		super(parent);
	}
	
	private void paintWithEraser(MouseEvent e) {
		Layer layer = Main.getLayersList().getSelectedLayer();
		
		if (layer != null) {
			if (layer.getTop() == null) {
				layer.initTop();
			}
			Graphics2D g = layer.getTop().createGraphics();
			g.setBackground(new Color(255, 0, 0, 0));
			g.clearRect(boardToPaperCoordinatesX(e.getX()) - (int) layer.getShape().getX() - eraserSize/2, 
					boardToPaperCoordinatesY(e.getY() - (int) layer.getShape().getY()) - eraserSize/2, 
					eraserSize, eraserSize);
			parent.repaint();
			Main.layersList.updateImage(layer.getShape());
		}
		
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (e.getButton() == 0) {	
			paintWithEraser(e);
		}
	}
	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getButton() == 0) {	
			paintWithEraser(e);
		} else if (e.getButton() == MouseEvent.BUTTON3) {
			openAddShapePopupMenu(e);
		}
	}

	public static int getEraserSize() {
		return eraserSize;
	}

	public static void setEraserSize(int eraserSize) {
		EraserMouseAdapter.eraserSize = eraserSize;
	}
}
