package gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.text.JTextComponent;

import install.Preferences;
import le.gui.ColorTheme;

public class Theme implements ColorTheme{
	private static Color lightModeBackgroundColor = new Color(233, 233, 233);
	private static Color darkModeBackgroundColor = new Color(66, 66, 66);
	private static Color lightModeTextColor = Color.BLACK;
	private static Color darkModeTextColor = Color.WHITE;
	private boolean isAffectingButtons = false;
	
	@Override
	public Color getBackgroundColor() {
		if (isLightMode()) {
			return lightModeBackgroundColor;
		}
		return darkModeBackgroundColor;
	}
	@Override
	public Color getTextColor() {
		if (isLightMode()) {
			return lightModeTextColor;
		}
		return darkModeTextColor;
	}
	
	@Override
	public Component affect(Component component) {
		if (component.getName() != null && component.getName().equals(DONT_AFFECT)) {
			return component;
		}
		if (component instanceof JTextComponent) {
			component.setBackground(getBackgroundColor().brighter());
			component.setForeground(getTextColor());
		}else if(component instanceof JButton){	
			if (isAffectingButtons()) {
				component.setBackground(getBackgroundColor().darker());
				component.setForeground(getTextColor());
			}
		}else{
			if (component instanceof JComponent) {
				((JComponent) component).setOpaque(true);
				if (isAffectingButtons() || !(component instanceof JButton)) {
					component.setBackground(getBackgroundColor());
					component.setForeground(getTextColor());
				}
			}
		}
		if (component instanceof Container) {
			affectContainer((Container)component);
		}
		return component;
	}
	public static Color opposeColor(Color c) {
		return new Color(255 - c.getRed(), 255 - c.getGreen(), 255 - c.getBlue());
	}
	public boolean isLightMode() {
		return !Preferences.darkMode;
	}
	@Override
	public boolean isAffectingButtons() {
		return isAffectingButtons || Preferences.darkMode;
	}
}
