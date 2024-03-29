package operatins.changes;

import drawables.shapes.Picture;
import drawables.shapes.Rectangle;
import drawables.shapes.abstractShapes.Shape;

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
			break;
		case PREVIEW_CHANGE:
			((Picture)s).setPreview(newValue);
			break;
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
		case PREVIEW_CHANGE:
			((Picture)s).setPreview(!newValue);
			break;
		default:
			throw new IllegalArgumentException("ID " + fieldID + " is not valid for boolean change");
		}
	}

}
