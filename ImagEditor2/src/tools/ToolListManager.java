package tools;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import gui.components.board.adapters.BrushMouseAdapter;
import gui.components.board.adapters.EraserMouseAdapter;
import gui.layouts.ListLayout;
import install.Resources;
import le.gui.components.LSlider;
import main.Main;

public class ToolListManager {

	public static final int PICKER_TOOL = 0;
	public static final int BRUSH_TOOL = 1;
	public static final int ERASER_TOOL = 2;
	

	private static int currentTool = 0;
	
	
	private static JLabel pickerToolLabel;
	private static JLabel brushToolLabel;
	private static JLabel eraserToolLabel;
	
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
		eraserToolLabel = new JLabel(Resources.eraserIcon);
		eraserToolLabel.setOpaque(true);
		eraserToolLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				ToolListManager.setCurrentTool(ToolListManager.ERASER_TOOL);
			}
		});
		toolsSideBarPanel.add(eraserToolLabel);
		toolsSideBarPanel.setMaximumSize(new Dimension(50, 50));
		Main.f.add(toolsSideBarPanel, Main.translator.getAfterTextBorder());
		return toolsSideBarPanel;
	}
	
	private static void update(int toolToChange) {
		Color c = getLabelForTool(toolToChange).getBackground();
		getLabelForTool(toolToChange).setBackground(getLabelForTool(currentTool).getBackground());
		getLabelForTool(currentTool).setBackground(c);
		Main.getBoard().setMouseAdapterForTool(toolToChange);
		Main.toolsSettingsPanel.remove(0);
		Main.toolsSettingsPanel.add(createTopPanelForTool(toolToChange), 
				Main.translator.getBeforeTextBorder());
		Main.f.revalidate();
		Main.f.repaint();
	}
	
	public static JPanel createTopPanelForTool(int tool) {
		JPanel p = new JPanel();
		p.setLayout(new BorderLayout());
		
		if (tool == BRUSH_TOOL) {
			JPanel optionsBar = new JPanel(new GridLayout(1, 2, 3, 3));
			JPanel colorPanel = new JPanel(new BorderLayout());
			colorPanel.add(Main.theme.affect(new JLabel("Color:")), Main.translator.getBeforeTextBorder());
			JLabel colorLabel = new JLabel();
			colorLabel.setOpaque(true);
			colorLabel.setBackground(BrushMouseAdapter.getBrushColor());
			colorPanel.add(colorLabel);
			JButton setColorButton = new JButton("Set Color");
			Main.theme.affect(setColorButton);
			setColorButton.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					colorLabel.setBackground(JColorChooser.showDialog(p, "Choose Text color", colorLabel.getBackground()));
					BrushMouseAdapter.setBrushColor(colorLabel.getBackground());
				}
			});
			colorPanel.add(setColorButton, Main.translator.getAfterTextBorder());
			optionsBar.add(colorPanel);
			LSlider brushSizeSlider = new LSlider("Brush Size", 1, 100, BrushMouseAdapter.getBrushSize());
			brushSizeSlider.slider.addChangeListener(new ChangeListener() {
				
				@Override
				public void stateChanged(ChangeEvent e) {
					BrushMouseAdapter.setBrushSize(brushSizeSlider.getValue());
				}
			});
			optionsBar.add(brushSizeSlider);
			p.add(optionsBar, Main.translator.getBeforeTextBorder());
		} else if (tool == ERASER_TOOL) {
			JPanel optionsBar = new JPanel(new GridLayout(1, 1, 3, 3));
			LSlider brushSizeSlider = new LSlider("Eraser Size", 1, 100, EraserMouseAdapter.getEraserSize());
			brushSizeSlider.slider.addChangeListener(new ChangeListener() {
				
				@Override
				public void stateChanged(ChangeEvent e) {
					EraserMouseAdapter.setEraserSize(brushSizeSlider.getValue());
				}
			});
			optionsBar.add(brushSizeSlider);
			p.add(optionsBar, Main.translator.getBeforeTextBorder());
		} else if (tool == PICKER_TOOL) {
//			((BorderLayout)p.getLayout()).setVgap(5);
			p.add(new JLabel("<html><big>Picker Tool</big></html>"));
		}
		
		return p;
	}
	
	private static JLabel getLabelForTool(int tool) {
		if (tool == PICKER_TOOL) {
			return pickerToolLabel;
		}
		if (tool == BRUSH_TOOL) {
			return brushToolLabel;
		}
		if (tool == ERASER_TOOL) {
			return eraserToolLabel;
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
