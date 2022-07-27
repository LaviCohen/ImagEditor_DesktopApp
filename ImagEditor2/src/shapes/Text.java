package shapes;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import le.gui.dialogs.LDialogs;
import le.gui.dialogs.LFontChooser;
import le.gui.dialogs.LFontChooser.FontHolder;
import main.Main;
import shapes.abstractShapes.Shape;

public class Text extends Shape{
	Color color;
	Font font;
	String text;
	public Text(int x, int y, boolean visible, String name, Color color, Font font, String text) {
		super(x, y, visible, name);
		this.color = color;
		this.font = font;
		this.text = text;
	}

	@Override
	public void draw(Graphics2D g) {
		g.setColor(color);
		g.setFont(font);
		g.drawString(text, (int)x, (int)y + getHeightOnBoard());
	}
	@Override
	public void edit() {
		JDialog editDialog = new JDialog(Main.f);
		editDialog.setLayout(new GridLayout(5, 1));
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
		editDialog.add(positionPanel);
		JPanel textPanel = new JPanel(new BorderLayout());
		textPanel.add(Main.theme.affect(new JLabel("Text:")), 
				Main.translator.getBeforeTextBorder());
		JTextField textField = new JTextField(text);
		Main.theme.affect(textField);
		textPanel.add(textField);
		editDialog.add(textPanel);
		JPanel colorPanel = new JPanel(new BorderLayout());
		colorPanel.add(Main.theme.affect(new JLabel("Color:")), Main.translator.getBeforeTextBorder());
		JLabel colorLabel = new JLabel();
		colorLabel.setOpaque(true);
		colorLabel.setBackground(color);
		colorPanel.add(colorLabel);
		FontHolder fontHolder = new FontHolder(this.font);
		JButton setFontButton = new JButton("Set Font");
		Main.theme.affect(setFontButton);
		setFontButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				fontHolder.setFont(LFontChooser.openChooseFontDialog(editDialog,
						"Set Font", fontHolder.getFont(), null, Main.theme));
			}
		});
		editDialog.add(setFontButton);
		JButton setColorButton = new JButton("Set Color");
		Main.theme.affect(setColorButton);
		setColorButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				colorLabel.setBackground(JColorChooser.showDialog(editDialog, "Choose Text color", color));
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
					double x = Double.parseDouble(xField.getText());
					double y = Double.parseDouble(yField.getText());
					String text = textField.getText();
					Color color = colorLabel.getBackground();
					Text.this.x = x;
					Text.this.y = y;
					Text.this.text = text;
					Text.this.color = color;
					Text.this.font = fontHolder.getFont();
					editDialog.dispose();
					Main.getShapeList().updateImage(Text.this);
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
					String text = textField.getText();
					Color color = colorLabel.getBackground();
					Text.this.x = x;
					Text.this.y = y;
					Text.this.text = text;
					Text.this.color = color;
					Text.this.font = fontHolder.getFont();
					Main.getShapeList().updateImage(Text.this);
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
		editDialog.setSize(editDialog.getWidth() + 50, editDialog.getHeight());
		editDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		editDialog.setVisible(true);
	}
	@SuppressWarnings("deprecation")
	@Override
	public int getWidthOnBoard() {
		return Toolkit.getDefaultToolkit().getFontMetrics(font).stringWidth(text);
	}
	@SuppressWarnings("deprecation")
	@Override
	public int getHeightOnBoard() {
		return Toolkit.getDefaultToolkit().getFontMetrics(font).getHeight();
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public Font getFont() {
		return font;
	}

	public void setFont(Font font) {
		this.font = font;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	public Text(String[] data) {
		this(Integer.parseInt(data[0]), Integer.parseInt(data[1]), 
				Boolean.parseBoolean(data[2]), data[3], new Color(Integer.parseInt(data[4])),
				new Font(data[5], Integer.parseInt(data[6]), Integer.parseInt(data[7])), data[8]);
	}
	public Text(String line) {
		this(line.split(",", 9));
	}

	@Override
	public String encodeShape() {
		return super.encodeShape() + "," + color.getRGB() + "," + font.getFamily() + "," +
				font.getStyle() + "," + font.getSize() + "," + text;
	}
}
