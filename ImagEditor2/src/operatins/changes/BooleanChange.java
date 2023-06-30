package operatins.changes;

import shapes.Rectangle;
import shapes.abstractShapes.Shape;

public class BooleanChange extends Change{

	public BooleanChange(int fieldID) {
		super(fieldID);
	}

	@Override
	public void apply(Shape s) {
		switch (fieldID) {
		case VISIBILITY_CHANGE:
			s.setVisible(!s.isVisible());
			break;
		case IS_FILLED_CHANGE:
			((Rectangle)s).setFilled(!((Rectangle)s).isFilled());
		default:
			throw new IllegalArgumentException("ID " + fieldID + " is not valid for boolean change");
		}
	}

	@Override
	public void undo(Shape s) {
		switch (fieldID) {
		case VISIBILITY_CHANGE:
			s.setVisible(!s.isVisible());
			break;
		case IS_FILLED_CHANGE:
			((Rectangle)s).setFilled(!((Rectangle)s).isFilled());
		default:
			throw new IllegalArgumentException("ID " + fieldID + " is not valid for boolean change");
		}
	}

}
