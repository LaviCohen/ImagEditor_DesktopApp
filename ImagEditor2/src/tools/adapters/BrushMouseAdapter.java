package tools.adapters;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

import drawables.Layer;
import gui.components.Board;
import install.Preferences;
import le.utils.PictureUtilities;
import main.Main;
import operatins.ChangesOperation;
import operatins.OperationsManager;
import operatins.changes.Change;
import operatins.changes.ObjectChange;

public class BrushMouseAdapter extends BoardAdapter{

	protected static Color brushColor = Color.BLACK;
	
	protected static int brushSize = 5;
	
	protected int lastX = -100;
	protected int lastY = -100;
	
	protected BufferedImage lastTop = null;
	
	public BrushMouseAdapter(Board parent) {
		super(parent);
	}
	
	private void paintWithBrush(MouseEvent e) {
		Layer layer = Main.getLayersList().getSelectedLayer();
		
		if (layer != null) {
			if (layer.getTop() == null) {
				layer.initTop();
			}
			Graphics2D g = layer.getTop().createGraphics();
			g.setColor(brushColor);
			int x = boardToPaperCoordinatesX(e.getX()) - (int) layer.getShape().getX();
			int y = boardToPaperCoordinatesY(e.getY()) - (int) layer.getShape().getY();
			g.fillOval(x - brushSize/2, y - brushSize/2, brushSize, brushSize);
			if (lastX != -100 && lastY != -100) {
				System.out.println("Drawing Connector from " + lastX + ", " + lastY);
				drawConnectionLine(lastX, lastY, x, y, g);
			}
			lastX = x;
			lastY = y;
			parent.repaint();
			Main.getLayersList().updateImage(layer.getShape());
		}
		
	}
	
	private void drawConnectionLine(int lastX, int lastY, int x, int y, Graphics2D g) {
		if (lastX == x) {
			//Orientation lock, no incline, private case
			System.out.println("Filling Rect");
			g.fillRect(lastX - brushSize/2, lastY, brushSize, y - lastY);
			return;
		}
		double incline = ((double)lastY - y)/((double)lastX - x);
		
		double deg = Math.atan(incline);
		
		int diffX = -(int)(Math.sin(deg) * brushSize/2);
		int diffY = (int)(Math.cos(deg) * brushSize/2);

		Polygon p = new Polygon();
		p.addPoint(lastX + diffX, lastY + diffY);
		p.addPoint(x + diffX, y + diffY);
		p.addPoint(x - diffX, y - diffY);
		p.addPoint(lastX - diffX, lastY - diffY);
		g.fillPolygon(p);
		//My coolest ever debugging, worth keeping.
//		System.out.println(String.format("%d, %d, %d, %d", lastX + diffX, lastY + diffY, x + diffX, y + diffY));
//		System.out.println(String.format("%d, %d, %d, %d", lastX - diffX, lastY - diffY, x - diffX, y - diffY));
//		g.setColor(Color.red);
//		g.drawLine(lastX + diffX, lastY + diffY, x + diffX, y + diffY);
//		g.drawLine(lastX - diffX, lastY - diffY, x - diffX, y - diffY);
//		g.setColor(Color.GREEN);
//		g.drawOval(lastX + diffX, lastY + diffY, 10, 10);
//		g.drawOval(x + diffX, y + diffY, 10, 10);
//		g.drawOval(lastX - diffX, lastY - diffY, 10, 10);
//		g.drawOval(x - diffX, y - diffY, 10, 10);
//		g.setColor(brushColor);
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

	@Override
	public void mousePressed(MouseEvent e) {
		Layer layer = Main.getLayersList().getSelectedLayer();
		if (layer != null) {
			lastX = boardToPaperCoordinatesX(e.getX()) - (int) layer.getShape().getX();
			lastY = boardToPaperCoordinatesX(e.getY()) - (int) layer.getShape().getY();
			if (Preferences.keepTrackOfTopLayers) {
				lastTop = PictureUtilities.copy(layer.getTop());
			}
		}	
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		if (Preferences.keepTrackOfTopLayers) {
			LinkedList<Change> list = new LinkedList<Change>();
			BufferedImage newTop = null;
			if (Main.getLayersList().getSelectedLayer().getTop() != null) {
				newTop = PictureUtilities.copy(Main.getLayersList().getSelectedLayer().getTop());
			}
			list.add(new ObjectChange(Change.LAYER_TOP_CHANGE, lastTop, newTop));
			OperationsManager.addOperation(new ChangesOperation(
					Main.getLayersList().getSelectedLayer().getShape(), list));
		}
		lastX = -100;
		lastY = -100;
	}
	
	public static Color getBrushColor() {
		return brushColor;
	}

	public static void setBrushColor(Color brushColor) {
		BrushMouseAdapter.brushColor = brushColor;
	}

	public static int getBrushSize() {
		return brushSize;
	}

	public static void setBrushSize(int brushSize) {
		BrushMouseAdapter.brushSize = brushSize;
	}
}
