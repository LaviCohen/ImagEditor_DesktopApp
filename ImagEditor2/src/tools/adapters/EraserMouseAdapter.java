package tools.adapters;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

import drawables.Layer;
import gui.components.Board;
import le.utils.PictureUtilities;
import main.Main;
import operatins.ChangesOperation;
import operatins.OperationsManager;
import operatins.changes.Change;
import operatins.changes.ObjectChange;

public class EraserMouseAdapter extends BoardAdapter {

	protected static int eraserSize = 5;
	
	protected BufferedImage lastTop = null;
	
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
			Main.getLayersList().updateImage(layer.getShape());
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
	
	@Override
	public void mousePressed(MouseEvent e) {
		Layer layer = Main.getLayersList().getSelectedLayer();
		if (layer != null) {
			lastTop = PictureUtilities.copy(layer.getTop());
		}	
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		LinkedList<Change> list = new LinkedList<Change>();
		list.add(new ObjectChange(Change.LAYER_TOP_CHANGE, lastTop, 
				PictureUtilities.copy(Main.getLayersList().getSelectedLayer().getTop())));
		OperationsManager.addOperation(new ChangesOperation(
				Main.getLayersList().getSelectedLayer().getShape(), list));
	}

	public static int getEraserSize() {
		return eraserSize;
	}

	public static void setEraserSize(int eraserSize) {
		EraserMouseAdapter.eraserSize = eraserSize;
	}
}
