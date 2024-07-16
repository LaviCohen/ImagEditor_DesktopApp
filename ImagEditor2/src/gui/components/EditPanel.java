package gui.components;

import java.awt.LayoutManager;
import java.util.LinkedList;

import javax.swing.JPanel;

import operatins.changes.Change;

public abstract class EditPanel extends JPanel{

	private static final long serialVersionUID = 1L;

	public EditPanel(LayoutManager lm) {
		super(lm);
	}
	
	public abstract LinkedList<Change> getChanges();
}
