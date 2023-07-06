package gui.components.board.adapters;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import gui.components.board.Board;
import main.Actions;

public abstract class BoardMouseAdapter extends MouseAdapter{
	
	protected Board parent;
	
	public BoardMouseAdapter(Board parent) {
		this.parent = parent;
	}
	
	public void openAddShapePopupMenu(MouseEvent e) {
		JPopupMenu addPopupMenu = new JPopupMenu("Add Shape");
		JMenuItem addText = new JMenuItem("Text");
		addText.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Actions.addText();
			}
		});
		addPopupMenu.add(addText);
		JMenuItem addRectengle = new JMenuItem("Rectengle");
		addRectengle.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Actions.addRectangle();
			}
		});
		addPopupMenu.add(addRectengle);
		JMenuItem addPicture = new JMenuItem("Picture");
		addPicture.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Actions.addPicture();
			}
		});
		addPopupMenu.add(addPicture);
		JMenuItem addCode = new JMenuItem("Code");
		addCode.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Actions.addCode();
			}
		});
		addPopupMenu.add(addCode);
		addPopupMenu.show(parent, e.getX(), e.getY());
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
