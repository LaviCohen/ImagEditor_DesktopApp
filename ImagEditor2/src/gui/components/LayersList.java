package gui.components;

import java.awt.BorderLayout;
import java.awt.Color;
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
import drawables.shapes.Picture;
import drawables.shapes.abstractShapes.Shape;
import gui.layouts.ListLayout;
import install.Resources;
import main.Main;

public class LayersList extends JPanel{
	private static final long serialVersionUID = 1L;
	public LinkedList<LayerPanel> layerPanels = new LinkedList<LayersList.LayerPanel>();
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
			this.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					layersList.setSelection(cur);
					if (e.getButton() == MouseEvent.BUTTON3) {
						popup.show(cur, e.getX(), e.getY());
					}
				}
			});
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
			layer.getShape().draw(g);
			layer.getShape().setX(x);
			layer.getShape().setY(y);
			JPanel panel = new JPanel(new BorderLayout());
			panel.add(new JLabel(new ImageIcon(
					Picture.getScaledImage(display, displayWidth, displayHeight))));
			return panel;
		}
	}
	public LayerPanel selected = null;
	public LayersList(Layer[] layers) {
		super(new ListLayout(0, 3));
		for (int i = layers.length - 1; i >= 0; i--) {
			LayerPanel sp = (LayerPanel) getGUIforlayer(layers[i]);
			layerPanels.add(sp);
			this.add(sp);
		}
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
			selected.setBackground(Main.theme.getBackgroundColor());
		}
		selected = layerPanel;
		if (selected != null) {
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
}