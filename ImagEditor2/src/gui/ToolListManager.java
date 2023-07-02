package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;

import gui.layouts.ListLayout;
import install.Resources;
import main.Main;

public class ToolListManager {

	public static final int PICKER_TOOL = 0;
	public static final int BRUSH_TOOL = 1;
	

	private static int currentTool = 0;
	
	
	private static JLabel pickerToolLabel;
	private static JLabel brushToolLabel;
	
	public static JPanel createToolsPanel() {
		JPanel toolsSideBarPanel = new JPanel();
		toolsSideBarPanel.setLayout(new ListLayout(5, 5));
		pickerToolLabel = new JLabel(Resources.pickerIcon);
		pickerToolLabel.setOpaque(true);
		pickerToolLabel.setBackground(Color.CYAN);
		pickerToolLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				ToolListManager.setCurrentTool(ToolListManager.PICKER_TOOL);
			}
		});
		toolsSideBarPanel.add(pickerToolLabel);
		brushToolLabel = new JLabel(Resources.brushIcon);
		brushToolLabel.setOpaque(true);
		brushToolLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				ToolListManager.setCurrentTool(ToolListManager.BRUSH_TOOL);
			}
		});
		toolsSideBarPanel.add(brushToolLabel);
		toolsSideBarPanel.setMaximumSize(new Dimension(50, 50));
		Main.f.add(toolsSideBarPanel, Main.translator.getAfterTextBorder());
		return toolsSideBarPanel;
	}
	
	private static void update(int toolToChange) {
		Color c = getLabelForTool(toolToChange).getBackground();
		getLabelForTool(toolToChange).setBackground(getLabelForTool(currentTool).getBackground());
		getLabelForTool(currentTool).setBackground(c);
		Main.getBoard().setMouseAdapterForTool(toolToChange);
	}
	
	private static JLabel getLabelForTool(int tool) {
		if (tool == PICKER_TOOL) {
			return pickerToolLabel;
		}
		if (tool == BRUSH_TOOL) {
			return brushToolLabel;
		}
		return null;
	}

	public static int getCurrentTool() {
		return currentTool;
	}

	public static void setCurrentTool(int currentTool) {
		update(currentTool);
		ToolListManager.currentTool = currentTool;
	}
}
