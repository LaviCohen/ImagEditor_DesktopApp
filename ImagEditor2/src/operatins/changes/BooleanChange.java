package operatins.changes;

import drawables.shapes.Picture;
import drawables.shapes.Rectangle;
import drawables.shapes.abstractShapes.Shape;

public class BooleanChange extends Change{

	private boolean newValue;
	
	public BooleanChange(ChangeType changeType, boolean newValue) {
		super(changeType);
		this.newValue = newValue;
	}

	@Override
	public void apply(Shape s) {
		switch (changeType) {
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
			throw new IllegalArgumentException("ID " + changeType + " is not valid for boolean change");
		}
	}

	@Override
	public void undo(Shape s) {
		switch (changeType) {
		case VISIBILITY_CHANGE:
			s.setVisible(!newValue);
			break;
		case IS_FILLED_CHANGE:
			((Rectangle)s).setFilled(!newValue);
		case PREVIEW_CHANGE:
			((Picture)s).setPreview(!newValue);
			break;
		default:
			throw new IllegalArgumentException("ID " + changeType + " is not valid for boolean change");
		}
	}

}
