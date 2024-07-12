package drawables.shapes;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
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
	public void edit() {
		JDialog editDialog = new JDialog(Main.f);
		editDialog.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(3, 3, 3, 3);
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1;
		gbc.weighty = 0;
	    gbc.anchor = GridBagConstraints.FIRST_LINE_START;
	    gbc.gridx  = 0;
	    gbc.gridy = 0;
		editDialog.setTitle("Edit Text");
		EditPanel positionPanel = createPositionPanel();
		editDialog.add(positionPanel, gbc);
		EditPanel textPanel = createTextPanel("Text:");
		gbc.weighty = 3;
		gbc.gridy = 1;
		editDialog.add(textPanel, gbc);
		gbc.weighty = 0;
		EditPanel colorPanel = createColorPanel();
		gbc.gridy = 2;
		editDialog.add(colorPanel, gbc);
		FontHolder fontHolder = new FontHolder(this.font);
		JButton setFontButton = new JButton("Set Font");
		setFontButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				fontHolder.setFont(LFontChooser.openChooseFontDialog(editDialog,
						"Set Font", fontHolder.getFont(), null, Main.theme));
			}
		});
		gbc.gridy = 3;
		editDialog.add(setFontButton, gbc);
		ActionListener actionListener =  new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					LinkedList<Change> changes = new LinkedList<>();
					changes.addAll(positionPanel.getChanges());
					changes.addAll(colorPanel.getChanges());
					changes.addAll(textPanel.getChanges());
					if (!Text.this.font.equals(fontHolder.getFont())) {
						changes.add(new ObjectChange(ChangeType.FONT_CHANGE, Text.this.font, fontHolder.getFont()));
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
		gbc.gridy = 4;
		editDialog.add(createActionPanel(actionListener), gbc);
		Main.theme.affect(editDialog);
		editDialog.pack();
		editDialog.setSize(editDialog.getWidth() + 50, editDialog.getHeight());
		moveDialogToCorrectPos(editDialog);
		editDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		editDialog.setVisible(true);
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
		return (int) (font.getLineMetrics(text, new FontRenderContext(null, false, true)).getHeight() * 0.8);
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
