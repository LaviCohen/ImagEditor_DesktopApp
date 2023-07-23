package effects;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.JButton;
import javax.swing.JDialog;

import drawables.shapes.Picture;
import le.gui.components.LSlider;
import main.Main;

public class RetroEffect extends Effect{

	private int variety;
	
	public RetroEffect(int variety) {
		this.variety = variety;
	}
	
	public RetroEffect() {
		this(32);
	}

	public RetroEffect(String[] data) {
		this(Integer.parseInt(data[0]));
	}

	@Override
	public void affectImage(BufferedImage bf) {
		System.out.println(bf.getWidth() + ", " + bf.getHeight());
		for (int i = 0; i < bf.getWidth(); i++) {
			for (int j = 0; j < bf.getHeight(); j++) {
				Color cur = new Color(bf.getRGB(i, j));
				Color affected = new Color(cur.getRed() / variety * variety,
						cur.getGreen() / variety * variety, cur.getBlue() / variety * variety,
						cur.getAlpha());
//				System.out.println(affected + ", " + affected.getAlpha());
				bf.setRGB(i, j, affected.getRGB());
			}
		}
	}

	@Override
	public void edit(Picture parent) {
		JDialog editDialog = new JDialog(Main.f);
		editDialog.setTitle("Edit Retro Effect");
		editDialog.setLayout(new GridLayout(2, 1));
		LSlider varietySlider = new LSlider("Variety:", 1, 255, variety);
		Main.theme.affect(varietySlider);
		editDialog.add(varietySlider);
		JButton apply = new JButton("Apply");
		Main.theme.affect(apply);
		apply.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				variety = (int) varietySlider.getValue();
				editDialog.dispose();
				parent.invalidate();;
				Main.getBoard().repaint();
			}
		});
		editDialog.add(apply);
		editDialog.pack();
		editDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		editDialog.setVisible(true);
	}

	@Override
	public String encodeEffect() {
		return super.encodeEffect() + "_" + this.variety;
	}
}
