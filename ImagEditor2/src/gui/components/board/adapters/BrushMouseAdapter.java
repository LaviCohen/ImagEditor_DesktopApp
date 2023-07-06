package gui.components.board.adapters;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;

import drawables.Layer;
import gui.components.board.Board;
import main.Main;

public class BrushMouseAdapter extends BoardMouseAdapter{

	protected Color brushColor = Color.BLACK;
	
	protected int brushSize = 5;
	
	public BrushMouseAdapter(Board parent) {
		super(parent);
	}
	
	private void paintWithBrush(MouseEvent e) {
		Layer layer = Main.getLayersList().getSelectedLayer();
		
		if (layer != null) {
			if (layer.getTop() == null) {
				layer.initTop();
			}
			Graphics g = layer.getTop().getGraphics();
			g.setColor(brushColor);
			g.fillOval(
					screenToBoardCoordsX(e.getX()) - (int) layer.getShape().getX() - brushSize/2, 
					screenToBoardCoordsY(e.getY() - (int) layer.getShape().getY()) - brushSize/2, 
					brushSize, brushSize);
			parent.repaint();
			Main.layersList.updateImage(layer.getShape());
		}
		
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (e.getButton() == 0) {	
			paintWithBrush(e);
		}
	}
	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getButton() == 0) {	
			paintWithBrush(e);
		} else if (e.getButton() == MouseEvent.BUTTON3) {
			openAddShapePopupMenu(e);
		}
	}

	public Color getBrushColor() {
		return brushColor;
	}

	public void setBrushColor(Color brushColor) {
		this.brushColor = brushColor;
	}

	public int getBrushSize() {
		return brushSize;
	}

	public void setBrushSize(int brushSize) {
		this.brushSize = brushSize;
	}
	
	
	
}
