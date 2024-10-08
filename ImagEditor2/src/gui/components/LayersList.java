package gui.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import drawables.Layer;
import drawables.shapes.abstractShapes.Shape;
import install.Resources;
import le.gui.layouts.ListLayout;
import le.utils.PictureUtilities;
import main.Main;

public class LayersList extends JPanel{
	
	private static final long serialVersionUID = 1L;
	
	private LinkedList<LayerPanel> layerPanels = new LinkedList<LayersList.LayerPanel>();
	
	private int seperatorPosition = -1;

	private LayerPanel selected = null;
	
	private JPanel seperator;
	
	private boolean hasSeperator = false;
	
	public LayersList(Layer[] layers) {
		super(new ListLayout(0, 3));
		for (int i = layers.length - 1; i >= 0; i--) {
			LayerPanel sp = (LayerPanel) getGUIforlayer(layers[i]);
			layerPanels.add(sp);
			this.add(sp);
		}
		seperator = new JPanel();
		seperator.setPreferredSize(new Dimension(20, 5));
		seperator.setOpaque(true);
		seperator.setBackground(Color.GREEN);
	}
	public void addSeperator(int index) {
		removeSeperator();
		this.add(seperator, (Integer) index);
		this.seperatorPosition = index;
		setHasSeperator(true);
		repaint();
	}
	public void removeSeperator() {
		this.remove(seperator);
		setHasSeperator(false);
		repaint();
	}
	public JComponent getGUIforlayer(Layer layer) {
		LayerPanel layerPanel = new LayerPanel(layer, this);
		return layerPanel;
	}
	public void setSelection(Shape shape) {
		setSelection(getLayerForShape(shape));
	}
	public void setSelection(Layer layer) {
		if (layer == null) {
			setSelection((LayerPanel)null);
			return;
		}
		for (LayerPanel layerPanel: layerPanels) {
			if (layer == layerPanel.layer) {
				setSelection(layerPanel);
				return;
			}
		}
	}
	public void setSelection(LayerPanel layerPanel) {
		if (selected != null) {
			selected.setBackground(Main.theme.getBackgroundColor().brighter());
		}
		selected = layerPanel;
		if (selected != null) {
			selected.layer.getShape().edit();
			selected.setBackground(Color.CYAN);
			selected.revalidate();
			selected.repaint();
		}
	}
	public Layer getSelectedLayer() {
		if (selected == null) {
			return null;
		}
		return selected.layer;
	}
	public Shape getSelectedShape() {
		if (selected == null) {
			return null;
		}
		return selected.layer.getShape();
	}
	public void updateImage(Shape shape) {
		Layer layer = getLayerForShape(shape);
		for (int i = 0; i < layerPanels.size(); i++) {
			LayerPanel sp = layerPanels.get(i);
			if (sp.layer == layer) {
				sp.remove(((BorderLayout)sp.getLayout()).getLayoutComponent(Main.translator.getBeforeTextBorder()));
				sp.add(LayerPanel.getSmallImage(layer), Main.translator.getBeforeTextBorder());
				sp.revalidate();
				sp.repaint();
				this.revalidate();
				this.repaint();
				break;
			}
		}
	}
	public Layer getLayerForShape(Shape shape) {
		for (LayerPanel layerPanel : layerPanels) {
			if (layerPanel.layer.getShape() == shape) {
				return layerPanel.layer;
			}
		}
		return null;
	}
	public int getSeperatorPositionID() {
		return seperatorPosition;
	}
	public void setSeperatorPositionID(int seperatorPositionID) {
		this.seperatorPosition = seperatorPositionID;
	}
	

	public LinkedList<LayerPanel> getLayerPanels() {
		return layerPanels;
	}
	public void setLayerPanels(LinkedList<LayerPanel> layerPanels) {
		this.layerPanels = layerPanels;
	}


	public boolean isHasSeperator() {
		return hasSeperator;
	}
	public void setHasSeperator(boolean hasSeperator) {
		this.hasSeperator = hasSeperator;
	}


