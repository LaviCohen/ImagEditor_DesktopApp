package shapes;

import java.awt.Graphics;
import java.io.IOException;

public abstract class Shape {
	public static final int DEFAULT_X = 0;
	public static final int DEFAULT_Y = 0;
	public static final boolean DEFAULT_VISIBLE = true;
	int x;
	int y;
	int id;
	boolean visible;
	String name;
	static int shapesCount = 1;
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
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
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
	public static Shape parseShape(String line) throws NumberFormatException, IOException {
		if (line.startsWith(Rectangle.class.getName())) {
			return new Rectangle(line.substring(line.indexOf(':') + 1).split(","));
		}
		if (line.startsWith(Text.class.getName())) {
			return new Text(line.substring(line.indexOf(':') + 1).split(","));
		}
		if (line.startsWith(Picture.class.getName())) {
			return new Picture(line.substring(line.indexOf(':') + 1).split(","));
		}
		return null;
	}
}