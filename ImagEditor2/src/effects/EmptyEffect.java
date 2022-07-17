package effects;

import java.awt.image.BufferedImage;

public class EmptyEffect extends Effect {
	@Override
	public void edit() {}
	@Override
	public BufferedImage getImage(BufferedImage bf) {
		return bf;
	}
}