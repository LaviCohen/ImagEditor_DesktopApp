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
import javax.swing.JPanel;
import javax.swing.JTextField;

import le.gui.dialogs.LDialogs;
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
		positionPanel.add(Main.theme.affect(new JLabel("X:")));
		JTextField xField = new JTextField(this.x + "");
		Main.theme.affect(xField);
		positionPanel.add(xField);
		positionPanel.add(Main.theme.affect(new JLabel("Y:")));
		JTextField yField = new JTextField(this.y + "");
		Main.theme.affect(yField);
		positionPanel.add(yField);
		editDialog.add(positionPanel);
		JPanel sizePanel = new JPanel(new GridLayout(1, 4));
		sizePanel.add(Main.theme.affect(new JLabel("Width:")));
		JTextField widthField = new JTextField(this.width + "");
		Main.theme.affect(widthField);
		sizePanel.add(widthField);
		sizePanel.add(Main.theme.affect(new JLabel("Height:")));
		JTextField heightField = new JTextField(this.height + "");
		Main.theme.affect(heightField);
		sizePanel.add(heightField);
		editDialog.add(sizePanel);
		JPanel colorPanel = new JPanel(new BorderLayout());
		colorPanel.add(Main.theme.affect(new JLabel("Color:")), Main.translator.getBeforeTextBorder());
		JLabel colorLabel = new JLabel();
		colorLabel.setOpaque(true);
		colorLabel.setBackground(color);
		colorPanel.add(colorLabel);
		JButton setColorButton = new JButton("Set Color");
		Main.theme.affect(setColorButton);
		setColorButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				colorLabel.setBackground(JColorChooser.showDialog(editDialog, "Choose Rectangle color", color));
			}
		});
		colorPanel.add(setColorButton, Main.translator.getAfterTextBorder());
		editDialog.add(colorPanel);
		JButton apply = new JButton("Apply");
		Main.theme.affect(apply);
		apply.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					int x = Integer.parseInt(xField.getText());
					int y = Integer.parseInt(yField.getText());
					int width = Integer.parseInt(widthField.getText());
					int height = Integer.parseInt(heightField.getText());
					Color color = colorLabel.getBackground();
					Rectangle.this.x = x;
					Rectangle.this.y = y;
					Rectangle.this.width = width;
					Rectangle.this.height = height;
					Rectangle.this.color = color;
					Main.getShapeList().updateImage(Rectangle.this);
					editDialog.dispose();
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
					int x = Integer.parseInt(xField.getText());
					int y = Integer.parseInt(yField.getText());
					int width = Integer.parseInt(widthField.getText());
					int height = Integer.parseInt(heightField.getText());
					Color color = colorLabel.getBackground();
					Rectangle.this.x = x;
					Rectangle.this.y = y;
					Rectangle.this.width = width;
					Rectangle.this.height = height;
					Rectangle.this.color = color;
					Main.getShapeList().updateImage(Rectangle.this);
					Main.getBoard().repaint();
				} catch (Exception e2) {
					LDialogs.showMessageDialog(Main.f, "Invalid input", "Error", LDialogs.ERROR_MESSAGE);
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