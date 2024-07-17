package tools.adapters;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.LinkedList;

import javax.swing.JTextArea;

import drawables.shapes.Picture;
import drawables.shapes.Text;
import drawables.shapes.abstractShapes.Shape;
import drawables.shapes.abstractShapes.StretchableShpae;
import gui.components.Board;
import main.Main;
import operatins.ChangesOperation;
import operatins.OperationsManager;
import operatins.changes.Change;
import operatins.changes.ChangeType;
import operatins.changes.NumericalChange;
import operatins.changes.ObjectChange;

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
		if (shapeInFocus != text) {
			focusLost.run();
		}
		if (shapeInFocus != null && !shapeInFocus.isVisible()) {
			shapeInFocus = null;
		}
		if (shapeInFocus != null) {
			touchedWrapper = touchWrapper(e);
		}
		Main.getLayersList().setSelection(shapeInFocus);
	}
	private JTextArea ta;
	private String originalText;
	private Text text;
	public Runnable focusLost = new Runnable() {
		
		@Override
		public void run() {
			if (text == null) {
				return;
			}
			Main.getBoard().remove(ta);
			text.setVisible(true);
			if (!ta.getText().equals(originalText)) {
				LinkedList<Change> changes = new LinkedList<Change>();
				changes.add(new ObjectChange(ChangeType.TEXT_CHANGE, originalText, ta.getText()));
				OperationsManager.addOperation(new ChangesOperation(text, changes));
				Main.getLayersList().updateImage(text);
			}
			Main.getBoard().repaint();
			ta = null;
			originalText = null;
			text = null;
		}
	};
	public void editTextOnBoard(Text text) {
		this.text = text;
		editTextOnBoard();
	}
	private void editTextOnBoard() {
		System.out.println("Editing text on board");
		ta = new JTextArea(text.getText());
		ta.setFont(resizeFont(text.getFont(), parent.getZoomRate()));
		ta.setForeground(text.getColor());
		if (text.getColor().getRed() + text.getColor().getGreen() + text.getColor().getBlue() > 400) {
			ta.setBackground(Color.BLACK);
		}
		ta.setMargin(new Insets(0, 0, 0, 0));
		Main.getBoard().add(ta);
		originalText = text.getText();
		ta.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				super.keyTyped(e);
				System.out.println(e.getKeyChar());
				text.setText(ta.getText());
				ta.setBounds(parent.paperToBoardCoordinatesX((int) text.getX()),
						parent.paperToBoardCoordinatesY((int) text.getY()), 
						ta.getPreferredSize().width + 15, ta.getPreferredSize().height);
				Main.getBoard().repaint();
			}
		});
		ta.requestFocus();
		ta.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				super.focusLost(e);
				focusLost.run();
			}
		});
		text.setVisible(false);
		ta.setBounds(parent.paperToBoardCoordinatesX((int) text.getX()),
				parent.paperToBoardCoordinatesY((int) text.getY()), 
				ta.getPreferredSize().width + 15, ta.getPreferredSize().height);
		Main.getBoard().revalidate();
		Main.getBoard().repaint();
	}
	public Font resizeFont(Font f, double sizeFactor) {
		return new Font(f.getName(), f.getStyle(), (int) (f.getSize() * sizeFactor));
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		if (shapeInFocus != null) {
			LinkedList<Change> changes = new LinkedList<Change>();
			if (totalMovementX != 0) {
				changes.add(new NumericalChange(ChangeType.X_CHANGE, totalMovementX));
			}
			if (totalMovementY != 0) {
				changes.add(new NumericalChange(ChangeType.Y_CHANGE, totalMovementY));
			}
			if (totalStretchX != 0) {
				changes.add(new NumericalChange(ChangeType.WIDTH_CHANGE, totalStretchX));
			}
			if (totalStretchY != 0) {
				changes.add(new NumericalChange(ChangeType.HEIGHT_CHANGE, totalStretchY));
			}
			if (totalCutFromBottom != 0) {
				System.out.println("Bottom: " + totalCutFromBottom);
				changes.add(new NumericalChange(ChangeType.CUT_FROM_BOTTOM_CHANGE, totalCutFromBottom));
			}
			if (totalCutFromTop != 0) {
				System.out.println("Top: " + totalCutFromTop);
				changes.add(new NumericalChange(ChangeType.CUT_FROM_TOP_CHANGE, totalCutFromTop));
			}
			if (totalCutFromRight != 0) {
				System.out.println("Right: " + totalCutFromRight);
				changes.add(new NumericalChange(ChangeType.CUT_FROM_RIGHT_CHANGE, totalCutFromRight));
			}
			if (totalCutFromLeft != 0) {
				System.out.println("Left: " + totalCutFromLeft);
				changes.add(new NumericalChange(ChangeType.CUT_FROM_LEFT_CHANGE, totalCutFromLeft));
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
		shapeInFocus = getShapeAt(e);
		if (e.getButton() == MouseEvent.BUTTON3) {
			super.rightClick(shapeInFocus, e);
		}
		if(e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1 
				&& shapeInFocus instanceof Text){
			editTextOnBoard((Text) shapeInFocus);
		}
		parent.repaint();
	}
	@Override
	public void mouseDragged(MouseEvent e) {
		if (shapeInFocus != null) {
			movementInX = (int)((e.getXOnScreen() - previousX) * 100 / Main.getZoomSlider().getValue());
			movementInY = (int)((e.getYOnScreen() - previousY) * 100 / Main.getZoomSlider().getValue());
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
					totalCutFromBottom -= movementInY;
					break;
				case BOTTOM_RIGHT_WRAPPER:
					p.addToCutFromRight(-movementInX);
					totalCutFromRight -= movementInX;
					p.addToCutFromBottom(-movementInY);
					totalCutFromBottom -= movementInY;
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
