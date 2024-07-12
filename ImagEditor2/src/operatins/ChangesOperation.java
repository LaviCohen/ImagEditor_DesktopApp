package operatins;

import java.util.LinkedList;

import drawables.shapes.abstractShapes.Shape;
import operatins.changes.Change;

public class ChangesOperation implements Operation{

	private LinkedList<Change> changes;
	private Shape shape;
	
	
	
	public ChangesOperation(Shape s, LinkedList<Change> changes) {
		super();
		this.changes = changes;
		this.shape = s;
	}

	@Override
	public void undo() {
		for (Change change : changes) {
			change.undo(shape);
		}
	}

	@Override
	public void redo() {
		for (Change change : changes) {
			change.apply(shape);
		}
	}

	public Shape getShape() {
		return shape;
	}

}
