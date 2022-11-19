package effects;

import java.awt.image.BufferedImage;

import shapes.Picture;

public abstract class Effect {
	public abstract void affectImage(BufferedImage bf);
	public abstract void edit(Picture parent);
	public String encodeEffect() {
		return this.getClass().getName();
	}
	public static Effect parseEffect(String s) {
		if (s.startsWith(BlackAndWhiteEffect.class.getName())) {
			return new BlackAndWhiteEffect(s.substring(s.indexOf('_') + 1).split("_"));
		}else if (s.startsWith(BlurEffect.class.getName())) {
			return new BlurEffect(s.substring(s.indexOf('_') + 1).split("_"));
		}else if (s.startsWith(GreenScreenEffect.class.getName())) {
			return new GreenScreenEffect(s.substring(s.indexOf('_') + 1).split("_"));
		}else if (s.startsWith(RetroEffect.class.getName())) {
			return new RetroEffect(s.substring(s.indexOf('_') + 1).split("_"));
		}else if (s.startsWith(OutlineEffect.class.getName())) {
			return new OutlineEffect(s.substring(s.indexOf('_') + 1).split("_"));
		}else {
			System.out.println("Couldn't parse effect: " + s);
			return null;
		}
	}
}
