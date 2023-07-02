package gui.components.board;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import gui.ToolListManager;
import gui.components.board.adapters.BoardMouseAdapter;
import gui.components.board.adapters.BrushMouseAdapter;
import gui.components.board.adapters.PickingMouseAdapter;
import main.Main;
import shapes.abstractShapes.Shape;

public class Board extends JPanel{
	private static final long serialVersionUID = 1L;
	
	public JLabel displayLabel;
	public Graphics2D g;
	public Color backgroundColor;
	public BufferedImage paper;
	public LinkedList<Shape> shapes = new LinkedList<Shape>();
	
	private BoardMouseAdapter currentMouseAdapter = null;
	
	public boolean inited = false;
	
	public Board(Color color, int width, int height) {
		this.setLayout(new BorderLayout());
		this.backgroundColor = color;
		paper = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		displayLabel = new JLabel(new ImageIcon(paper));
		this.add(displayLabel, BorderLayout.CENTER);
		g = paper.createGraphics();
		inited = true;
		setMouseAdapterForTool(ToolListManager.getCurrentTool());
		repaint();
	}
	public void setMouseAdapterForTool(int tool) {
		if (currentMouseAdapter != null) {
			currentMouseAdapter.invalidate();
			this.removeMouseListener(currentMouseAdapter);
			this.removeMouseMotionListener(currentMouseAdapter);
		}
		if (tool == ToolListManager.PICKER_TOOL) {
			currentMouseAdapter = new PickingMouseAdapter(this);
		} else if (tool == ToolListManager.BRUSH_TOOL) {
			currentMouseAdapter = new BrushMouseAdapter(this);
		}
		this.addMouseListener(currentMouseAdapter);
		this.addMouseMotionListener(currentMouseAdapter);
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
				if (Main.shapeList.getSelectedShape() == shape && currentMouseAdapter instanceof
						PickingMouseAdapter) {
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