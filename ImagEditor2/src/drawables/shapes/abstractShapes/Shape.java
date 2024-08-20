package drawables.shapes.abstractShapes;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;

import drawables.Drawable;
import drawables.shapes.Code;
import drawables.shapes.Picture;
import drawables.shapes.Rectangle;
import drawables.shapes.Text;
import gui.components.EditPanel;
import le.gui.dialogs.LDialogs;
import main.Actions;
import main.Main;
import operatins.ChangesOperation;
import operatins.OperationsManager;
import operatins.changes.Change;
import operatins.changes.ChangeType;
import operatins.changes.NumericalChange;

public abstract class Shape implements Drawable{
	public static final int DEFAULT_X = 0;
	public static final int DEFAULT_Y = 0;
	public static final boolean DEFAULT_VISIBLE = true;
	protected double x;
	protected double y;
	protected int id;
	protected boolean visible;
	protected String name;
	protected static int shapesCount = 1;
	public Shape(double x, double y, boolean visible, String name) {
		super();
		this.x = x;
		this.y = y;
		this.visible = visible;
		this.id = shapesCount;
		if (name != null) {
			this.name = name;
		}else {
			this.name = getDefaultName();
		}
		shapesCount++;
	}
	public Shape() {
		this(DEFAULT_X, DEFAULT_Y, DEFAULT_VISIBLE, null);
	}
	public double getX() {
		return x;
	}
	public void setX(double x) {
		this.x = x;
	}
	public double getY() {
		return y;
	}
	public void setY(double y) {
		this.y = y;
	}
	public boolean isVisible() {
		return visible;
	}
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setLocation(Point p) {
		setX(p.getX());
		setY(p.getY());
	}
	public abstract EditPanel getEditPanel(boolean dialog);
	public void edit() {
		
		Main.getEditShapesTopPanel().removeAll();
		
		Main.getEditShapesTopPanel().setLayout(new BorderLayout(15, 0));
		
		Main.getEditShapesTopPanel().add(new JLabel(Shape.this.name + ":"), Main.translator.getBeforeTextBorder());
		
		EditPanel editPanel = getEditPanel(false);
		
		Main.getEditShapesTopPanel().add(editPanel);
		
		ActionListener al = new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getActionCommand().equals("Pop Out")) {
					JDialog editDialog = new JDialog(Main.f, "Editing " + Shape.this.name);
					EditPanel editPanel = getEditPanel(true);
					editDialog.add(editPanel);
					editDialog.add(createActionPanel(false,  new ActionListener() {
						
						@Override
						public void actionPerformed(ActionEvent e) {
							LinkedList<Change> changes = editPanel.getChanges();
							if (!changes.isEmpty()) {
								OperationsManager.operate(new ChangesOperation(Shape.this, changes));
								invalidate();
								Main.getLayersList().updateImage(Shape.this);
								Main.getBoard().repaint();
							}
							
							if (e.getActionCommand().equals("Apply & Close")) {
								editDialog.dispose();
							}
						}
					}), BorderLayout.SOUTH);
					Main.theme.affect(editDialog);
					editDialog.pack();
					moveDialogToCorrectPos(editDialog);
					editDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					editDialog.setVisible(true);
				} else if(e.getActionCommand().equals("Apply")) {
					LinkedList<Change> changes = editPanel.getChanges();
					if (!changes.isEmpty()) {
						OperationsManager.operate(new ChangesOperation(Shape.this, changes));
						invalidate();
						Main.getLayersList().updateImage(Shape.this);
						Main.getBoard().repaint();
					}
				}
			}
		};
		
		Main.getEditShapesTopPanel().add(createActionPanel(true, al), Main.translator.getAfterTextBorder());
		
		Main.theme.affect(Main.getEditShapesTopPanel());
		
		Main.f.revalidate();
		Main.f.repaint();
	}
	public abstract int getWidthOnBoard();
	public abstract int getHeightOnBoard();
	@Override
	public String toString() {
		return this.name;
	}
	public String getDefaultName() {
		return this.getClass().getSimpleName() + " " + shapesCount;
	}
	public String describe() {
		return "id:" + id + "\nx:" + x + "\ny" + y + "\nvisible:" + visible + "\nname:" + name;
	}
	public String encodeShape() {
		return this.getClass().getName() + ":" + x + "," + y + "," + visible + "," + name;
	}
	public double getScreenYCoordinates() {
		return Shape.this.getY() * Main.getBoard().getZoomRate() + 
				Main.getBoard().getTopGap() + 
				Main.getBoard().getLocationOnScreen().y;
	}
	public double getScreenXCoordinates() {
		return Shape.this.getX() * Main.getBoard().getZoomRate() + 
				Main.getBoard().getLeftGap() + 
				Main.getBoard().getLocationOnScreen().x;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public JPopupMenu getPopupMenuForShape() {
		JPopupMenu popup = new JPopupMenu("Options");
		Main.theme.affect(popup);
		JMenuItem setName = new JMenuItem(Main.translator.get("Set Name"));
		Main.theme.affect(setName);
		setName.addActionListener(new ActionListener() {
	
			@Override
			public void actionPerformed(ActionEvent e) {
				Shape.this.setName(LDialogs
						.showInputDialog(null, Main.translator.get("Enter the new name for") + " \"" + Shape.this.getName() + "\"", null));
				Main.updateLayersList();
			}
		});
		popup.add(setName);
		JMenuItem edit = new JMenuItem(Main.translator.get("Edit"));
		Main.theme.affect(edit);
		edit.addActionListener(new ActionListener() {
	
			@Override
			public void actionPerformed(ActionEvent e) {
				Shape.this.edit();
			}
		});
		popup.add(edit);
		popup.addSeparator();
		JMenuItem oneLayerUp = new JMenuItem(Main.translator.get("One Layer Up"));
		Main.theme.affect(oneLayerUp);
		oneLayerUp.addActionListener(new ActionListener() {
	
			@Override
			public void actionPerformed(ActionEvent e) {
				Actions.moveOneLayerUp(Main.getLayersList().getLayerForShape(Shape.this));
				Main.getBoard().repaint();
				Main.updateLayersList();
			}
		});
		popup.add(oneLayerUp);
		JMenuItem oneLayerDown = new JMenuItem(Main.translator.get("One Layer Down"));
		Main.theme.affect(oneLayerDown);
		oneLayerDown.addActionListener(new ActionListener() {
	
			@Override
			public void actionPerformed(ActionEvent e) {
				Actions.moveOneLayerDown(Main.getLayersList().getLayerForShape(Shape.this));
				Main.getBoard().repaint();
				Main.updateLayersList();
			}
		});
		popup.add(oneLayerDown);
		return popup;
	}
	public static Shape parseShape(String line) throws NumberFormatException, IOException {
		if (line.startsWith(Rectangle.class.getName())) {
			return new Rectangle(line.substring(line.indexOf(':') + 1));
		}else if (line.startsWith(Text.class.getName())) {
			return new Text(line.substring(line.indexOf(':') + 1));
		}else if (line.startsWith(Picture.class.getName())) {
			return new Picture(line.substring(line.indexOf(':') + 1));
		}else if (line.startsWith(Code.class.getName())) {
			return new Code(line.substring(line.indexOf(':') + 1));
		}
		return null;
	}
	
	protected EditPanel createPositionPanel() {
		JTextField xField = new JTextField(this.x + "");
		Main.theme.affect(xField);
		JTextField yField = new JTextField(this.y + "");
		Main.theme.affect(yField);
		EditPanel positionPanel = new EditPanel(new GridLayout(1, 4)) {
			private static final long serialVersionUID = 1L;
			
			@Override
			public LinkedList<Change> getChanges() {
				double x = Double.parseDouble(xField.getText());
				double y = Double.parseDouble(yField.getText());
				
				LinkedList<Change> changes = new LinkedList<>();
				if (Shape.this.x != x) {
					changes.add(new NumericalChange(ChangeType.X_CHANGE, x - Shape.this.x));
				}
				if (Shape.this.y != y) {
					changes.add(new NumericalChange(ChangeType.Y_CHANGE, y - Shape.this.y));
				}
				return changes;
			}
		};
		positionPanel.add(Main.theme.affect(new JLabel("X:")));
		positionPanel.add(xField);
		positionPanel.add(Main.theme.affect(new JLabel("Y:")));
		positionPanel.add(yField);
		
		return positionPanel;
	}
	
	protected JPanel createActionPanel(boolean top, ActionListener actionListener) {
		JPanel actionPanel = new JPanel(new BorderLayout());
		JButton firstButton = new JButton(top ? "Apply" : "Apply & Close");
		JButton secondBottoun = new JButton(top ? "Pop Out" : "Apply");
		firstButton.addActionListener(actionListener);
		secondBottoun.addActionListener(actionListener);
		actionPanel.add(firstButton);
		actionPanel.add(secondBottoun, BorderLayout.EAST);
		return actionPanel;
	}
	protected void moveDialogToCorrectPos(JDialog dialog){
		double xOnScreen, yOnScreen;
		xOnScreen = Shape.this.getScreenXCoordinates();
		if (Shape.this.getScreenYCoordinates() - 20 > dialog.getHeight()) {
			yOnScreen = Shape.this.getScreenYCoordinates() - 10 - dialog.getHeight();
		} else {
			yOnScreen = Shape.this.getScreenYCoordinates() + Shape.this.getHeightOnBoard() + 10;
		}
		int x = (int) (xOnScreen - Main.f.getLocationOnScreen().x - 16);

		int y = (int) (yOnScreen - Main.f.getLocationOnScreen().y - 16);
		if (x > Main.f.getWidth() + Main.f.getLocationOnScreen().x - dialog.getWidth()) {
			x = Main.f.getWidth() + Main.f.getLocationOnScreen().x - dialog.getWidth();
		}
		if (y > Main.f.getHeight() + Main.f.getLocationOnScreen().y - dialog.getHeight()) {
			y = Main.f.getHeight() + Main.f.getLocationOnScreen().y - dialog.getHeight();
		}
		if (x < 0) {
			x = 0;
		}
		if (y < 0) {
			y = 0;
		}
		dialog.setLocation(x, y);
	}
	
	/** 
	 * This method tells the shape that a change might happen, so it won't use pre-calculation
	 * and or rendering to its next draw call.
	 * Since not all the shapes needs it, it is not an abstract method, and it being overridden as needed.
	 */
	public void invalidate() {}
}