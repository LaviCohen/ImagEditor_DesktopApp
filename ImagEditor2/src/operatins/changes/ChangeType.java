package operatins.changes;

public enum ChangeType {
	//Shape Changes
	X_CHANGE, 
	Y_CHANGE, 
	NAME_CHANGE, 
	VISIBILITY_CHANGE, 
	
	//Strechable Shape Changes
	WIDTH_CHANGE, 
	HEIGHT_CHANGE, 
	
	//Colored Shape Changes
	COLOR_CHANGE, 
	
	//Textual Shape Changes
	TEXT_CHANGE, 
	
	//Picture Changes
	CUT_FROM_LEFT_CHANGE, CUT_FROM_RIGHT_CHANGE, CUT_FROM_BOTTOM_CHANGE, CUT_FROM_TOP_CHANGE,
	SRC_IMAGE_CHANGE, ROTATION_CHANGE, TRANSPARENCY_CHANGE, PREVIEW_CHANGE, SRC_PREVIEW_CHANGE, 
	
	//Text Changes
	FONT_CHANGE, 
	
	//Rectangle Changes
	ROUND_WIDTH_CHANGE, ROUND_HEIGHT_CHANGE, IS_FILLED_CHANGE, 
	
	//Layer's Top Changes
	LAYER_TOP_CHANGE
}
