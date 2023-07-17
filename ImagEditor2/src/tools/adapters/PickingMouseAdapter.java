package tools.adapters;

import java.awt.event.MouseEvent;
import java.util.LinkedList;

import drawables.shapes.Picture;
import drawables.shapes.Text;
import drawables.shapes.abstractShapes.Shape;
import drawables.shapes.abstractShapes.StretchableShpae;
import gui.components.Board;
import le.gui.dialogs.LDialogs;
import main.Main;
import operatins.ChangesOperation;
import operatins.OperationsManager;
import operatins.changes.Change;
import operatins.changes.NumericalChange;

public class PickingMouseAdapter extends BoardAdapter{
	
	public static final int TOP_LEFT_WRAPPER = 1;
	public static final int TOP_RIGHT_WRAPPER = 2;
	public static final int BOTTOM_LEFT_WRAPPER = 3;
	public static final int BOTTOM_RIGHT_WRAPPER = 4;
	
	Shape shapeInFocus = null;
	
	
	int previousX = 0;
	int previousY = 0;
	double totalMovementX = 0;
	double totalMovementY = 0;
	double totalStretchX = 0;
	double totalStretchY = 0;
	double totalCutFromBottom = 0;
	double totalCutFromTop = 0;
	double totalCutFromRight = 0;
	double totalCutFromLeft = 0;
	int movementInX = 0;
	int movementInY = 0;
	public int touchedWrapper = 0;
	
