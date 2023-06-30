package gui.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import le.gui.dialogs.LDialogs;
import main.Main;
import operatins.ChangesOperation;
import operatins.OperationsManager;
import operatins.changes.Change;
import operatins.changes.NumericalChange;
import shapes.Picture;
import shapes.Text;
import shapes.abstractShapes.Shape;
import shapes.abstractShapes.StretchableShpae;

public class Board extends JPanel{
	private static final long serialVersionUID = 1L;
	
	public JLabel displayLabel;
	public Graphics2D g;
	public Color backgroundColor;
	public BufferedImage paper;
	public LinkedList<Shape> shapes = new LinkedList<Shape>();
	
	public boolean inited = false;
	
	public Board(Color color, int width, int height) {
		this.setLayout(new BorderLayout());
		this.backgroundColor = color;
		paper = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		displayLabel = new JLabel(new ImageIcon(paper));
		this.add(displayLabel, BorderLayout.CENTER);
		g = paper.createGraphics();
		inited = true;
		MouseAdapter mouseListener = new MouseAdapter() {
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
			
			@Override
			public void mousePressed(MouseEvent e) {
				previousX = e.getXOnScreen();
				previousY = e.getYOnScreen();
				shapeInFocus = getShapeAt(
						(int)((e.getX() - getLeftGap()) / getZoomRate()),
						(int)((e.getY() - getTopGap()) / getZoomRate()));
				if (shapeInFocus != null && !shapeInFocus.isVisible()) {
					shapeInFocus = null;
				}
				if (shapeInFocus != null) {
					touchedWrapper = touchWrapper(e);
				}
				Main.getShapeList().setSelection(shapeInFocus);
			}
			private double getTopGap() {
				return ((Board.this.getHeight() - (Board.this.paper.getHeight() * getZoomRate()))/2);
			}
			private double getLeftGap() {
				return ((Board.this.getWidth()  - (Board.this.paper.getWidth()  * getZoomRate()))/2);
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				if (shapeInFocus != null) {
					LinkedList<Change> changes = new LinkedList<Change>();
					System.out.println("Total Movment: " + totalMovementX + ", " + totalMovementY);
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
						OperationsManager.addOperation(new ChangesOperation(shapeInFocus, 
								changes.toArray(new Change[0])));
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
				Board.this.repaint();
			}
			@Override
			public void mouseClicked(MouseEvent e) {
				shapeInFocus = getShapeAt(screenToBoardCoordsX(e.getX()), screenToBoardCoordsY(e.getY()));
				if (shapeInFocus != null && !shapeInFocus.isVisible()) {
					shapeInFocus = null;
				}
				if (e.getButton() == MouseEvent.BUTTON3) {
					if (shapeInFocus != null) {
						shapeInFocus.getPopupMenuForShape().show(Board.this, e.getX(), e.getY());
					}
				}
				if(e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1 
						&& shapeInFocus instanceof Text){
					String text = LDialogs.
							showInputDialog(Board.this, "Enter Text:", ((Text)shapeInFocus).getText());
					if (text != null && !text.equals(((Text)shapeInFocus).getText())) {
						((Text)shapeInFocus).setText(text);
						Main.getShapeList().updateImage(shapeInFocus);
					}
				}
				Board.this.repaint();
			}
			public static final int TOP_LEFT_WRAPPER = 1;
			public static final int TOP_RIGHT_WRAPPER = 2;
			public static final int BOTTOM_LEFT_WRAPPER = 3;
			public static final int BOTTOM_RIGHT_WRAPPER = 4;
			public int touchedWrapper = 0;
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
						p.lastDrawn = null;
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
			public int screenToBoardCoordsX(int screenX) {
				return (int)((screenX - getLeftGap()) / getZoomRate());
			}
			public int screenToBoardCoordsY(int screenY) {
				return (int)((screenY - getTopGap()) / getZoomRate());
			}
			public int touchWrapper(MouseEvent e) {
				int x = screenToBoardCoordsX(e.getX());
				int y = screenToBoardCoordsY(e.getY());
				
				int wrapperWidth = 9;
				int wrapperHeight = 9;
				
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
			/**
			 * Return if value is between start & start + difference
			 * */
			public boolean isBetween(double start, double value, double difference) {
				if (difference < 0) {
					start += difference;
					difference = -difference;
				}
				return value >= start && value - difference <= start;
			}
		};
		this.addMouseListener(mouseListener);
		this.addMouseMotionListener(mouseListener);
		repaint();
	}
	public void addShape(Shape s) {
		shapes.add(s);
		if (inited) {
			Main.updateShapeList();
		}
		repaint();
		System.out.println(s.getClass().getName() + " added");
	}
	@Override
	public void repaint() {
		if (inited) {
			paintShapes();
			displayLabel.setIcon(new ImageIcon(
					getScaledImage(paper, 
							(int)(paper.getWidth() * getZoomRate()),
							(int)(paper.getHeight() * getZoomRate()))));
		}
		super.repaint();
		System.gc();
	}
	public void paintCornerWrappers(Shape selectedShape) {
		if (selectedShape == null) {
			return;
		}
		g.setColor(Color.GRAY);
		//Top-Left wrapper
		g.fillRect((int)selectedShape.getX(), 
				(int)selectedShape.getY(), 9, 9);
		//Top-Right wrapper
		g.fillRect((int)selectedShape.getX(), 
				(int)selectedShape.getY() + selectedShape.getHeightOnBoard() - 9, 9, 9);
		//Bottom-Left wrapper
		g.fillRect((int)selectedShape.getX() + selectedShape.getWidthOnBoard() - 9,
				(int)selectedShape.getY(), 9, 9);
		//Bottom-Right wrapper
		g.fillRect((int)selectedShape.getX() + selectedShape.getWidthOnBoard() - 9,
				(int)selectedShape.getY() + selectedShape.getHeightOnBoard() - 9, 9, 9);
	}
	private void paintShapes() {
		paintShapes(g);
	}
	public void paintShapes(Graphics2D g) {
		g.setColor(backgroundColor);
		g.fillRect(0, 0, paper.getWidth(), paper.getHeight());
		for (Shape shape:shapes) {
			if (shape.isVisible()) {
				shape.draw(g);
				if (Main.shapeList.getSelectedShape() == shape) {
					paintCornerWrappers(Main.getShapeList().getSelectedShape());
				}
			}
		}
	}
	public void setPaperSize(int width, int height) {
		paper = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		System.gc();
		g = paper.createGraphics();
		this.displayLabel.setIcon(new ImageIcon(paper));
		Main.updateSizeLabel();
		Main.f.revalidate();
		repaint();
	}
	public static Image getScaledImage(Image srcImg, int w, int h){
	    BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
	    Graphics2D g2 = resizedImg.createGraphics();

	    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	    g2.drawImage(srcImg, 0, 0, w, h, null);
	    g2.dispose();

	    return resizedImg;
	}
	public LinkedList<Shape> getShapesList() {
		return shapes;
	}
	public double getZoomRate() {
		return Main.getZoomSlider().getValue() / 100.0;
	}
	public Shape getShapeAt(int x, int y) {
		Shape s = null;
		for (int i = shapes.size() - 1; i > -1; i--) {
			s = shapes.get(i);
			if (s.getX() < x && s.getY() < y && s.getX() + s.getWidthOnBoard() > x && s.getY() + s.getHeightOnBoard() > y) {
				return s;
			}
		}
		return null;
	}
	public int getPaperWidth() {
		return this.paper.getWidth();
	}
	public int getPaperHeight() {
		return this.paper.getHeight();
	}
}