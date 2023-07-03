package operatins;

import drawables.Layer;
import main.Main;

public class RemoveLayerOperation implements Operation{

	private Layer layer;
	
	public RemoveLayerOperation(Layer layer) {
		super();
		this.layer = layer;
	}

	@Override
	public void undo() {
		Main.getBoard().getLayersList().add(layer);
		Main.getBoard().repaint();
		Main.updateLayersList();
	}

	@Override
	public void redo() {
		Main.getBoard().getLayersList().remove(layer);
		Main.getBoard().repaint();
		Main.updateLayersList();
	}

}
