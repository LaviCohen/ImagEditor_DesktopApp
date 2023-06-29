package operatins;

import main.Main;

public class SetPaperSizeOperation implements Operation{

	private int oldWidth, oldHeight, newWidth, newHeight;

	public SetPaperSizeOperation(int oldWidth, int oldHeight, int newWidth, int newHeight) {
		super();
		this.oldWidth = oldWidth;
		this.oldHeight = oldHeight;
		this.newWidth = newWidth;
		this.newHeight = newHeight;
	}

	@Override
	public void undo() {
		Main.getBoard().setPaperSize(oldWidth, oldHeight);
	}

	@Override
	public void redo() {
		Main.getBoard().setPaperSize(newWidth, newHeight);	
	}
	
	
}
