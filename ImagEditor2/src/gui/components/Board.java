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
		final Board cur = this;
		MouseAdapter mouseListener = new MouseAdapter() {
			int firstX = 0;
			int firstY = 0;
			int firstShapeX = 0;
			int firstShapeY = 0;
			double movementInX = 0;
			double movementInY = 0;
			@Override
			public void mousePressed(MouseEvent e) {
				firstX = e.getXOnScreen();
				firstY = e.getYOnScreen();
				shapeInFocus = getShapeAt(
						(int)((e.getX() - getLeftGap()) / getZoomRate()),
						(int)((e.getY() - getUpGap()) / getZoomRate()));
				if (shapeInFocus != null) {
					firstShapeX = shapeInFocus.getX();
					firstShapeY = shapeInFocus.getY();
				}
			}
			private double getUpGap() {
				return ((cur.getHeight() - (cur.paper.getHeight() * getZoomRate()))/2);
			}
			private double getLeftGap() {
				return ((cur.getWidth()  - (cur.paper.getWidth()  * getZoomRate()))/2);
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				firstX = 0;
				firstY = 0;
				firstShapeX = 0;
				firstShapeY = 0;
				movementInX = 0;
				movementInY = 0;
				shapeInFocus = null;
			}
			@Override
			public void mouseClicked(MouseEvent e) {
				System.out.println(e.getClickCount());
				shapeInFocus = getShapeAt(
						(int)((e.getX() - getLeftGap()) / getZoomRate()),
						(int)((e.getY() - getUpGap()) / getZoomRate()));
				if (e.getButton() == MouseEvent.BUTTON3) {
					if (shapeInFocus != null) {
						Main.getPopupMenuForShape(shapeInFocus).show(cur, e.getX(), e.getY());
					}
				}
				if(e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1 
						&& shapeInFocus instanceof Text){
					String text = LDialogs.
							showInputDialog(cur, "Enter Text:", ((Text)shapeInFocus).getText());
					if (text != null && !text.equals(((Text)shapeInFocus).getText())) {
						((Text)shapeInFocus).setText(text);
						cur.repaint();
						Main.getShapeList().updateImage(shapeInFocus);
					}
				}
			}
			Shape shapeInFocus = null;
			@Override
			public void mouseDragged(MouseEvent e) {
				if (shapeInFocus != null) {
					movementInX += (e.getXOnScreen() - firstX) * 100.0 / Main.getZoomSlider().getValue();
					movementInY += (e.getYOnScreen() - firstY) * 100.0 / Main.getZoomSlider().getValue();
					shapeInFocus.setX(firstShapeX + (int)movementInX);
					shapeInFocus.setY(firstShapeY + (int)movementInY);
					firstX = e.getXOnScreen();
					firstY = e.getYOnScreen();
					Main.getBoard().repaint();
				}
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
	private void paintShapes() {
		paintShapes(g);
	}
	public void paintShapes(Graphics g) {
		g.setColor(backgroundColor);
		g.fillRect(0, 0, paper.getWidth(), paper.getHeight());
		for (int i = 0; i < shapes.size(); i++) {
			if (shapes.get(i).isVisible()) {
				shapes.get(i).draw(g);
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