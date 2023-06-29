package operatins;

import main.Main;
import shapes.abstractShapes.Shape;

public class RemoveShapeOperation implements Operation{

	private Shape s;
	
	public RemoveShapeOperation(Shape s) {
		super();
		this.s = s;
	}

	@Override
	public void undo() {
		Main.getBoard().getShapesList().add(s);
		Main.getBoard().repaint();
		Main.updateShapeList();
	}

	@Override
	public void redo() {
		Main.getBoard().getShapesList().remove(s);
		Main.getBoard().repaint();
		Main.updateShapeList();
	}

}
