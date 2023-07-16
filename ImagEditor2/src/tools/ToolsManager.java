package tools;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import drawables.Layer;
import drawables.shapes.GroupShape;
import drawables.shapes.abstractShapes.Shape;
import gui.components.Board;
import gui.layouts.ListLayout;
import install.Resources;
import le.gui.ColorTheme;
import le.gui.components.LSlider;
import le.gui.dialogs.LDialogs;
import main.Main;
import tools.adapters.BoardAdapter;
import tools.adapters.BrushMouseAdapter;
import tools.adapters.EraserMouseAdapter;
import tools.adapters.GroupMouseAdapter;
import tools.adapters.PickingMouseAdapter;
import tools.adapters.TextMouseAdapter;
import tools.adapters.UngroupMouseAdapter;

public class ToolsManager {

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
		//Picker
		pickerToolLabel = new JLabel(Resources.pickerIcon);
		pickerToolLabel.setToolTipText("Mouse Picking Abilities (Moving, Resizing etc.)");
		pickerToolLabel.setOpaque(true);
		pickerToolLabel.setBackground(Color.CYAN);
		pickerToolLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				ToolsManager.setCurrentTool(ToolsManager.PICKER_TOOL);
			}
		});
		toolsSideBarPanel.add(pickerToolLabel);
		//Brush
		brushToolLabel = new JLabel(Resources.brushIcon);
		brushToolLabel.setToolTipText("Use the Brush to Paint on Your Layers");
		brushToolLabel.setOpaque(true);
		brushToolLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				ToolsManager.setCurrentTool(ToolsManager.BRUSH_TOOL);
			}
		});
		toolsSideBarPanel.add(brushToolLabel);
		//Eraser
		eraserToolLabel = new JLabel(Resources.eraserIcon);
		eraserToolLabel.setToolTipText("Use the Eraser to Erase Brushing");
		eraserToolLabel.setOpaque(true);
		eraserToolLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				ToolsManager.setCurrentTool(ToolsManager.ERASER_TOOL);
			}
		});
		toolsSideBarPanel.add(eraserToolLabel);
		//Text
		textToolLabel = new JLabel(Resources.textIcon);
		textToolLabel.setToolTipText("Use This Tool to Add Text Easily");
		textToolLabel.setOpaque(true);
		textToolLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				ToolsManager.setCurrentTool(ToolsManager.TEXT_TOOL);
			}
		});
		toolsSideBarPanel.add(textToolLabel);
		//Group
		groupToolLabel = new JLabel(Resources.groupIcon);
		groupToolLabel.setToolTipText("Use This Tool to Group Layers");
		groupToolLabel.setOpaque(true);
		groupToolLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				ToolsManager.setCurrentTool(ToolsManager.GROUP_TOOL);
			}
		});
		toolsSideBarPanel.add(groupToolLabel);
		//Ungroup
		ungroupToolLabel = new JLabel(Resources.ungroupIcon);
		ungroupToolLabel.setToolTipText("Use This Tool to Ungroup Layers");
		ungroupToolLabel.setOpaque(true);
		ungroupToolLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				ToolsManager.setCurrentTool(ToolsManager.UNGROUP_TOOL);
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
		} else if (tool == GROUP_TOOL) {
			JPanel optionsBar = new JPanel(new GridLayout(1, 1, 3, 3));
			JButton groupButton = new JButton("Group Selected Items");
			groupButton.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					LinkedList<Shape> selected = 
							((GroupMouseAdapter)Main.getBoard().getCurrentMouseAdapter())
							.getSelected();
					if (selected.size() < 2) {
						return;
					}
					for (Shape shape : selected) {
						Main.getBoard().getLayersList().remove(Main.getLayersList().getLayerForShape(shape));
						
					}
					GroupShape groupShape = new GroupShape(true, "Group", selected.toArray(new Shape[0]));
					Main.getBoard().addLayer(new Layer(groupShape));
					Main.updateLayersList();
				}
			});
			optionsBar.add(groupButton);
			p.add(optionsBar);
		}else if (tool == UNGROUP_TOOL) {
			JPanel optionsBar = new JPanel(new GridLayout(1, 1, 3, 3));
			JButton ungroupButton = new JButton("Ungroup Selected Shape");
			ungroupButton.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					Layer selected = Main.getLayersList().getSelectedLayer();
					if (selected == null || !(selected.getShape() instanceof GroupShape)) {
						LDialogs.showMessageDialog(Main.f, "No Group Shape is Selected", "Warning", LDialogs.WARNING_MESSAGE);
						return;
					}
					GroupShape groupShape = (GroupShape) selected.getShape();
					Layer[] layers = groupShape.getLayers();
					
					if (selected.getTop() != null) {
						for (Layer layer : layers) {
							layer.setTop(selected.getTop().getSubimage(
									(int) layer.getShape().getX(), (int) layer.getShape().getY(),
									layer.getShape().getWidthOnBoard(), layer.getShape().getHeightOnBoard()));
						}
					}
					
					for (Layer layer : layers) {
						layer.getShape().setX(layer.getShape().getX() + groupShape.getX());
						layer.getShape().setY(layer.getShape().getY() + groupShape.getY());
						Main.getBoard().addLayer(layer);
					}
					
					Main.getBoard().getLayersList().remove(Main.getLayersList().getLayerForShape(groupShape));
					
					Main.updateLayersList();
					
					Main.getBoard().repaint();
				}
			});
			optionsBar.add(ungroupButton);
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
		if (tool == ToolsManager.PICKER_TOOL) {
			return new PickingMouseAdapter(board);
		} else if (tool == ToolsManager.BRUSH_TOOL) {
			return new BrushMouseAdapter(board);
		}else if (tool == ToolsManager.ERASER_TOOL) {
			return new EraserMouseAdapter(board);
		}else if (tool == ToolsManager.TEXT_TOOL) {
			return new TextMouseAdapter(board);
		}else if (tool == ToolsManager.GROUP_TOOL) {
			return new GroupMouseAdapter(board);
		}else if (tool == ToolsManager.UNGROUP_TOOL) {
			return new UngroupMouseAdapter(board);
		}
		
		return null;
	}
	
	public static int getCurrentTool() {
		return currentTool;
	}

	public static void setCurrentTool(int currentTool) {
		update(currentTool);
		ToolsManager.currentTool = currentTool;
	}
}
