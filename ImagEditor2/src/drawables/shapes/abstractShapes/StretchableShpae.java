package drawables.shapes.abstractShapes;

import main.Main;

public abstract class StretchableShpae extends Shape{
	
	protected double width;
	protected double height;
	
	public StretchableShpae(double x, double y, boolean visible, String name, double width, double height) {
		super(x, y, visible, name);
		this.width = width;
		this.height = height;
	}

	public void strecthBy(int widthDiff, int heightDiff) {
		this.width += widthDiff;
		this.height += heightDiff;
		Main.getLayersList().getLayerForShape(this).adjustTopSize((int)this.width, (int)this.height);
	}
	
	@Override
	public String encodeShape() {
		return super.encodeShape() + "," + width + "," + height;
	}
	
	@Override
	public int getWidthOnBoard() {
		return (int)getWidth();
	}
	@Override
	public int getHeightOnBoard() {
		return (int)getHeight();
	}
	public double getWidth() {
		return width;
	}
	public void setWidth(double width) {
		this.width = width;
		Main.getLayersList().getLayerForShape(this).adjustTopSize((int)this.width, (int)this.height);
	}
	public double getHeight() {
		return height;
	}
	public void setHeight(double height) {
		this.height = height;
		Main.getLayersList().getLayerForShape(this).adjustTopSize((int)this.width, (int)this.height);
	}
}
