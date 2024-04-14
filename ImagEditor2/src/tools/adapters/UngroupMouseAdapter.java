package tools.adapters;

import java.awt.event.MouseEvent;

import gui.components.Board;

public class UngroupMouseAdapter extends BoardAdapter {

	public UngroupMouseAdapter(Board parent) {
		super(parent);
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON3) {
			rightClick(e);
			return;
		}
	}
}