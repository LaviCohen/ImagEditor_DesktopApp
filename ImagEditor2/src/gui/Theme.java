package gui;

import java.awt.Color;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.text.JTextComponent;

import install.DefaultSettings;
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
	public JComponent affect(JComponent component) {
		if (component instanceof JTextComponent) {
			component.setBackground(getBackgroundColor().brighter());
			component.setForeground(getTextColor());
		}else if(component instanceof JButton){	
			if (isAffectingButtons()) {
				component.setBackground(getBackgroundColor().darker());
				component.setForeground(getTextColor());
			}
		}else{
			component.setOpaque(true);
			ColorTheme.super.affect(component);
		}
		return component;
	}
	public static Color opposeColor(Color c) {
		return new Color(255 - c.getRed(), 255 - c.getGreen(), 255 - c.getBlue());
	}
	public boolean isLightMode() {
		return !DefaultSettings.darkMode;
	}
	@Override
	public boolean isAffectingButtons() {
		return isAffectingButtons ;
	}
}
