package operatins.changes;

import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;

import shapes.Code;
import shapes.Picture;
import shapes.Rectangle;
import shapes.Text;
import shapes.abstractShapes.Shape;

public class ObjectChange extends Change{

	private Object oldObject;
	private Object newObject;
	
	public ObjectChange(int fieldID, Object oldString, Object newString) {
		super(fieldID);
		this.oldObject = oldString;
		this.newObject = newString;
	}

	@Override
	public void apply(Shape s) {
		switch (fieldID) {
		case NAME_CHANGE:
			s.setName((String) newObject);
			break;
		case TEXT_CHANGE:
			((Text)s).setText((String) newObject);
			break;
		case CODE_CHANGE:
			((Code)s).setCode((String) newObject);
			break;
		case SRC_IMAGE_CHANGE:
			((Picture)s).setImage((BufferedImage) newObject);
			break;
		case FONT_CHANGE:
			((Text)s).setFont((Font)newObject);
			break;
		case TEXT_COLOR_CHANGE:
			((Text)s).setColor((Color)newObject);
			break;
		case RECTANGLE_COLOR_CHANGE:
			((Rectangle)s).setColor((Color)newObject);
			break;
		default:
			throw new IllegalArgumentException("ID " + fieldID + " is not valid for String change");
		}
	}

	@Override
	public void undo(Shape s) {
		switch (fieldID) {
		case NAME_CHANGE:
			s.setName((String) oldObject);
			break;
		case TEXT_CHANGE:
			((Text)s).setText((String) oldObject);
			break;
		case CODE_CHANGE:
			((Code)s).setCode((String) oldObject);
			break;
		case SRC_IMAGE_CHANGE:
			System.out.println("Reversing to " + oldObject);
			((Picture)s).setImage((BufferedImage) oldObject);
			break;
		case FONT_CHANGE:
			((Text)s).setFont((Font)oldObject);
			break;
		case TEXT_COLOR_CHANGE:
			((Text)s).setColor((Color)oldObject);
			break;
		case RECTANGLE_COLOR_CHANGE:
			((Rectangle)s).setColor((Color)oldObject);
			break;
		default:
			throw new IllegalArgumentException("ID " + fieldID + " is not valid for String change");
		}
	}

}
