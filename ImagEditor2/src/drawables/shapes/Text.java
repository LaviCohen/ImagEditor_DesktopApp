package drawables.shapes;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.font.FontRenderContext;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JDialog;

import drawables.shapes.abstractShapes.ColoredShape;
import drawables.shapes.abstractShapes.Shape;
import drawables.shapes.abstractShapes.TextualShape;
import gui.components.EditPanel;
import install.Preferences;
import le.gui.dialogs.LDialogs;
import le.gui.dialogs.LFontChooser;
import le.gui.dialogs.LFontChooser.FontHolder;
import main.Main;
import operatins.ChangesOperation;
import operatins.OperationsManager;
import operatins.changes.Change;
import operatins.changes.ChangeType;
import operatins.changes.ObjectChange;

public class Text extends Shape implements ColoredShape, TextualShape{
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
		double totalHeight = 0;
		for (String line: text.split("\n")) {
			totalHeight += getHeightOfLine(line);
			g.drawString(line, (int)x, (int) (y + totalHeight));
		}
	}
	@Override
	public EditPanel getEditPanel(boolean dialog) {
		EditPanel positionPanel = createPositionPanel();
		EditPanel textPanel = createTextPanel("Text:");
		EditPanel colorPanel = createColorPanel();
		FontHolder fontHolder = new FontHolder(this.font);
		JButton setFontButton = new JButton("Set Font");
		setFontButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				fontHolder.setFont(LFontChooser.openChooseFontDialog(setFontButton.getParent(),
						"Set Font", fontHolder.getFont(), null, Main.theme));
			}
		});
		EditPanel editDialog = new EditPanel(dialog ? new GridBagLayout() : new GridLayout(1, 5, 15, 0)) {
			private static final long serialVersionUID = 1L;

			@Override
			public LinkedList<Change> getChanges() {
				try {
					LinkedList<Change> changes = new LinkedList<>();
					if (dialog || Preferences.showPositionOnTop) {
						changes.addAll(positionPanel.getChanges());
					}
					changes.addAll(colorPanel.getChanges());
					if (dialog) {
						changes.addAll(textPanel.getChanges());
					}
					if (!Text.this.font.equals(fontHolder.getFont())) {
						changes.add(new ObjectChange(ChangeType.FONT_CHANGE, Text.this.font, fontHolder.getFont()));
					}
					return changes;
				} catch (Exception e2) {
					LDialogs.showMessageDialog(Main.f, "Invalid input", "Error", LDialogs.ERROR_MESSAGE);
					e2.printStackTrace();
				}
				return null;
			}
		};
		if (dialog) {
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.insets = new Insets(3, 3, 3, 3);
			gbc.fill = GridBagConstraints.BOTH;
			gbc.weightx = 1;
			gbc.weighty = 0;
		    gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		    gbc.gridx  = 0;
		    gbc.gridy = 0;
			editDialog.add(positionPanel, gbc);
			gbc.weighty = 3;
			gbc.gridy = 1;
			editDialog.add(textPanel, gbc);
			gbc.weighty = 0;
			gbc.gridy = 2;
			editDialog.add(colorPanel, gbc);
			gbc.gridy = 3;
			editDialog.add(setFontButton, gbc);
		} else {
			if (Preferences.showPositionOnTop) {
				editDialog.add(positionPanel);
			}
			editDialog.add(colorPanel);
			editDialog.add(setFontButton);
		}
		return editDialog;
	}
	public static Text createNewDefaultText() {
		return new Text(0, 0, true, null, Color.BLACK, new Font("Arial", Font.PLAIN, 20), "Text");
	}
	@Override
	public int getWidthOnBoard() {
		double max = 0;
		for(String line: text.split("\n")) {
			double width = getWidthOfLine(line);
			if (width > max) {
				max = width;
			}
		}
		return (int)max;
	}
	@Override
	public int getHeightOnBoard() {
		double total = 0;
		for(String line: text.split("\n")) {
			total += getHeightOfLine(line);
		}
		return (int)total;
	}

	@SuppressWarnings("deprecation")
	public int getWidthOfLine(String text) {
		return Toolkit.getDefaultToolkit().getFontMetrics(font).stringWidth(text);
	}
	
	public int getHeightOfLine(String text) {
		return (int) (font.getLineMetrics(text, new FontRenderContext(null, false, true)).getHeight());
	}
	
	@Override
	public Color getColor() {
		return color;
	}

	@Override
	public void setColor(Color color) {
		this.color = color;
	}

	public Font getFont() {
		return font;
	}

	public void setFont(Font font) {
		this.font = font;
	}

	@Override
	public String getText() {
		return text;
	}
	
	@Override
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
