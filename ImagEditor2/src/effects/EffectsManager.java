package effects;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import le.gui.dialogs.LDialogs;
import main.Main;
import shapes.Picture;

public class EffectsManager extends Effect{
	public Picture parent;
	public HashMap<Effect, Boolean> effects = new HashMap<Effect, Boolean>();
	public EffectsManager(Picture picture) {
		this.parent = picture;
	}
	public BufferedImage getImage(BufferedImage bufferedImage) {
		BufferedImage ret = bufferedImage;
		for(Effect effect:effects.keySet()) {
			if (effect instanceof GreenScreenEffect && effects.get(effect)) {
				ret = effect.getImage(ret);
			}
		}
		for(Effect effect:effects.keySet()) {
			if (!(effect instanceof GreenScreenEffect) && effects.get(effect)) {
//				System.out.println("Performing " + effect.getClass().getName() + " Effect");
				ret = effect.getImage(ret);
			}
		}
		return ret;
	}
	@Override
	public void edit(Picture parent) {
		JDialog effectManagerDialog = new JDialog(Main.f);
		effectManagerDialog.setBackground(Main.theme.getBackgroundColor());
		effectManagerDialog.setTitle("Effects Manager");
		effectManagerDialog.setLayout(new BorderLayout());
		GridLayout effectsLayout = new GridLayout(effects.size(), 1);
		JPanel allEffects = new JPanel(effectsLayout);
		Main.theme.affect(allEffects);
		for (Effect effect : effects.keySet()) {
			allEffects.add(getPanelForEffect(effect, effectManagerDialog));
		}
		effectManagerDialog.add(allEffects);
		JButton addEffect = new JButton("Add Effect");
		Main.theme.affect(addEffect);
		addEffect.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Object ans = LDialogs.showInputDialog(Main.f, "Choose Effect:", "Add Effect",
						LDialogs.QUESTION_MESSAGE,
						new String[] {"Green Screen", "Blur", "Black & White", "Retro", "Outline"},
						null);
				if (ans == null) {
					return;
				}
				String effectName = ans.toString();
				Effect effect = null;
				if(effectName.equals("Green Screen")) {
					effect = new GreenScreenEffect();
				}else if(effectName.equals("Blur")) {
					effect = new BlurEffect();
				}else if(effectName.equals("Black & White")) {
					effect = new BlackAndWhiteEffect();
				}else if(effectName.equals("Retro")) {
					effect = new RetroEffect();
				}else if(effectName.equals("Outline")) {
					effect = new OutlineEffect();
				}
				if (effect == null) {
					return;
				}
				effects.put(effect, true);
				effectsLayout.setRows(effectsLayout.getRows() + 1);
				allEffects.add(getPanelForEffect(effect, effectManagerDialog));
				effectManagerDialog.pack();
				effect.edit(parent);
				parent.lastDrawn = null;
				Main.getBoard().repaint();
			}
		});
		effectManagerDialog.add(addEffect, BorderLayout.SOUTH);
		effectManagerDialog.pack();
		effectManagerDialog.addComponentListener(new ComponentListener() {
			
			@Override
			public void componentShown(ComponentEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void componentResized(ComponentEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void componentMoved(ComponentEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void componentHidden(ComponentEvent e) {
				Main.getBoard().repaint();
			}
		});
		effectManagerDialog.setVisible(true);
	}
	public JPanel getPanelForEffect(Effect effect, JDialog dialog) {
		JPanel panel = new JPanel(new BorderLayout());
		Main.theme.affect(panel);
		JCheckBox active = new JCheckBox();
		Main.theme.affect(active);
		active.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				effects.put(effect, active.isSelected());
				parent.lastDrawn = null;
				Main.getBoard().repaint();
			}
		});
		active.setSelected(effects.get(effect));
		panel.add(active, Main.translator.getBeforeTextBorder());
		panel.add(Main.theme.affect(new JLabel(effect.getClass().getSimpleName())));
		JPanel actionsPanel = new JPanel(new GridLayout(1, 2));
		Main.theme.affect(actionsPanel);
		JButton edit = new JButton("Edit Effect");
		Main.theme.affect(edit);
		edit.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				effect.edit(parent);
			}
		});
		actionsPanel.add(edit);
		JButton remove = new JButton("Remove Effect");
		Main.theme.affect(remove);
		remove.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				effects.remove(effect);
				dialog.dispose();
				parent.lastDrawn = null;
				Main.getBoard().repaint();
			}
		});
		actionsPanel.add(remove);
		panel.add(actionsPanel, Main.translator.getAfterTextBorder());
		return panel;
	}
	public EffectsManager(String s, Picture parent) {
		String[] data = s.split("^");
		int effectsNum = data.length/2;
		for (int i = 0; i < effectsNum; i++) {
			effects.put(Effect.parseEffect(data[i * 2 + 1]),
					Boolean.parseBoolean(data[i * 2 + 2]));
		}
	}
	@Override
	public String encodeEffect() {
		Object[] keys = effects.keySet().toArray();
		if (keys.length == 0) {
			return super.encodeEffect();
		}
		String s = "";
		for (Object object : keys) {
			s += "^" + ((Effect)object).encodeEffect() + "^" + effects.get(object);
		}
		return super.encodeEffect() + s;
	}
}