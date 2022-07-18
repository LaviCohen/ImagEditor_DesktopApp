package gui.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import install.Resources;
import layouts.ListLayout;
import main.Main;
import shapes.Picture;
import shapes.Shape;

public class ShapeList extends JPanel{
	private static final long serialVersionUID = 1L;
	public LinkedList<ShapePanel> shapePanels = new LinkedList<ShapeList.ShapePanel>();
	public static class ShapePanel extends JPanel{
		private static final long serialVersionUID = 1L;
		public Shape shape;
		public static int displayWidth = 50;
		public static int displayHeight = 50;
		public ShapePanel(Shape shape, ShapeList shapeList) {
			super(new BorderLayout(5, 0));
			this.shape = shape;
			final ShapePanel cur = this;
			JPopupMenu popup = Main.getPopupMenuForShape(shape);
			this.setBackground(Color.WHITE);
			JButton showNhide = new JButton(Resources.hideIcon);
			showNhide.setToolTipText("hide this shape");
			showNhide.setFocusPainted(false);
			showNhide.setBackground(Color.WHITE);
			showNhide.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					if (shape.isVisible()) {
						shape.setVisible(false);
						showNhide.setIcon(Resources.showIcon);
						showNhide.setToolTipText("show this shape");
					}else {
						shape.setVisible(true);
						showNhide.setIcon(Resources.hideIcon);
						showNhide.setToolTipText("hide this shape");
					}
					Main.getBoard().repaint();
					showNhide.repaint();
				}
			});
			this.add(showNhide, Main.translator.getAfterTextBorder());
			this.add(new JLabel(shape.getName()));
			this.addMouseListener(new MouseListener() {
				
				@Override
				public void mouseReleased(MouseEvent e) {
					// TODO Auto-generated method stub
					
				}
				@Override
				public void mousePressed(MouseEvent e) {
					// TODO Auto-generated method stub
					
				}
				@Override
				public void mouseExited(MouseEvent e) {
					// TODO Auto-generated method stub
					
				}
				@Override
				public void mouseEntered(MouseEvent e) {
					
				}
				@Override
				public void mouseClicked(MouseEvent e) {
					shapeList.setSelection(cur);
					if (e.getButton() == MouseEvent.BUTTON3) {
						popup.show(cur, e.getX(), e.getY());
					}
				}
			});
			this.add(getSmallImage(shape), Main.translator.getBeforeTextBorder());
		}
		public static JPanel getSmallImage(Shape s) {
			int shapeWidth = s.getWidthOnBoard();
			int shapeHeight = s.getHeightOnBoard();
			int max = Math.max(shapeWidth, shapeHeight);
			BufferedImage display = new BufferedImage(max, max, BufferedImage.TYPE_INT_ARGB);
			int x = s.getX();
			int y = s.getY();
			s.setY(0);
			s.setX(0);
			Graphics g = display.getGraphics();
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, max, max);
			s.draw(g);
			s.setX(x);
			s.setY(y);
			JPanel panel = new JPanel(new BorderLayout());
			panel.add(new JLabel(new ImageIcon(
					Picture.getScaledImage(display, displayWidth, displayHeight))));
			return panel;
		}
	}
	public ShapePanel selected = null;
	public ShapeList(Shape[] shapes) {
		super(new ListLayout(0, 3));
		for (int i = shapes.length - 1; i >= 0; i--) {
			ShapePanel sp = (ShapePanel) getGUIforShape(shapes[i]);
			shapePanels.add(sp);
			this.add(sp);
		}
	}
	public JComponent getGUIforShape(Shape shape) {
		ShapePanel shapePanel = new ShapePanel(shape, this);
		return shapePanel;
	}
	public void setSelection(Shape s) {
		for (int i = 0; i < shapePanels.size(); i++) {
			if (s == shapePanels.get(i).shape) {
				setSelection(shapePanels.get(i));
				return;
			}
		}
	}
	public void setSelection(ShapePanel shapePanel) {
		if (selected != null) {
			selected.setBackground(Color.WHITE);
		}
		selected = shapePanel;
		selected.setBackground(Color.CYAN);
		selected.revalidate();
		selected.repaint();
	}
	public Shape getSelectedShape() {
		if (selected == null) {
			return null;
		}
		return selected.shape;
	}
	public void updateImage(Shape s) {
		for (int i = 0; i < shapePanels.size(); i++) {
			ShapePanel sp = shapePanels.get(i);
			if (sp.shape == s) {
				sp.remove(((BorderLayout)sp.getLayout()).getLayoutComponent(Main.translator.getBeforeTextBorder()));
				sp.add(ShapePanel.getSmallImage(s), Main.translator.getBeforeTextBorder());
				sp.revalidate();
				sp.repaint();
				this.revalidate();
				this.repaint();
				break;
			}
		}
	}
}