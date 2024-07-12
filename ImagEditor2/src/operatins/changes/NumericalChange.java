package operatins.changes;

import drawables.shapes.Picture;
import drawables.shapes.Rectangle;
import drawables.shapes.abstractShapes.Shape;
import drawables.shapes.abstractShapes.StretchableShpae;

public class NumericalChange extends Change{
	
	protected double changeValue;
	
	public NumericalChange(ChangeType changeType, double changeValue) {
		super(changeType);
		this.changeValue = changeValue;
	}

	@Override
	public void apply(Shape s) {
		switch (changeType) {
		case X_CHANGE:
			s.setX(s.getX() + changeValue);
			break;
		case Y_CHANGE:
			s.setY(s.getY() + changeValue);
			break;
		case WIDTH_CHANGE:
			((StretchableShpae)s).setWidth(((StretchableShpae)s).getWidth() + changeValue);
			break;
		case HEIGHT_CHANGE:
			((StretchableShpae)s).setHeight(((StretchableShpae)s).getHeight() + changeValue);
			break;
		case CUT_FROM_BOTTOM_CHANGE:
			((Picture)s).addToCutFromBottom((int) changeValue);
			break;
		case CUT_FROM_TOP_CHANGE:
			((Picture)s).addToCutFromTop((int) changeValue);
			break;
		case CUT_FROM_RIGHT_CHANGE:
			((Picture)s).addToCutFromRight((int) changeValue);
			break;
		case CUT_FROM_LEFT_CHANGE:
			((Picture)s).addToCutFromLeft((int) changeValue);
			break;
		case ROTATION_CHANGE:
			((Picture)s).setRotation((int) (((Picture)s).getRotation() + changeValue));
			break;
		case ROUND_WIDTH_CHANGE:
			((Rectangle)s).setRoundWidth((int) (((Rectangle)s).getRoundWidth() + changeValue));
			break;
		case ROUND_HEIGHT_CHANGE:
			((Rectangle)s).setRoundHeight((int) (((Rectangle)s).getRoundHeight() + changeValue));
			break;
		default:
			throw new IllegalArgumentException("ID " + changeType + " is not valid for numerical change");
		}
	}

	@Override
	public void undo(Shape s) {
		switch (changeType) {
		case X_CHANGE:
			s.setX(s.getX() - changeValue);
			break;
		case Y_CHANGE:
			s.setY(s.getY() - changeValue);
			break;
		case WIDTH_CHANGE:
			((StretchableShpae)s).setWidth(((StretchableShpae)s).getWidth() - changeValue);
			break;
		case HEIGHT_CHANGE:
			((StretchableShpae)s).setHeight(((StretchableShpae)s).getHeight() - changeValue);
			break;
		case CUT_FROM_BOTTOM_CHANGE:
			((Picture)s).addToCutFromBottom((int) -changeValue);
			break;
		case CUT_FROM_TOP_CHANGE:
			((Picture)s).addToCutFromTop((int) -changeValue);
			break;
		case CUT_FROM_RIGHT_CHANGE:
			((Picture)s).addToCutFromRight((int) -changeValue);
			break;
		case CUT_FROM_LEFT_CHANGE:
			((Picture)s).addToCutFromLeft((int) -changeValue);
			break;
		case ROTATION_CHANGE:
			((Picture)s).setRotation((int) (((Picture)s).getRotation() - changeValue));
			break;
		case ROUND_WIDTH_CHANGE:
			((Rectangle)s).setRoundWidth((int) (((Rectangle)s).getRoundWidth() - changeValue));
			break;
		case ROUND_HEIGHT_CHANGE:
			((Rectangle)s).setRoundHeight((int) (((Rectangle)s).getRoundHeight() - changeValue));
			break;
		default:
			throw new IllegalArgumentException("ID " + changeType + " is not valid for numerical change");
		}
	}
}
