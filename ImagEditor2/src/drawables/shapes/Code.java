package drawables.shapes;

import java.awt.BorderLayout;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

import javax.swing.JDialog;
import javax.swing.JEditorPane;

import drawables.shapes.abstractShapes.Shape;
import drawables.shapes.abstractShapes.TextualShape;
import gui.components.EditPanel;
import main.Main;
import operatins.changes.Change;

public class Code extends Shape implements TextualShape{
	
	private String code;

	private JEditorPane pane;
	
	public Code(String code, boolean isHTML) {
		this.pane = new JEditorPane();
		if (isHTML) {
			this.pane.setContentType("text/html");
		}
		setText(code);
	}
	
	public Code(String line) {
		this(line.split(",", 5));
	}

	public Code(String[] data) {
		super(Double.parseDouble(data[0]), Double.parseDouble(data[1]), 
				Boolean.parseBoolean(data[2]), data[3]);
		this.code = data[4];
	}
	
	static int fileID = 0;

	@Override
	public void draw(Graphics2D g) {
		BufferedImage bf = new BufferedImage(getWidthOnBoard(), getHeightOnBoard(), 
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = bf.createGraphics();
		pane.paint(g2);
		g.drawImage(bf, (int)this.x, (int)this.y, null);
	}

	@Override
	public EditPanel getEditPanel(boolean full, boolean vertical) {
		
		EditPanel positionPanel = createPositionPanel();
		EditPanel textPanel = createTextPanel("Code:");
		EditPanel editPanel = new EditPanel(new BorderLayout()) {
			
			private static final long serialVersionUID = 1L;

			@Override
			public Object[] getData() {
				return null;
			}
			
			@Override
			public LinkedList<Change> getChanges() {
				LinkedList<Change> changes = new LinkedList<>();
				changes.addAll(positionPanel.getChanges());
				changes.addAll(textPanel.getChanges());
				
				return changes;
			}
		};
		editPanel.add(positionPanel, BorderLayout.NORTH);
		editPanel.add(textPanel);
		return editPanel;
	}
	
	@Override
	public String getText() {
		return code;
	}

	@Override
	public void setText(String code) {
		this.code = code;
		Code.this.pane.setText(code);

		JDialog d = new JDialog(Main.f);
		d.add(pane);
		d.pack();
		d.repaint();
		d.dispose();
	}
	
	public static Code createNewDefaultCode() {
		return new Code("<html><i>Your Code</i></html>", true);
	}

	@Override
	public int getWidthOnBoard() {
		return pane.getPreferredSize().width;
	}

	@Override
	public int getHeightOnBoard() {
		return pane.getPreferredSize().height;
	}
	
	@Override
	public String encodeShape() {
		return super.encodeShape() + "," + this.code;
	}
}
