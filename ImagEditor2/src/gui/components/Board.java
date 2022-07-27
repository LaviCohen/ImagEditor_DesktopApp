package gui.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
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
import shapes.Shape;
import shapes.StretcableShpae;
import shapes.Text;

public class Board extends JPanel{
	private static final long serialVersionUID = 1L;
	
	public JLabel displayLabel;
	public Graphics g;
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
		g = paper.getGraphics();
		inited = true;
		MouseAdapter mouseListener = new MouseAdapter() {
			Shape shapeInFocus = null;
			
			
			int firstX = 0;
			int firstY = 0;
			int movementInX = 0;
			int movementInY = 0;
			
			@Override
			public void mousePressed(MouseEvent e) {
				firstX = e.getXOnScreen();
				firstY = e.getYOnScreen();
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
				firstX = 0;
				firstY = 0;
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
						Main.getPopupMenuForShape(shapeInFocus).show(Board.this, e.getX(), e.getY());
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
					movementInX = (int)(e.getXOnScreen() - firstX) * 100 / Main.getZoomSlider().getValue();
					movementInY = (int)(e.getYOnScreen() - firstY) * 100 / Main.getZoomSlider().getValue();
					switch(touchedWrapper){
					case 0:
						shapeInFocus.setX(shapeInFocus.getX() + movementInX);
						shapeInFocus.setY(shapeInFocus.getY() + movementInY);
						break;
					case TOP_LEFT_WRAPPER:
						shapeInFocus.setX(shapeInFocus.getX() + movementInX);
						shapeInFocus.setY(shapeInFocus.getY() + movementInY);
						if (shapeInFocus instanceof StretcableShpae) {
							((StretcableShpae)shapeInFocus).strecthBy(-movementInX, -movementInY);
						}
						break;
					case TOP_RIGHT_WRAPPER:
						shapeInFocus.setY(shapeInFocus.getY() + movementInY);
						if (shapeInFocus instanceof StretcableShpae) {
							((StretcableShpae)shapeInFocus).strecthBy(movementInX, -movementInY);
						}
						break;
					case BOTTOM_LEFT_WRAPPER:
						shapeInFocus.setX(shapeInFocus.getX() + movementInX);
						if (shapeInFocus instanceof StretcableShpae) {
							((StretcableShpae)shapeInFocus).strecthBy(-movementInX, movementInY);
						}
						break;
					case BOTTOM_RIGHT_WRAPPER:
						if (shapeInFocus instanceof StretcableShpae) {
							((StretcableShpae)shapeInFocus).strecthBy(movementInX, movementInY);
						}
						break;
					}
					firstX = e.getXOnScreen();
					firstY = e.getYOnScreen();
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
			public boolean isBetween(int start, int value, int difference) {
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
		g.fillRect(selectedShape.getX(), 
				selectedShape.getY(), 9, 9);
		//Top-Right wrapper
		g.fillRect(selectedShape.getX(), 
				selectedShape.getY() + selectedShape.getHeightOnBoard() - 9, 9, 9);
		//Bottom-Left wrapper
		g.fillRect(selectedShape.getX() + selectedShape.getWidthOnBoard() - 9,
				selectedShape.getY(), 9, 9);
		//Bottom-Right wrapper
		g.fillRect(selectedShape.getX() + selectedShape.getWidthOnBoard() - 9,
				selectedShape.getY() + selectedShape.getHeightOnBoard() - 9, 9, 9);
	}
	private void paintShapes() {
		paintShapes(g);
	}
	public void paintShapes(Graphics g) {
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
		g = paper.getGraphics();
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