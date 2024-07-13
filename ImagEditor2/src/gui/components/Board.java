package gui.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import drawables.Layer;
import drawables.shapes.abstractShapes.Shape;
import install.Preferences;
import main.Actions;
import main.Main;
import tools.Tool;
import tools.ToolsManager;
import tools.adapters.BoardAdapter;
import tools.adapters.PickingMouseAdapter;

public class Board extends JPanel{
	
	private static final long serialVersionUID = 1L;
	
	public static enum ActionCodes{
		CTRL_C, CTRL_V, CTRL_X
	}
	
	private Color backgroundColor;
	private int paperWidth, paperHeight;
	private LinkedList<Layer> layers = new LinkedList<Layer>();
	
	private BoardAdapter currentBoardAdapter = null;
	
	public boolean inited = false;
	
	private boolean activeManualRefreshing = false;

	private boolean isExportPaintMode = false;
	
	public Board(Color color, int width, int height) {
		this.setLayout(new BorderLayout());
		this.setFocusable(true);
		this.setOpaque(true);
		this.paperWidth = width;
		this.paperHeight = height;
		this.backgroundColor = color;
		inited = true;
		initSpecialIntefaces();
		setMouseAdapterForTool(ToolsManager.getCurrentTool());
		repaint();
	}
	
	public void initSpecialIntefaces() {
		
		new DropTarget(this, new DropTargetListener() {
			
			@Override
			public void dropActionChanged(DropTargetDragEvent dtde) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void drop(DropTargetDropEvent dtde) {
				System.out.println("Transferred");
				dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
				System.out.println(Actions.handleTransferable(dtde.getTransferable()));
			}
			
			@Override
			public void dragOver(DropTargetDragEvent dtde) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void dragExit(DropTargetEvent dte) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void dragEnter(DropTargetDragEvent dtde) {
				// TODO Auto-generated method stub
				
			}
		});
		
		//Initializing inputs
		this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK), ActionCodes.CTRL_V);
		
		//Initializing actions
		this.getActionMap().put(ActionCodes.CTRL_V, new AbstractAction() {
			
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				Actions.copyFromClipboardTo(new Point(0, 0));
				
			}
		});
	}
	
	public void setMouseAdapterForTool(Tool tool) {
		if (currentBoardAdapter != null) {
			this.removeMouseListener(currentBoardAdapter);
			this.removeMouseMotionListener(currentBoardAdapter);
		}
		currentBoardAdapter = ToolsManager.getAdapterForTool(this, tool);
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
		if (Preferences.manualRefreshOnly && !activeManualRefreshing) {
			return;
		}
		super.repaint();
		System.gc();
	}
	public void paintCornerWrappers(Shape selectedShape, Graphics2D g) {
		if (selectedShape == null) {
			return;
		}
		g.setColor(Color.GRAY);
		//Top-Left wrapper
		g.fillRect((int)(selectedShape.getX() * getZoomRate() + getLeftGap()), 
				(int)(selectedShape.getY() * getZoomRate() + getTopGap()), 9, 9);
		//Bottom-Left wrapper
		g.fillRect((int)(selectedShape.getX() * getZoomRate() + getLeftGap()), 
				(int)(selectedShape.getY() * getZoomRate() + getTopGap() + selectedShape.getHeightOnBoard() * getZoomRate() - 9), 9, 9);
		//Top-Right wrapper
		g.fillRect((int)(selectedShape.getX() * getZoomRate() + getLeftGap() + selectedShape.getWidthOnBoard() * getZoomRate() - 9),
				(int)(selectedShape.getY() * getZoomRate() + getTopGap()), 9, 9);
		//Bottom-Right wrapper
		g.fillRect((int)(selectedShape.getX() * getZoomRate() + getLeftGap() + selectedShape.getWidthOnBoard() * getZoomRate() - 9),
				(int)(selectedShape.getY() * getZoomRate() + getTopGap() + selectedShape.getHeightOnBoard() * getZoomRate() - 9), 9, 9);
	}
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (Preferences.manualRefreshOnly && !activeManualRefreshing) {
			return;
		}
		setSize(getPreferredSize());
		paintShapes((Graphics2D)g);
		if (Main.getLayersList() != null &&
				Main.getLayersList().getSelectedLayer() != null &&
				 Main.getBoard().getCurrentMouseAdapter() instanceof PickingMouseAdapter) {
			Main.getBoard().paintCornerWrappers(Main.getLayersList().getSelectedLayer().getShape(), (Graphics2D) g);
		}
		activeManualRefreshing = false;
	}
	public void paintShapes(Graphics2D g) {
		g.setColor(backgroundColor);
		g.fillRect((int)getLeftGap(), (int)getTopGap(), (int)(paperWidth * getZoomRate()),
				(int)(paperHeight * getZoomRate()));
		g.setClip((int)getLeftGap(), (int)getTopGap(), (int)(paperWidth * getZoomRate()),
				(int)(paperHeight * getZoomRate()));
		for (Layer layer:layers) {
			Shape s = layer.getShape();
			if (getZoomRate() != 1) {
				double x = s.getX(), y = s.getY();
				s.setX(0);
				s.setY(0);
				BufferedImage preview = new BufferedImage(s.getWidthOnBoard(), s.getHeightOnBoard(), 
						BufferedImage.TYPE_INT_ARGB_PRE);
				layer.draw(preview.createGraphics());
				g.drawImage(preview, 
						(int)(x * getZoomRate() + getLeftGap()), 
						(int)(y * getZoomRate() + getTopGap()),
						(int)(preview.getWidth() * getZoomRate()), 
						(int)(preview.getHeight() * getZoomRate()), null);
				s.setX(x);
				s.setY(y);
			} else {
				s.setX(s.getX() + getLeftGap());
				s.setY(s.getY() + getTopGap());
				layer.draw(g);
				s.setX(s.getX() - getLeftGap());
				s.setY(s.getY() - getTopGap());
			}
		}
	}
	public void setPaperSize(int width, int height) {
		paperWidth = width;
		paperHeight = height;
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
	
	public double getTopGap() {
		if (isExportPaintMode) {
			return 0;
		}
		return ((getHeight() - (paperHeight * getZoomRate()))/2);
	}
	public double getLeftGap() {
		if (isExportPaintMode) {
			return 0;
		}
		return ((getWidth()  - (paperWidth  * getZoomRate()))/2);
	}
	public int boardToPaperCoordinatesX(int boardX) {
		return (int)((boardX - getLeftGap()) / getZoomRate());
	}
	public int boardToPaperCoordinatesY(int boardY) {
		return (int)((boardY - getTopGap()) / getZoomRate());
	}

	public int getPaperWidth() {
		return paperWidth;
	}
	public int getPaperHeight() {
		return paperHeight;
	}
	public Color getBackgroundColor() {
		return backgroundColor;
	}
	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
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
	@Override
	public Dimension getPreferredSize() {
		Dimension d = getParent().getSize();
		if (d.width < paperWidth * getZoomRate()) {
			d.width = (int) (paperWidth * getZoomRate());
		}
		if (d.height < paperHeight * getZoomRate()) {
			d.height = (int) (paperHeight * getZoomRate());
		}
		return d;
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
	public boolean isActiveManualRefreshing() {
		return activeManualRefreshing;
	}
	public void setActiveManualRefreshing(boolean isHoldingManualRefresh) {
		this.activeManualRefreshing = isHoldingManualRefresh;
	}
	public void setExportPaintMode(boolean b) {
		this.isExportPaintMode  = b;
	}
	public boolean isExportPaintMode() {
		return isExportPaintMode;
	}
}