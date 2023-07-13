package drawables;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import drawables.shapes.Picture;
import drawables.shapes.abstractShapes.Shape;
import le.utils.PictureUtilities;
import main.Main;
import tools.adapters.PickingMouseAdapter;

public class Layer implements Drawable{
	
	protected Shape shape;

	protected BufferedImage top;
	
	public Layer(Shape shape) {
		super();
		this.shape = shape;
		this.top = null;
	}
	
	public Layer(Shape shape, BufferedImage top) {
		super();
		this.shape = shape;
		this.top = top;
	}


	@Override
	public void draw(Graphics2D graphics) {
		if (shape.isVisible()) {
			shape.draw(graphics);
			if (top != null) {
				graphics.drawImage(top, (int)shape.getX(), (int)shape.getY(), null);
			}
			if (Main.getLayersList().getSelectedLayer() != null &&
					Main.getLayersList().getSelectedLayer().getShape() == shape
					&& Main.getBoard().getCurrentMouseAdapter() instanceof PickingMouseAdapter) {
				Main.getBoard().paintCornerWrappers(shape);
			}
		}
	}
	
	public void initTop() {
		top = new BufferedImage(shape.getWidthOnBoard(), shape.getHeightOnBoard(), 
				BufferedImage.TYPE_INT_ARGB_PRE);
	}
	
	public void adjustTopSize(int width, int height) {
		top = PictureUtilities.getScaledImage(top, width, height);
	}
	
	public String encodeLayer() {
		return getTopEncoding() + "%" +  shape.encodeShape();
	}
	
	private String getTopEncoding() {
		if (top == null) {
			return null;
		}
		return Picture.encodeSourceImge(top);
	}
	
	public static Layer parseLayer(String encodedLayer) throws NumberFormatException, IOException {
		String[] data = encodedLayer.split("%");
		if (data[0].equals("null")) {
			return new Layer(Shape.parseShape(data[1]));
		}
		Layer l = new Layer(Shape.parseShape(data[1]));
		l.setTop(Picture.decodeSourceImage(data[0]));
		return l;
	}
	
	public Shape getShape() {
		return shape;
	}

	public void setShape(Shape shape) {
		this.shape = shape;
	}

	public BufferedImage getTop() {
		return top;
	}

	public void setTop(BufferedImage top) {
		this.top = top;
	}
	
}