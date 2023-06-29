package operatins;

import main.Main;
import shapes.abstractShapes.Shape;

public class AddShapeOperation implements Operation{

	private Shape s;
	
	public AddShapeOperation(Shape s) {
		super();
		this.s = s;
	}

	@Override
	public void undo() {
		Main.getBoard().getShapesList().remove(s);
		Main.getBoard().repaint();
		Main.updateShapeList();
	}

}
