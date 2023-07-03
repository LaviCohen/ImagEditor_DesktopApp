package operatins.changes;

import drawables.shapes.Picture;
import drawables.shapes.Rectangle;
import drawables.shapes.abstractShapes.Shape;
import drawables.shapes.abstractShapes.StretchableShpae;

public class NumericalChange extends Change{
	
	protected double changeValue;
	
	public NumericalChange(int fieldID, double changeValue) {
		super(fieldID);
		this.changeValue = changeValue;
	}

	@Override
	public void apply(Shape s) {
		switch (fieldID) {
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
			((Picture)s).setCutFromBottom(((Picture)s).getCutFromBottom() + changeValue);
			break;
		case CUT_FROM_TOP_CHANGE:
			((Picture)s).setCutFromTop(((Picture)s).getCutFromTop() + changeValue);
			break;
		case CUT_FROM_RIGHT_CHANGE:
			((Picture)s).setCutFromRight(((Picture)s).getCutFromRight() + changeValue);
			break;
		case CUT_FROM_LEFT_CHANGE:
			((Picture)s).setCutFromLeft(((Picture)s).getCutFromLeft() + changeValue);
			break;
		case ROUND_WIDTH_CHANGE:
			((Rectangle)s).setRoundWidth((int) (((Rectangle)s).getRoundWidth() + changeValue));
			break;
		case ROUND_HEIGHT_CHANGE:
			((Rectangle)s).setRoundHeight((int) (((Rectangle)s).getRoundHeight() + changeValue));
			break;
		default:
			throw new IllegalArgumentException("ID " + fieldID + " is not valid for numerical change");
		}
	}

	@Override
	public void undo(Shape s) {
		switch (fieldID) {
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
			((Picture)s).setCutFromBottom(((Picture)s).getCutFromBottom() - changeValue);
			break;
		case CUT_FROM_TOP_CHANGE:
			((Picture)s).setCutFromTop(((Picture)s).getCutFromTop() - changeValue);
			break;
		case CUT_FROM_RIGHT_CHANGE:
			((Picture)s).setCutFromRight(((Picture)s).getCutFromRight() - changeValue);
			break;
		case CUT_FROM_LEFT_CHANGE:
			((Picture)s).setCutFromLeft(((Picture)s).getCutFromLeft() - changeValue);
			break;
		case ROUND_WIDTH_CHANGE:
			((Rectangle)s).setRoundWidth((int) (((Rectangle)s).getRoundWidth() - changeValue));
			break;
		case ROUND_HEIGHT_CHANGE:
			((Rectangle)s).setRoundHeight((int) (((Rectangle)s).getRoundHeight() - changeValue));
			break;
		default:
			throw new IllegalArgumentException("ID " + fieldID + " is not valid for numerical change");
		}
	}
}
