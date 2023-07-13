package drawables.shapes;

import java.awt.BorderLayout;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import drawables.shapes.abstractShapes.Shape;
import le.gui.dialogs.LDialogs;
import main.Main;
import operatins.ChangesOperation;
import operatins.OperationsManager;
import operatins.changes.Change;
import operatins.changes.NumericalChange;
import operatins.changes.ObjectChange;

public class Code extends Shape{
	
	private String code;

	private JEditorPane pane;
	
	public Code(String code, boolean isHTML) {
		this.pane = new JEditorPane();
		if (isHTML) {
			this.pane.setContentType("text/html");
		}
		setCode(code);
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
	public void edit() {
		JDialog editDialog = new JDialog(Main.f);
		editDialog.setLayout(new BorderLayout());
		editDialog.setTitle("Edit Text");
		JPanel positionPanel = new JPanel(new GridLayout(1, 4));
		positionPanel.add(Main.theme.affect(new JLabel("X:")));
		JTextField xField = new JTextField(this.x + "");
		Main.theme.affect(xField);
		positionPanel.add(xField);
		positionPanel.add(Main.theme.affect(new JLabel("Y:")));
		JTextField yField = new JTextField(this.y + "");
		Main.theme.affect(yField);
		positionPanel.add(yField);
		editDialog.add(positionPanel, BorderLayout.NORTH);
		JPanel textPanel = new JPanel(new BorderLayout());
		textPanel.add(Main.theme.affect(new JLabel("Code:")), 
				BorderLayout.NORTH);
		JTextArea textArea = new JTextArea(code);
		Main.theme.affect(textArea);
		textPanel.add(textArea);
		editDialog.add(textPanel);
		JButton apply = new JButton("Apply");
		JButton preview = new JButton("Preview");
		Main.theme.affect(apply);
		apply.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				preview.getActionListeners()[0].actionPerformed(new ActionEvent(apply, 0, "apply"));
				editDialog.dispose();
			}
		});
		Main.theme.affect(preview);
		preview.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					double x = Double.parseDouble(xField.getText());
					double y = Double.parseDouble(yField.getText());
					String code = textArea.getText();
					LinkedList<Change> changes = new LinkedList<>();
					if (Code.this.x != x) {
						changes.add(new NumericalChange(Change.X_CHANGE, x - Code.this.x));
					}
					if (Code.this.y != y) {
						changes.add(new NumericalChange(Change.Y_CHANGE, y - Code.this.y));
					}
					if (!Code.this.code.equals(code)) {
						changes.add(new ObjectChange(Change.CODE_CHANGE, Code.this.code, code));
					}
					if (!changes.isEmpty()) {
						OperationsManager.operate(new ChangesOperation(Code.this, changes));
						Main.getLayersList().updateImage(Code.this);
						Main.getBoard().repaint();
					}
				} catch (Exception e2) {
					LDialogs.showMessageDialog(Main.f, "Invalid input", "Error", LDialogs.ERROR_MESSAGE);
				}		
			}
		});
		JPanel actionPanel = new JPanel(new BorderLayout());
		actionPanel.add(apply);
		actionPanel.add(preview, BorderLayout.EAST);
		editDialog.add(actionPanel, BorderLayout.SOUTH);
		editDialog.pack();
		editDialog.setSize(editDialog.getWidth() + 50, editDialog.getHeight());
		editDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		editDialog.setVisible(true);
	}
	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
		Code.this.pane.setText(code);

		JDialog d = new JDialog(Main.f);
		d.add(pane);
		d.pack();
		d.repaint();
		d.dispose();
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
