package drawables.shapes;

import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

import javax.swing.JDialog;
import javax.swing.JPanel;

import drawables.shapes.abstractShapes.Shape;
import gui.components.EditPanel;
import main.Main;
import operatins.ChangesOperation;
import operatins.OperationsManager;
import operatins.changes.Change;
import operatins.changes.NumericalChange;

public class GroupShape extends Shape{

	private Shape[] shapes;
	
	private int widthOnBoard;
	private int heightOnBoard;
	
	
	public GroupShape(double x, double y, boolean visible, String name, Shape[] shapes) {
		super(x, y, visible, name);
		this.shapes = shapes;
	}

	@Override
	public void draw(Graphics2D graphics) {
		for (Shape shape : shapes) {
			shape.draw(graphics);
		}
	}

	@Override
	public void edit() {
		JDialog editDialog = new JDialog(Main.f, "Edit");
		
		EditPanel positionPanel = createPositionPanel();
		
		editDialog.add(positionPanel);
		
		ActionListener actionListener = new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Object[] positionData = positionPanel.getData();
				double x = (Double) positionData[0];
				double y = (Double) positionData[1];
				LinkedList<Change> changes = new LinkedList<>();
				if (GroupShape.this.x != x) {
					changes.add(new NumericalChange(Change.X_CHANGE, x - GroupShape.this.x));
				}
				if (GroupShape.this.y != y) {
					changes.add(new NumericalChange(Change.Y_CHANGE, y - GroupShape.this.y));
				}
				
				if (!changes.isEmpty()) {
					OperationsManager.operate(new ChangesOperation(GroupShape.this, changes));
					Main.getLayersList().updateImage(GroupShape.this);
					Main.getBoard().repaint();
				}
				
				if (e.getActionCommand().equals("Apply & Close")) {
					editDialog.dispose();
				}
			}
		};
		
		JPanel actionPanel = createActionPanel(actionListener);
		
		editDialog.add(actionPanel);
		
		editDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		editDialog.pack();
		editDialog.setVisible(true);
		
	}

	@Override
	public int getWidthOnBoard() {
		return widthOnBoard;
	}

	@Override
	public int getHeightOnBoard() {
		return heightOnBoard;
	}

	public Shape[] getShapes() {
		return shapes;
	}
}
