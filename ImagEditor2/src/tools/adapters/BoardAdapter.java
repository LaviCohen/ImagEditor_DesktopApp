package tools.adapters;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import drawables.shapes.Code;
import drawables.shapes.Picture;
import drawables.shapes.Rectangle;
import drawables.shapes.Text;
import drawables.shapes.abstractShapes.Shape;
import gui.components.Board;
import main.Actions;

public abstract class BoardAdapter extends MouseAdapter{
	
	protected Board parent;
	
	public BoardAdapter(Board parent) {
		this.parent = parent;
	}
	
	public void rightClick(MouseEvent e) {
		rightClick(getShapeAt(e), e);
	}
	
	public void rightClick(Shape shapeInFocus, MouseEvent e) {
		if (shapeInFocus != null) {
			shapeInFocus.getPopupMenuForShape().show(parent, e.getX(), e.getY());
		} else {
			openAddShapePopupMenu(e);
		}
	}
	
	public Shape getShapeAt(MouseEvent e){
		Shape shapeInFocus = parent.getShapeAt(boardToPaperCoordinatesX(e.getX()), boardToPaperCoordinatesY(e.getY()));
		if (shapeInFocus != null && !shapeInFocus.isVisible()) {
			shapeInFocus = null;
		}
		return shapeInFocus;
	}
	
	public void openAddShapePopupMenu(MouseEvent mouseEvent) {
		JPopupMenu addPopupMenu = new JPopupMenu("Add Shape");
		JMenuItem addText = new JMenuItem("Text");
		addText.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Text t = Text.createNewDefaultText();
				t.setLocation(boardToPaperCoordinatesPoint(mouseEvent.getPoint()));
				Actions.addShape(t);
			}
		});
		addPopupMenu.add(addText);
		JMenuItem addRectengle = new JMenuItem("Rectengle");
		addRectengle.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Rectangle r = Rectangle.createNewDefaultRectangle();
				r.setLocation(boardToPaperCoordinatesPoint(mouseEvent.getPoint()));
				Actions.addShape(r);
			}
		});
		addPopupMenu.add(addRectengle);
		JMenuItem addPicture = new JMenuItem("Picture");
		addPicture.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Picture p = Picture.createNewDefaultPicture();
				p.setLocation(boardToPaperCoordinatesPoint(mouseEvent.getPoint()));
				Actions.addShape(p);
			}
		});
		addPopupMenu.add(addPicture);
		JMenuItem addCode = new JMenuItem("Code");
		addCode.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Code c = Code.createNewDefaultCode();
				c.setLocation(boardToPaperCoordinatesPoint(mouseEvent.getPoint()));
				Actions.addShape(c);
			}
		});
		addPopupMenu.add(addCode);
		addPopupMenu.show(parent, mouseEvent.getX(), mouseEvent.getY());
	}
	
	protected Point boardToPaperCoordinatesPoint(Point p) {
		return new Point(boardToPaperCoordinatesX(p.x), boardToPaperCoordinatesY(p.y));
	}
	
	protected int boardToPaperCoordinatesX(int boardX) {
		return parent.boardToPaperCoordinatesX(boardX);
	}
	protected int boardToPaperCoordinatesY(int boardY) {
		return parent.boardToPaperCoordinatesY(boardY);
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
