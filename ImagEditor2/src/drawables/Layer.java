package drawables;

import java.awt.Graphics2D;
import java.io.IOException;

import drawables.shapes.abstractShapes.Shape;
import gui.components.board.adapters.PickingMouseAdapter;
import main.Main;

public class Layer implements Drawable{
	
	protected Shape shape;

	public Layer(Shape shape) {
		super();
		this.shape = shape;
	}


	@Override
	public void draw(Graphics2D graphics) {
		if (shape.isVisible()) {
			shape.draw(graphics);
			if (Main.getLayersList().getSelectedLayer() != null &&
					Main.layersList.getSelectedLayer().getShape() == shape
					&& Main.getBoard().getCurrentMouseAdapter() instanceof PickingMouseAdapter) {
				Main.getBoard().paintCornerWrappers(shape);
			}
		}
	}
	
	public String encodeLayer() {
		return shape.encodeShape();
	}
	
	public static Layer parseLayer(String encodedLayer) throws NumberFormatException, IOException {
		return new Layer(Shape.parseShape(encodedLayer));
	}
	
	public Shape getShape() {
		return shape;
	}

	public void setShape(Shape shape) {
		this.shape = shape;
	}
	
}