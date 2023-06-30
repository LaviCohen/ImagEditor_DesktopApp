package shapes;

import java.awt.BorderLayout;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import le.gui.dialogs.LDialogs;
import main.Main;
import shapes.abstractShapes.Shape;

public class Code extends Shape{
	
	private String code;

	private JEditorPane pane;
	
	public Code(String code, boolean isHTML) {
		this.code = code;
		this.pane = new JEditorPane();
		if (isHTML) {
			this.pane.setContentType("text/html");
		}
		this.pane.setText(code);
	}
	
	public Code(String line) {
		this(line.split(",", 5));
	}

	public Code(String[] data) {
		super(Double.parseDouble(data[0]), Double.parseDouble(data[1]), 
				Boolean.parseBoolean(data[2]), data[3]);
		this.code = data[4];
	}

	@Override
	public void draw(Graphics2D g) {
		BufferedImage bf = new BufferedImage(getWidthOnBoard(), getHeightOnBoard(), 
				BufferedImage.TYPE_INT_ARGB);
		pane.paint(bf.getGraphics());
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
		Main.theme.affect(apply);
		apply.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					double x = Double.parseDouble(xField.getText());
					double y = Double.parseDouble(yField.getText());
					String code = textArea.getText();
					Code.this.x = x;
					Code.this.y = y;
					Code.this.code = code;
					Code.this.pane.setText(code);
					editDialog.dispose();
					Main.getShapeList().updateImage(Code.this);
					Main.getBoard().repaint();
				} catch (Exception e2) {
					LDialogs.showMessageDialog(Main.f, "Invalid input", "Error", LDialogs.ERROR_MESSAGE);
				}
			}
		});
		JButton preview = new JButton("Preview");
		Main.theme.affect(preview);
		preview.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					double x = Double.parseDouble(xField.getText());
					double y = Double.parseDouble(yField.getText());
					String code = textArea.getText();
					Code.this.x = x;
					Code.this.y = y;
					Code.this.code = code;
					Code.this.pane.setText(code);
					Main.getShapeList().updateImage(Code.this);
					Main.getBoard().repaint();
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
