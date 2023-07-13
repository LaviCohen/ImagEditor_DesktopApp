package gui.components;

import java.awt.LayoutManager;

import javax.swing.JPanel;

public abstract class EditPanel extends JPanel{

	private static final long serialVersionUID = 1L;

	public EditPanel(LayoutManager lm) {
		super(lm);
	}
	
	public abstract Object[] getData();
}