	public PickingMouseAdapter(Board parent) {
		super(parent);
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		previousX = e.getXOnScreen();
		previousY = e.getYOnScreen();
		shapeInFocus = parent.getShapeAt(boardToPaperCoordinatesX(e.getX()), boardToPaperCoordinatesY(e.getY()));
		if (shapeInFocus != null && !shapeInFocus.isVisible()) {
			shapeInFocus = null;
		}
		if (shapeInFocus != null) {
			touchedWrapper = touchWrapper(e);
		}
		Main.getLayersList().setSelection(shapeInFocus);
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		if (shapeInFocus != null) {
			LinkedList<Change> changes = new LinkedList<Change>();
			if (totalMovementX != 0) {
				changes.add(new NumericalChange(Change.X_CHANGE, totalMovementX));
			}
			if (totalMovementY != 0) {
				changes.add(new NumericalChange(Change.Y_CHANGE, totalMovementY));
			}
			if (totalStretchX != 0) {
				changes.add(new NumericalChange(Change.WIDTH_CHANGE, totalStretchX));
			}
			if (totalStretchY != 0) {
				changes.add(new NumericalChange(Change.HEIGHT_CHANGE, totalStretchY));
			}
			if (totalCutFromBottom != 0) {
				changes.add(new NumericalChange(Change.CUT_FROM_BOTTOM_CHANGE, totalCutFromBottom));
			}
			if (totalCutFromTop != 0) {
				changes.add(new NumericalChange(Change.CUT_FROM_TOP_CHANGE, totalCutFromTop));
			}
			if (totalCutFromRight != 0) {
				changes.add(new NumericalChange(Change.CUT_FROM_RIGHT_CHANGE, totalCutFromRight));
			}
			if (totalCutFromLeft != 0) {
				changes.add(new NumericalChange(Change.CUT_FROM_LEFT_CHANGE, totalCutFromLeft));
			}
			
			if (!changes.isEmpty()) {
				OperationsManager.addOperation(new ChangesOperation(shapeInFocus, changes));
			}
			
		}
		previousX = 0;
		previousY = 0;
		totalMovementX = 0;
		totalMovementY = 0;
		totalStretchX = 0;
		totalStretchY = 0;
		totalCutFromBottom = 0;
		totalCutFromTop= 0;
		totalCutFromRight = 0;
		totalCutFromLeft = 0;
		movementInX = 0;
		movementInY = 0;
		shapeInFocus = null;
		parent.repaint();
	}
	@Override
	public void mouseClicked(MouseEvent e) {
		shapeInFocus = parent.getShapeAt(boardToPaperCoordinatesX(e.getX()), boardToPaperCoordinatesY(e.getY()));
		if (shapeInFocus != null && !shapeInFocus.isVisible()) {
			shapeInFocus = null;
		}
		if (e.getButton() == MouseEvent.BUTTON3) {
			if (shapeInFocus != null) {
				shapeInFocus.getPopupMenuForShape().show(parent, e.getX(), e.getY());
			} else {
				openAddShapePopupMenu(e);
			}
		}
		if(e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1 
				&& shapeInFocus instanceof Text){
			String text = LDialogs.
					showInputDialog(parent, "Enter Text:", ((Text)shapeInFocus).getText());
			if (text != null && !text.equals(((Text)shapeInFocus).getText())) {
				((Text)shapeInFocus).setText(text);
				Main.getLayersList().updateImage(shapeInFocus);
			}
		}
		parent.repaint();
	}
	@Override
	public void mouseDragged(MouseEvent e) {
		if (shapeInFocus != null) {
			movementInX = (int)(e.getXOnScreen() - previousX) * 100 / Main.getZoomSlider().getValue();
			movementInY = (int)(e.getYOnScreen() - previousY) * 100 / Main.getZoomSlider().getValue();
			if(shapeInFocus instanceof Picture && ((Picture)shapeInFocus).isCutting() && touchedWrapper != 0) {
				Picture p = ((Picture)shapeInFocus);
				switch(touchedWrapper){
				case TOP_LEFT_WRAPPER:
					p.addToCutFromLeft(movementInX);
					totalCutFromLeft += movementInX;
					p.addToCutFromTop(movementInY);
					totalCutFromTop += movementInY;
					break;
				case TOP_RIGHT_WRAPPER:
					p.addToCutFromRight(-movementInX);
					totalCutFromRight -= movementInX;
					p.addToCutFromTop(movementInY);
					totalCutFromTop += movementInY;
					break;
				case BOTTOM_LEFT_WRAPPER:
					p.addToCutFromLeft(movementInX);
					totalCutFromLeft += movementInX;
					p.addToCutFromBottom(-movementInY);
					totalCutFromBottom -= movementInX;
					break;
				case BOTTOM_RIGHT_WRAPPER:
					p.addToCutFromRight(-movementInX);
					totalCutFromRight -= movementInX;
					p.addToCutFromBottom(-movementInY);
					totalCutFromBottom += movementInY;
					break;
				}
				p.invalidate();
			}else {
				switch(touchedWrapper){
				case 0:
					shapeInFocus.setX(shapeInFocus.getX() + movementInX);
					totalMovementX += movementInX;
					shapeInFocus.setY(shapeInFocus.getY() + movementInY);
					totalMovementY += movementInY;
					break;
				case TOP_LEFT_WRAPPER:
					shapeInFocus.setX(shapeInFocus.getX() + movementInX);
					totalMovementX += movementInX;
					shapeInFocus.setY(shapeInFocus.getY() + movementInY);
					totalMovementY += movementInY;
					if (shapeInFocus instanceof StretchableShpae) {
						((StretchableShpae)shapeInFocus).strecthBy(-movementInX, -movementInY);
						totalStretchX -= movementInX;
						totalStretchY -= movementInY;
					}
					break;
				case TOP_RIGHT_WRAPPER:
					shapeInFocus.setY(shapeInFocus.getY() + movementInY);
					totalMovementY += movementInY;
					if (shapeInFocus instanceof StretchableShpae) {
						((StretchableShpae)shapeInFocus).strecthBy(movementInX, -movementInY);
						totalStretchX += movementInX;
						totalStretchY -= movementInY;
					}
					break;
				case BOTTOM_LEFT_WRAPPER:
					shapeInFocus.setX(shapeInFocus.getX() + movementInX);
					totalMovementX += movementInX;
					if (shapeInFocus instanceof StretchableShpae) {
						((StretchableShpae)shapeInFocus).strecthBy(-movementInX, movementInY);
						totalStretchX -= movementInX;
						totalStretchY += movementInY;
					}
					break;
				case BOTTOM_RIGHT_WRAPPER:
					if (shapeInFocus instanceof StretchableShpae) {
						((StretchableShpae)shapeInFocus).strecthBy(movementInX, movementInY);
						if(shapeInFocus instanceof Picture) {
							((Picture)shapeInFocus).invalidate();
						}
						totalStretchX += movementInX;
						totalStretchY += movementInY;
					}
					break;
				}
			}
			previousX = e.getXOnScreen();
			previousY = e.getYOnScreen();
			Main.getBoard().repaint();
		}
	}
	public int touchWrapper(MouseEvent e) {
		int x = boardToPaperCoordinatesX(e.getX());
		int y = boardToPaperCoordinatesY(e.getY());
		
		int wrapperWidth = (int) (9 / parent.getZoomRate());
		int wrapperHeight = (int) (9 / parent.getZoomRate());
		
		if (isBetween(shapeInFocus.getX(), x, wrapperHeight) && 
				isBetween(shapeInFocus.getY(), y, wrapperHeight)) {
			return TOP_LEFT_WRAPPER;
		}
		if (isBetween(shapeInFocus.getX() + shapeInFocus.getWidthOnBoard(), x, -wrapperWidth) && 
				isBetween(shapeInFocus.getY(), y, wrapperHeight)) {
			return TOP_RIGHT_WRAPPER;
		}
		if (isBetween(shapeInFocus.getX(), x, wrapperWidth) && 
				isBetween(shapeInFocus.getY() + shapeInFocus.getHeightOnBoard(), y, -wrapperHeight)) {
			return BOTTOM_LEFT_WRAPPER;
		}
		if (isBetween(shapeInFocus.getX() + shapeInFocus.getWidthOnBoard(), x, -wrapperWidth) && 
				isBetween(shapeInFocus.getY() + shapeInFocus.getHeightOnBoard(), y, -wrapperHeight)) {
			return BOTTOM_RIGHT_WRAPPER;
		}
		
		return 0;
	}
}
