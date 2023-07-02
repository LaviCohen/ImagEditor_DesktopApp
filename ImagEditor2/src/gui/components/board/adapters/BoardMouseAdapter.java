package gui.components.board.adapters;

import java.awt.event.MouseAdapter;

import gui.components.board.Board;

public abstract class BoardMouseAdapter extends MouseAdapter{
	
	protected Board parent;
	
	public BoardMouseAdapter(Board parent) {
		this.parent = parent;
	}
	
	public abstract void invalidate();
}
