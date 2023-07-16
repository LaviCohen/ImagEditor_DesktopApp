package drawables.shapes;

import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

import javax.swing.JDialog;
import javax.swing.JPanel;

import drawables.Layer;
import drawables.shapes.abstractShapes.Shape;
import gui.components.EditPanel;
import main.Main;
import operatins.ChangesOperation;
import operatins.OperationsManager;
import operatins.changes.Change;
import operatins.changes.NumericalChange;

public class GroupShape extends Shape{

	private Layer[] layers;
	
	private int widthOnBoard;
	private int heightOnBoard;
	
	
	public GroupShape(boolean visible, String name, Shape[] shapes) {
		super(0, 0, visible, name);
		this.layers = new Layer[shapes.length];
		for (int i = 0; i < shapes.length; i++) {
			layers[i] = Main.getLayersList().getLayerForShape(shapes[i]);
		}
		calculateXnYonBoard();
		calculateWnHonBoard();
	}

	private void calculateXnYonBoard() {
		double minX = layers[0].getShape().getX();
		double minY = layers[0].getShape().getY();
		for (Layer layer : layers) {
			Shape shape = layer.getShape();
			if (shape.getX() < minX) {
				minX = shape.getX();
			}
			if (shape.getY() < minY) {
				minY = shape.getY();
			}
		}
		for (Layer layer : layers) {
			Shape shape = layer.getShape();
			shape.setX(shape.getX() - minX);
			shape.setY(shape.getY() - minY);
		}
		this.setX(minX);
		this.setY(minY);
	}

	private void calculateWnHonBoard() {
		double maxWidthOnBoard = 0;
		double maxHeightOnBoard = 0;
		for (Layer layer : layers) {
			Shape shape = layer.getShape();
			if (shape.getX() + shape.getWidthOnBoard() > maxWidthOnBoard) {
				maxWidthOnBoard = shape.getX() + shape.getWidthOnBoard();
			}
			if (shape.getY() + shape.getHeightOnBoard() > maxHeightOnBoard) {
				maxHeightOnBoard = shape.getY() + shape.getHeightOnBoard();
			}
		}
		this.widthOnBoard = (int) maxWidthOnBoard;
		this.heightOnBoard = (int) maxHeightOnBoard;
	}

	@Override
	public void draw(Graphics2D graphics) {
		for (Layer layer : layers) {
			layer.getShape().setX(layer.getShape().getX() + this.x);
			layer.getShape().setY(layer.getShape().getY() + this.y);
			layer.draw(graphics);
			layer.getShape().setX(layer.getShape().getX() - this.x);
			layer.getShape().setY(layer.getShape().getY() - this.y);
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

	public Layer[] getLayers() {
		return layers;
	}
}
