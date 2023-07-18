package operatins.changes;

import drawables.shapes.abstractShapes.Shape;

public abstract class Change {
	
	//Shape Changes
	public static final int X_CHANGE = 0;
	public static final int Y_CHANGE = 1;
	public static final int NAME_CHANGE = 2;
	public static final int VISIBILITY_CHANGE = 3;
	
	//Stretchable Shape Changes
	public static final int WIDTH_CHANGE = 4;
	public static final int HEIGHT_CHANGE = 5;
	
	//Picture Changes
	public static final int CUT_FROM_LEFT_CHANGE = 6;
	public static final int CUT_FROM_RIGHT_CHANGE = 7;
	public static final int CUT_FROM_BOTTOM_CHANGE = 8;
	public static final int CUT_FROM_TOP_CHANGE = 9;
	public static final int SRC_IMAGE_CHANGE = 10;
	public static final int ROTATION_CHANGE = 20;
	public static final int PREVIEW_CHANGE = 21;
	public static final int SRC_PREVIEW_CHANGE = 22;
	
	//Code Changes
	public static final int CODE_CHANGE = 11;
	
	//Text Changes
	public static final int TEXT_COLOR_CHANGE = 12;
	public static final int FONT_CHANGE = 13;
	public static final int TEXT_CHANGE = 14;
	
	//Rectangle Changes
	public static final int ROUND_WIDTH_CHANGE = 15;
	public static final int ROUND_HEIGHT_CHANGE = 16;
	public static final int IS_FILLED_CHANGE = 17;
	public static final int RECTANGLE_COLOR_CHANGE = 18;
	
	//Layer Change
	public static final int LAYER_TOP_CHANGE = 19;
	
	
	protected int fieldID;
	
	public Change(int fieldID) {
		this.fieldID = fieldID;
	}
	public abstract void apply(Shape s);
	public abstract void undo(Shape s);
}
