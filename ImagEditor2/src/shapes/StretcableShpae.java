package shapes;

public abstract class StretcableShpae extends Shape{
	protected int width;
	protected int height;
	
	public StretcableShpae(int x, int y, boolean visible, String name, int width, int height) {
		super(x, y, visible, name);
		this.width = width;
		this.height = height;
	}

	public void strecthBy(int widthDiff, int heightDiff) {
		this.width += widthDiff;
		this.height += heightDiff;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
}
