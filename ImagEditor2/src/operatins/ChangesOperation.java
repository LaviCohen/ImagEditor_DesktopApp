package operatins;

import java.util.LinkedList;

import drawables.shapes.abstractShapes.Shape;
import operatins.changes.Change;

public class ChangesOperation implements Operation{

	private LinkedList<Change> changes;
	private Shape s;
	
	
	
	public ChangesOperation(Shape s, LinkedList<Change> changes) {
		super();
		this.changes = changes;
		this.s = s;
	}

	@Override
	public void undo() {
		for (Change change : changes) {
			change.undo(s);
		}
	}

	@Override
	public void redo() {
		for (Change change : changes) {
			change.apply(s);
		}
	}

}
