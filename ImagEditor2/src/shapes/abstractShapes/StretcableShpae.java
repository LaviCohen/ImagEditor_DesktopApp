package shapes.abstractShapes;

public abstract class StretcableShpae extends Shape{
	
	protected double width;
	protected double height;
	
	public StretcableShpae(int x, int y, boolean visible, String name, double width, double height) {
		super(x, y, visible, name);
		this.width = width;
		this.height = height;
	}

	public void strecthBy(int widthDiff, int heightDiff) {
		this.width += widthDiff;
		this.height += heightDiff;
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
	}
	public double getHeight() {
		return height;
	}
	public void setHeight(double height) {
		this.height = height;
	}
}
