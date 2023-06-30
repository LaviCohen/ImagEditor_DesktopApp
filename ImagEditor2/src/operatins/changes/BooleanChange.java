package operatins.changes;

import shapes.Rectangle;
import shapes.abstractShapes.Shape;

public class BooleanChange extends Change{

	private boolean newValue;
	
	public BooleanChange(int fieldID, boolean newValue) {
		super(fieldID);
		this.newValue = newValue;
	}

	@Override
	public void apply(Shape s) {
		switch (fieldID) {
		case VISIBILITY_CHANGE:
			s.setVisible(newValue);
			break;
		case IS_FILLED_CHANGE:
			((Rectangle)s).setFilled(newValue);
		default:
			throw new IllegalArgumentException("ID " + fieldID + " is not valid for boolean change");
		}
	}

	@Override
	public void undo(Shape s) {
		switch (fieldID) {
		case VISIBILITY_CHANGE:
			s.setVisible(!newValue);
			break;
		case IS_FILLED_CHANGE:
			((Rectangle)s).setFilled(!newValue);
		default:
			throw new IllegalArgumentException("ID " + fieldID + " is not valid for boolean change");
		}
	}

}
