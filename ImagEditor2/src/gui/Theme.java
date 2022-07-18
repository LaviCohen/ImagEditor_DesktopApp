package gui;

import java.awt.Color;

import install.DefaultSettings;

public class Theme {
	private static Color lightModeBackgroundColor = new Color(233, 233, 233);
	private static Color darkModeBackgroundColor = new Color(66, 66, 66);
	private static Color lightModeTextColor = Color.BLACK;
	private static Color darkModeTextColor = Color.WHITE;
	
	public static Color getBackgroundColor() {
		if (isLightMode()) {
			return lightModeBackgroundColor;
		}
		return darkModeBackgroundColor;
	}
	public static Color getTextColor() {
		if (isLightMode()) {
			return lightModeTextColor;
		}
		return darkModeTextColor;
	}
	public static Color opposeColor(Color c) {
		return new Color(255 - c.getRed(), 255 - c.getGreen(), 255 - c.getBlue());
	}
	public static boolean isLightMode() {
		return !DefaultSettings.darkMode;
	}
}
