package operatins;

import operatins.changes.Change;
import shapes.abstractShapes.Shape;

public class ChangesOperation implements Operation{

	private Change[] changes;
	private Shape s;
	
	
	
	public ChangesOperation(Shape s, Change... changes) {
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
