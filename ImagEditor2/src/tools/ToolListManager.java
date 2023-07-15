package tools;

import java.awt.BorderLayout;
import java.awt.Color;
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

import gui.components.Board;
import gui.layouts.ListLayout;
import install.Resources;
import le.gui.ColorTheme;
import le.gui.components.LSlider;
import main.Main;
import tools.adapters.BoardAdapter;
import tools.adapters.BrushMouseAdapter;
import tools.adapters.EraserMouseAdapter;
import tools.adapters.PickingMouseAdapter;
import tools.adapters.TextMouseAdapter;

public class ToolListManager {

	public static final int PICKER_TOOL = 0;
	public static final int BRUSH_TOOL = 1;
	public static final int ERASER_TOOL = 2;
	public static final int TEXT_TOOL = 4;
	public static final int GROUP_TOOL = 5;
	public static final int UNGROUP_TOOL = 6;
	

	private static int currentTool = 0;
	
	
	private static JLabel pickerToolLabel;
	private static JLabel brushToolLabel;
	private static JLabel eraserToolLabel;
	private static JLabel textToolLabel;
	private static JLabel groupToolLabel;
	private static JLabel ungroupToolLabel;
	
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
		textToolLabel = new JLabel(Resources.textIcon);
		textToolLabel.setOpaque(true);
		textToolLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				ToolListManager.setCurrentTool(ToolListManager.TEXT_TOOL);
			}
		});
		toolsSideBarPanel.add(textToolLabel);

		groupToolLabel = new JLabel(Resources.groupIcon);
		groupToolLabel.setOpaque(true);
		groupToolLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				ToolListManager.setCurrentTool(ToolListManager.GROUP_TOOL);
			}
		});
		toolsSideBarPanel.add(groupToolLabel);

		ungroupToolLabel = new JLabel(Resources.ungroupIcon);
		ungroupToolLabel.setOpaque(true);
		ungroupToolLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				ToolListManager.setCurrentTool(ToolListManager.UNGROUP_TOOL);
			}
		});
		toolsSideBarPanel.add(ungroupToolLabel);
		
		Main.f.add(toolsSideBarPanel, Main.translator.getAfterTextBorder());
		return toolsSideBarPanel;
	}
	
	private static void update(int toolToChange) {
		Color c = getLabelForTool(toolToChange).getBackground();
		getLabelForTool(toolToChange).setBackground(getLabelForTool(currentTool).getBackground());
		getLabelForTool(currentTool).setBackground(c);
		Main.getBoard().setMouseAdapterForTool(toolToChange);
		Main.getToolsSettingsPanel().remove(0);
		Main.getToolsSettingsPanel().add(createTopPanelForTool(toolToChange), 
				Main.translator.getBeforeTextBorder());
		Main.f.revalidate();
		Main.f.repaint();
	}
	
	public static JPanel createTopPanelForTool(int tool) {
		JPanel p = new JPanel(new BorderLayout());
		
		if (tool == PICKER_TOOL) {
			p.add(new JLabel("<html><big>Picker Tool: </big></html>"), Main.translator.getBeforeTextBorder());
		} else if (tool == BRUSH_TOOL) {
			p.add(new JLabel("<html><big>Brush Tool: </big></html>"), Main.translator.getBeforeTextBorder());
		}else if (tool == ERASER_TOOL) {
			p.add(new JLabel("<html><big>Eraser Tool: </big></html>"), Main.translator.getBeforeTextBorder());
		}else if (tool == TEXT_TOOL) {
			p.add(new JLabel("<html><big>Text Tool: </big></html>"), Main.translator.getBeforeTextBorder());
		}else if (tool == GROUP_TOOL) {
			p.add(new JLabel("<html><big>Group Tool: </big></html>"), Main.translator.getBeforeTextBorder());
		}else if (tool == UNGROUP_TOOL) {
			p.add(new JLabel("<html><big>Ungroup Tool: </big></html>"), Main.translator.getBeforeTextBorder());
		}
		
		if (tool == BRUSH_TOOL) {
			JPanel optionsBar = new JPanel(new GridLayout(1, 2, 8, 3));
			JPanel colorPanel = new JPanel(new BorderLayout());
			colorPanel.add(Main.theme.affect(new JLabel("Color:")), Main.translator.getBeforeTextBorder());
			JLabel colorLabel = new JLabel();
			colorLabel.setOpaque(true);
			colorLabel.setName(ColorTheme.DONT_AFFECT);
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
			LSlider brushSizeSlider = new LSlider("Size", 1, 100, BrushMouseAdapter.getBrushSize());
			brushSizeSlider.slider.addChangeListener(new ChangeListener() {
				
				@Override
				public void stateChanged(ChangeEvent e) {
					BrushMouseAdapter.setBrushSize(brushSizeSlider.getValue());
				}
			});
			optionsBar.add(brushSizeSlider);
			p.add(optionsBar);
		} else if (tool == ERASER_TOOL) {
			JPanel optionsBar = new JPanel(new GridLayout(1, 1, 3, 3));
			LSlider brushSizeSlider = new LSlider("Size", 1, 100, EraserMouseAdapter.getEraserSize());
			brushSizeSlider.slider.addChangeListener(new ChangeListener() {
				
				@Override
				public void stateChanged(ChangeEvent e) {
					EraserMouseAdapter.setEraserSize(brushSizeSlider.getValue());
				}
			});
			optionsBar.add(brushSizeSlider);
			p.add(optionsBar);
		}
		Main.theme.affect(p);
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
		if (tool == TEXT_TOOL) {
			return textToolLabel;
		}
		if (tool == GROUP_TOOL) {
			return groupToolLabel;
		}
		if (tool == UNGROUP_TOOL) {
			return ungroupToolLabel;
		}
		return null;
	}

	public static BoardAdapter getAdapterForTool(Board board, int tool) {
		if (tool == ToolListManager.PICKER_TOOL) {
			return new PickingMouseAdapter(board);
		} else if (tool == ToolListManager.BRUSH_TOOL) {
			return new BrushMouseAdapter(board);
		}else if (tool == ToolListManager.ERASER_TOOL) {
			return new EraserMouseAdapter(board);
		}else if (tool == ToolListManager.TEXT_TOOL) {
			return new TextMouseAdapter(board);
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
