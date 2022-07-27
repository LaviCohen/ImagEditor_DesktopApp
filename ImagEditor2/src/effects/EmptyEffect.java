package effects;

import java.awt.image.BufferedImage;

import shapes.Picture;

public class EmptyEffect extends Effect {
	@Override
	public void edit(Picture parent) {}
	@Override
	public BufferedImage getImage(BufferedImage bf) {
		return bf;
	}
}