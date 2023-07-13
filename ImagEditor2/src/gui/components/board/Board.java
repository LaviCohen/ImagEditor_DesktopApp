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

import drawables.Layer;
import drawables.shapes.abstractShapes.Shape;
import tools.ToolListManager;
import tools.adapters.BoardAdapter;
import tools.adapters.BrushMouseAdapter;
import tools.adapters.EraserMouseAdapter;
import tools.adapters.PickingMouseAdapter;
import tools.adapters.TextMouseAdapter;
import main.Main;

public class Board extends JPanel{
	private static final long serialVersionUID = 1L;
	
	private JLabel displayLabel;
	private Graphics2D g;
	private Color backgroundColor;
	private BufferedImage paper;
	private LinkedList<Layer> layers = new LinkedList<Layer>();
	
	private BoardAdapter currentBoardAdapter = null;
	
	public boolean inited = false;
	
	public Board(Color color, int width, int height) {
		this.setLayout(new BorderLayout());
		this.setFocusable(true);
		this.backgroundColor = color;
		paper = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		displayLabel = new JLabel(new ImageIcon(paper));
		displayLabel.setFocusable(true);
		this.add(displayLabel, BorderLayout.CENTER);
		g = paper.createGraphics();
		inited = true;
		setMouseAdapterForTool(ToolListManager.getCurrentTool());
		repaint();
	}
	public void setMouseAdapterForTool(int tool) {
		if (currentBoardAdapter != null) {
			this.removeMouseListener(currentBoardAdapter);
			this.removeMouseMotionListener(currentBoardAdapter);
		}
		if (tool == ToolListManager.PICKER_TOOL) {
			currentBoardAdapter = new PickingMouseAdapter(this);
		} else if (tool == ToolListManager.BRUSH_TOOL) {
			currentBoardAdapter = new BrushMouseAdapter(this);
		}else if (tool == ToolListManager.ERASER_TOOL) {
			currentBoardAdapter = new EraserMouseAdapter(this);
		}else if (tool == ToolListManager.TEXT_TOOL) {
			currentBoardAdapter = new TextMouseAdapter(this);
		}
		this.addMouseListener(currentBoardAdapter);
		this.addMouseMotionListener(currentBoardAdapter);
	}
	public void addLayer(Layer layer) {
		layers.add(layer);
		if (inited) {
			Main.updateLayersList();
		}
		repaint();
		System.out.println(layer.getShape().getClass().getName() + " added");
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
		for (Layer layer:layers) {
			layer.draw(g);
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
	public LinkedList<Layer> getLayersList() {
		return layers;
	}
	public double getZoomRate() {
		return Main.getZoomSlider().getValue() / 100.0;
	}
	public Shape getShapeAt(int x, int y) {
		Shape s = null;
		for (int i = layers.size() - 1; i > -1; i--) {
			s = layers.get(i).getShape();
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
	public JLabel getDisplayLabel() {
		return displayLabel;
	}
	public void setDisplayLabel(JLabel displayLabel) {
		this.displayLabel = displayLabel;
	}
	public Graphics2D getG() {
		return g;
	}
	public void setG(Graphics2D g) {
		this.g = g;
	}
	public Color getBackgroundColor() {
		return backgroundColor;
	}
	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}
	public BufferedImage getPaper() {
		return paper;
	}
	public void setPaper(BufferedImage paper) {
		this.paper = paper;
	}
	public LinkedList<Layer> getLayers() {
		return layers;
	}
	public void setLayers(LinkedList<Layer> layers) {
		this.layers = layers;
	}
	public BoardAdapter getCurrentMouseAdapter() {
		return currentBoardAdapter;
	}
	public void setCurrentMouseAdapter(BoardAdapter currentMouseAdapter) {
		this.currentBoardAdapter = currentMouseAdapter;
	}
	public boolean isInited() {
		return inited;
	}
	public void setInited(boolean inited) {
		this.inited = inited;
	}
}