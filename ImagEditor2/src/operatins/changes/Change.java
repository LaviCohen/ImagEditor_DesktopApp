package operatins.changes;

import drawables.shapes.abstractShapes.Shape;

public abstract class Change {
	
	protected ChangeType changeType;
	
	public Change(ChangeType changeType) {
		this.changeType = changeType;
	}
	public abstract void apply(Shape s);
	public abstract void undo(Shape s);
}
