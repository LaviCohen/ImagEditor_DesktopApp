package shapes;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import main.Main;

public class Rectangle extends Shape{

	int width;
	int height;
	Color color;
	
	public Rectangle(int x, int y, boolean visible, String name, int width, int height, Color color) {
		super(x, y, visible, name);
		this.width = width;
		this.height = height;
		this.color = color;
	}

	@Override
	public void draw(Graphics g) {
		g.setColor(color);
		g.fillRect(x, y, width, height);
	}

	@Override
	public void edit() {
		JDialog editDialog = new JDialog(Main.f);
		editDialog.setLayout(new GridLayout(4, 1));
		editDialog.setTitle("Edit Rectangle");
		JPanel positionPanel = new JPanel(new GridLayout(1, 4));
		positionPanel.add(new JLabel("X:"));
		JTextField xField = new JTextField(this.x + "");
		positionPanel.add(xField);
		positionPanel.add(new JLabel("Y:"));
		JTextField yField = new JTextField(this.y + "");
		positionPanel.add(yField);
		editDialog.add(positionPanel);
		JPanel sizePanel = new JPanel(new GridLayout(1, 4));
		sizePanel.add(new JLabel("width:"));
		JTextField widthField = new JTextField(this.width + "");
		sizePanel.add(widthField);
		sizePanel.add(new JLabel("height:"));
		JTextField heightField = new JTextField(this.height + "");
		sizePanel.add(heightField);
		editDialog.add(sizePanel);
		JPanel colorPanel = new JPanel(new BorderLayout());
		colorPanel.add(new JLabel("color:"), Main.translator.getBeforeTextBorder());
		JLabel colorLabel = new JLabel();
		colorLabel.setOpaque(true);
		colorLabel.setBackground(color);
		colorPanel.add(colorLabel);
		JButton setColorButton = new JButton("set color");
		setColorButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				colorLabel.setBackground(JColorChooser.showDialog(editDialog, "Choose Rectangle color", color));
			}
		});
		colorPanel.add(setColorButton, Main.translator.getAfterTextBorder());
		editDialog.add(colorPanel);
		JButton apply = new JButton("Apply");
		final Rectangle cur = this;
		apply.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					int x = Integer.parseInt(xField.getText());
					int y = Integer.parseInt(yField.getText());
					int width = Integer.parseInt(widthField.getText());
					int height = Integer.parseInt(heightField.getText());
					Color color = colorLabel.getBackground();
					cur.x = x;
					cur.y = y;
					cur.width = width;
					cur.height = height;
					cur.color = color;
					Main.getShapeList().updateImage(cur);
					editDialog.dispose();
					Main.getBoard().repaint();
				} catch (Exception e2) {
					JOptionPane.showMessageDialog(Main.f, "Invalid input", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		JButton preview = new JButton("Preview");
		preview.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					int x = Integer.parseInt(xField.getText());
					int y = Integer.parseInt(yField.getText());
					int width = Integer.parseInt(widthField.getText());
					int height = Integer.parseInt(heightField.getText());
					Color color = colorLabel.getBackground();
					cur.x = x;
					cur.y = y;
					cur.width = width;
					cur.height = height;
					cur.color = color;
					Main.getShapeList().updateImage(cur);
					Main.getBoard().repaint();
				} catch (Exception e2) {
					JOptionPane.showMessageDialog(Main.f, "Invalid input", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		JPanel actionPanel = new JPanel(new BorderLayout());
		actionPanel.add(apply);
		actionPanel.add(preview, BorderLayout.EAST);
		editDialog.add(actionPanel);
		editDialog.pack();
		editDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		editDialog.setVisible(true);
	}

	@Override
	public int getWidthOnBoard() {
		return width;
	}

	@Override
	public int getHeightOnBoard() {
		return height;
	}
	public Rectangle(String[] data) {
		this(Integer.parseInt(data[0]), Integer.parseInt(data[1]),
				Boolean.parseBoolean(data[2]), data[3], Integer.parseInt(data[4]),
				Integer.parseInt(data[5]), new Color(Integer.parseInt(data[6])));
	}
	@Override
	public String encodeShape() {
		return super.encodeShape() + "," + width + "," + height + "," + color.getRGB();
	}
}