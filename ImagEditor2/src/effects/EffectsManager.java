package effects;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import drawables.shapes.Picture;
import le.gui.dialogs.LDialogs;
import main.Main;

public class EffectsManager extends Effect{
	public Picture parent;
	public HashMap<Effect, Boolean> effects = new HashMap<Effect, Boolean>();
	public EffectsManager(Picture picture) {
		this.parent = picture;
	}
	public void affectImage(BufferedImage bufferedImage) {
		for(Effect effect:effects.keySet()) {
			if (effects.get(effect) == true) {// == true isn't needed, but code is more readable now
				System.out.println("Affecting with " + effect);
				effect.affectImage(bufferedImage);
			} else {
				System.out.println("Don't affecting with " + effect);
			}
		}
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
					effect = new BlackNWhiteEffect();
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
				parent.invalidate();
				Main.getBoard().repaint();
			}
		});
		effectManagerDialog.add(addEffect, BorderLayout.SOUTH);
		effectManagerDialog.pack();
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
				System.out.println(effect + " is " + (active.isSelected()?"active":"not active"));
				parent.invalidate();
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
				parent.invalidate();
				Main.getBoard().repaint();
			}
		});
		actionsPanel.add(remove);
		panel.add(actionsPanel, Main.translator.getAfterTextBorder());
		return panel;
	}
	public EffectsManager(String s, Picture parent) {
		this(parent);
		System.out.println("Decoding Effects Manager from Data: " + s);
		String[] data = s.split("#");
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
			s += "#" + ((Effect)object).encodeEffect() + "#" + effects.get(object);
		}
		return super.encodeEffect() + s;
	}
}