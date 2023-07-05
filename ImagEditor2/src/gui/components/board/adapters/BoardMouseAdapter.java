package gui.components.board.adapters;

import java.awt.event.MouseAdapter;

import gui.components.board.Board;

public abstract class BoardMouseAdapter extends MouseAdapter{
	
	protected Board parent;
	
	public BoardMouseAdapter(Board parent) {
		this.parent = parent;
	}
	
	protected double getTopGap() {
		return ((parent.getHeight() - (parent.getPaper().getHeight() * parent.getZoomRate()))/2);
	}
	protected double getLeftGap() {
		return ((parent.getWidth()  - (parent.getPaper().getWidth()  * parent.getZoomRate()))/2);
	}
	protected int screenToBoardCoordsX(int screenX) {
		return (int)((screenX - getLeftGap()) / parent.getZoomRate());
	}
	protected int screenToBoardCoordsY(int screenY) {
		return (int)((screenY - getTopGap()) / parent.getZoomRate());
	}
	
	/**
	 * Return if value is between start & start + difference
	 * */
	public static boolean isBetween(double start, double value, double difference) {
		if (difference < 0) {
			start += difference;
			difference = -difference;
		}
		return value >= start && value - difference <= start;
	}
}
