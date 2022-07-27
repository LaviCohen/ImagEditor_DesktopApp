package shapes.abstractShapes;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import le.gui.dialogs.LDialogs;
import main.Main;
import shapes.Code;
import shapes.Picture;
import shapes.Rectangle;
import shapes.Text;

public abstract class Shape {
	public static final int DEFAULT_X = 0;
	public static final int DEFAULT_Y = 0;
	public static final boolean DEFAULT_VISIBLE = true;
	protected double x;
	protected double y;
	protected int id;
	protected boolean visible;
	protected String name;
	protected static int shapesCount = 1;
	public Shape(int x, int y, boolean visible, String name) {
		super();
		this.x = x;
		this.y = y;
		this.visible = visible;
		this.id = shapesCount;
		if (name != null) {
			this.name = name;
		}else {
			this.name = getDefaultName();
		}
		shapesCount++;
	}
	public Shape() {
		this(DEFAULT_X, DEFAULT_Y, DEFAULT_VISIBLE, null);
	}
	public double getX() {
		return x;
	}
	public void setX(double x) {
		this.x = x;
	}
	public double getY() {
		return y;
	}
	public void setY(double y) {
		this.y = y;
	}
	public boolean isVisible() {
		return visible;
	}
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public abstract void draw(Graphics g);
	public abstract void edit();
	public abstract int getWidthOnBoard();
	public abstract int getHeightOnBoard();
	@Override
	public String toString() {
		return this.name;
	}
	public String getDefaultName() {
		return this.getClass().getSimpleName() + " " + shapesCount;
	}
	public String describe() {
		return "id:" + id + "\nx:" + x + "\ny" + y + "\nvisible:" + visible + "\nname:" + name;
	}
	public String encodeShape() {
		return this.getClass().getName() + ":" + x + "," + y + "," + visible + "," + name;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public JPopupMenu getPopupMenuForShape() {
		JPopupMenu popup = new JPopupMenu("Options");
		Main.theme.affect(popup);
		JMenuItem setName = new JMenuItem(Main.translator.get("Set Name"));
		Main.theme.affect(setName);
		setName.addActionListener(new ActionListener() {
	
			@Override
			public void actionPerformed(ActionEvent e) {
				Shape.this.setName(LDialogs
						.showInputDialog(null, Main.translator.get("Enter the new name for") + " \"" + Shape.this.getName() + "\"", null));
				Main.updateShapeList();
			}
		});
		popup.add(setName);
		JMenuItem edit = new JMenuItem(Main.translator.get("Edit"));
		Main.theme.affect(edit);
		edit.addActionListener(new ActionListener() {
	
			@Override
			public void actionPerformed(ActionEvent e) {
				Shape.this.edit();
			}
		});
		popup.add(edit);
		return popup;
	}
	public static Shape parseShape(String line) throws NumberFormatException, IOException {
		if (line.startsWith(Rectangle.class.getName())) {
			return new Rectangle(line.substring(line.indexOf(':') + 1));
		}else if (line.startsWith(Text.class.getName())) {
			return new Text(line.substring(line.indexOf(':') + 1));
		}else if (line.startsWith(Picture.class.getName())) {
			return new Picture(line.substring(line.indexOf(':') + 1));
		}else if (line.startsWith(Code.class.getName())) {
			return new Code(line.substring(line.indexOf(':') + 1));
		}
		return null;
	}
}