package effects;

import java.awt.image.BufferedImage;

public abstract class Effect {
	public abstract BufferedImage getImage(BufferedImage bf);
	public abstract void edit();
	public String encodeEffect() {
		return this.getClass().getName();
	}
	public static Effect parseEffect(String s) {
		if (s.startsWith(BlackAndWhiteEffect.class.getName())) {
			return new BlackAndWhiteEffect(s.substring(s.indexOf('|')).split("|"));
		}else if (s.startsWith(BlurEffect.class.getName())) {
			return new BlurEffect(s.substring(s.indexOf('|')).split("|"));
		}else if (s.startsWith(GreenScreenEffect.class.getName())) {
			return new GreenScreenEffect(s.substring(s.indexOf('|')).split("|"));
		}else {
			System.out.println("Couldn't parse effect: " + s);
			return null;
		}
	}
}
