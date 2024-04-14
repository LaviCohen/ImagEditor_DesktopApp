package drawables.shapes;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.font.FontRenderContext;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import drawables.shapes.abstractShapes.ColoredShape;
import drawables.shapes.abstractShapes.Shape;
import gui.components.EditPanel;
import le.gui.dialogs.LDialogs;
import le.gui.dialogs.LFontChooser;
import le.gui.dialogs.LFontChooser.FontHolder;
import main.Main;
import operatins.ChangesOperation;
import operatins.OperationsManager;
import operatins.changes.Change;
import operatins.changes.NumericalChange;
import operatins.changes.ObjectChange;

public class Text extends Shape implements ColoredShape{
	Color color;
	Font font;
	String text;
	public Text(double x, double y, boolean visible, String name, Color color, Font font, String text) {
		super(x, y, visible, name);
		this.color = color;
		this.font = font;
		this.text = text;
	}

	@Override
	public void draw(Graphics2D g) {
		g.setColor(color);
		g.setFont(font);
		g.drawString(text, (int)x, (int) (y + getHeightOnBoard() * 0.75));
	}
	@Override
	public void edit() {
		JDialog editDialog = new JDialog(Main.f);
		editDialog.setLayout(new GridLayout(5, 1));
		editDialog.setTitle("Edit Text");
		EditPanel positionPanel = createPositionPanel();
		editDialog.add(positionPanel);
		JPanel textPanel = new JPanel(new BorderLayout());
		textPanel.add(Main.theme.affect(new JLabel("Text:")), 
				Main.translator.getBeforeTextBorder());
		JTextField textField = new JTextField(text);
		textPanel.add(textField);
		editDialog.add(textPanel);
		EditPanel colorPanel = createColorPanel();
		editDialog.add(colorPanel);
		FontHolder fontHolder = new FontHolder(this.font);
		JButton setFontButton = new JButton("Set Font");
		setFontButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				fontHolder.setFont(LFontChooser.openChooseFontDialog(editDialog,
						"Set Font", fontHolder.getFont(), null, Main.theme));
			}
		});
		editDialog.add(setFontButton);
		ActionListener actionListener =  new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Object[] positionData = positionPanel.getData();
					double x = (Double) positionData[0];
					double y = (Double) positionData[1];
					String text = textField.getText();
					Color color = (Color) colorPanel.getData()[0];
					LinkedList<Change> changes = new LinkedList<>();
					if (Text.this.x != x) {
						changes.add(new NumericalChange(Change.X_CHANGE, x - Text.this.x));
					}
					if (Text.this.y != y) {
						changes.add(new NumericalChange(Change.Y_CHANGE, y - Text.this.y));
					}
					if (!Text.this.text.equals(text)) {
						changes.add(new ObjectChange(Change.TEXT_CHANGE, Text.this.text, text));
					}
					if (!Text.this.color.equals(color)) {
						changes.add(new ObjectChange(Change.TEXT_COLOR_CHANGE, Text.this.color, color));
					}
					if (!Text.this.font.equals(fontHolder.getFont())) {
						changes.add(new ObjectChange(Change.FONT_CHANGE, Text.this.font, fontHolder.getFont()));
					}
					
					if (!changes.isEmpty()) {
						OperationsManager.operate(new ChangesOperation(Text.this, changes));
						Main.getLayersList().updateImage(Text.this);
						Main.getBoard().repaint();
					}
				} catch (Exception e2) {
					LDialogs.showMessageDialog(Main.f, "Invalid input", "Error", LDialogs.ERROR_MESSAGE);
					e2.printStackTrace();
				}
				
				if (e.getActionCommand().equals("Apply & Close")) {
					editDialog.dispose();
				}
			}
		};
		editDialog.add(createActionPanel(actionListener));
		Main.theme.affect(editDialog);
		editDialog.pack();
		editDialog.setSize(editDialog.getWidth() + 50, editDialog.getHeight());
		moveDialogToCorrectPos(editDialog);
		editDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		editDialog.setVisible(true);
	}
	@SuppressWarnings("deprecation")
	@Override
	public int getWidthOnBoard() {
		return Toolkit.getDefaultToolkit().getFontMetrics(font).stringWidth(text);
	}
	@Override
	public int getHeightOnBoard() {
		return (int) font.getLineMetrics(text, new FontRenderContext(null, false, true)).getHeight();
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
		this(Double.parseDouble(data[0]), Double.parseDouble(data[1]), 
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
