package operatins.changes;

import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.File;

import drawables.shapes.Picture;
import drawables.shapes.Text;
import drawables.shapes.abstractShapes.ColoredShape;
import drawables.shapes.abstractShapes.Shape;
import drawables.shapes.abstractShapes.TextualShape;
import main.Main;

public class ObjectChange extends Change{

	private Object oldObject;
	private Object newObject;
	
	public ObjectChange(int fieldID, Object oldObject, Object newObject) {
		super(fieldID);
		this.oldObject = oldObject;
		this.newObject = newObject;
	}

	public void change(Shape s, Object to) {
		switch (fieldID) {
		case NAME_CHANGE:
			s.setName((String) to);
			break;
		case TEXT_CHANGE:
			((TextualShape)s).setText((String) to);
			break;
		case SRC_IMAGE_CHANGE:
			((Picture)s).setImage((BufferedImage) to);
			break;
		case FONT_CHANGE:
			((Text)s).setFont((Font)to);
			break;
		case COLOR_CHANGE:
			((ColoredShape)s).setColor((Color)to);
			break;
		case LAYER_TOP_CHANGE:
			Main.getLayersList().getLayerForShape(s).setTop((BufferedImage)to);
			break;
		case SRC_PREVIEW_CHANGE:
			((Picture)s).setSource((File)to);
			break;
		default:
			throw new IllegalArgumentException("ID " + fieldID + " is not valid for String change");
		}
	}
	
	@Override
	public void apply(Shape s) {
		change(s, newObject);
	}
	
	@Override
	public void undo(Shape s) {
		change(s, oldObject);
	}

}