	public static class LayerPanel extends JPanel{
		private static final long serialVersionUID = 1L;
		public Layer layer;
		public static int displayWidth = 50;
		public static int displayHeight = 50;
		public LayerPanel(Layer layer, LayersList layersList) {
			super(new BorderLayout(5, 0));
			this.setBackground(Main.theme.getBackgroundColor().brighter());
			this.setOpaque(true);
			this.layer = layer;
			final LayerPanel cur = this;
			JPopupMenu popup = layer.getShape().getPopupMenuForShape();
			JButton showNhide = new JButton(Resources.hideIcon);
			showNhide.setToolTipText("hide this layer");
			showNhide.setFocusPainted(false);
			showNhide.setBackground(Color.WHITE);
			if (!Main.theme.isLightMode()) {
				showNhide.setBackground(Color.BLACK);
			}
			showNhide.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					if (layer.getShape().isVisible()) {
						layer.getShape().setVisible(false);
						showNhide.setIcon(Resources.showIcon);
						showNhide.setToolTipText("show this layer");
					}else {
						layer.getShape().setVisible(true);
						showNhide.setIcon(Resources.hideIcon);
						showNhide.setToolTipText("hide this layer");
					}
					Main.getBoard().repaint();
					showNhide.repaint();
				}
			});
			this.add(showNhide, Main.translator.getAfterTextBorder());
			JLabel label = new JLabel(layer.getShape().getName());
			label.setBackground(Main.theme.getBackgroundColor());
			label.setForeground(Main.theme.getTextColor());
			this.add(label);
			MouseAdapter adapter = new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					layersList.setSelection(cur);
					if (e.getButton() == MouseEvent.BUTTON3) {
						popup.show(cur, e.getX(), e.getY());
					}
				}
				@Override
				public void mouseDragged(MouseEvent e) {
					Main.getLayersList().setSelection(LayerPanel.this);
					int y = e.getY();
					int curIndex = Main.getBoard().getLayers().size() - 1 -
							Main.getBoard().getLayers().indexOf(LayerPanel.this.layer);
					int futureIndex = curIndex + (y / 53);
					
					if (futureIndex < 0) {
						futureIndex = 0;
					} else if (futureIndex > Main.getLayersList().layerPanels.size()) {
						futureIndex = Main.getLayersList().layerPanels.size() - 1;
					}
					
					Main.getLayersList().addSeperator(futureIndex);
				}
				@Override
				public void mouseReleased(MouseEvent e) {
					if (Main.getLayersList().isHasSeperator()) {
						//Index Calculation
						int indexInList = Main.getLayersList().layerPanels.size() -
								Main.getLayersList().seperatorPosition;
						Layer cur = LayerPanel.this.layer;
						if (Main.getBoard().getLayers().indexOf(cur) < indexInList) {
							indexInList--;
						}
						//Swap
						Main.getBoard().getLayers().remove(cur);
						Main.getBoard().getLayers().add(indexInList, cur);
						//Update
						Main.updateLayersList();
						Main.getBoard().repaint();
					}
				}
			};
			this.addMouseListener(adapter);
			this.addMouseMotionListener(adapter);
			this.add(getSmallImage(layer), Main.translator.getBeforeTextBorder());
			
		}
		public static JPanel getSmallImage(Layer layer) {
			int layerWidth = layer.getShape().getWidthOnBoard();
			int layerHeight = layer.getShape().getHeightOnBoard();
			int max = Math.max(layerWidth, layerHeight);
			BufferedImage display = new BufferedImage(max, max, BufferedImage.TYPE_INT_ARGB);
			int x = (int)layer.getShape().getX();
			int y = (int)layer.getShape().getY();
			layer.getShape().setY(0);
			layer.getShape().setX(0);
			Graphics2D g = display.createGraphics();
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, max, max);
			layer.draw(g);
			layer.getShape().setX(x);
			layer.getShape().setY(y);
			JPanel panel = new JPanel(new BorderLayout());
			panel.add(new JLabel(new ImageIcon(
					PictureUtilities.getScaledImage(display, displayWidth, displayHeight))));
			return panel;
		}
	}
	
}