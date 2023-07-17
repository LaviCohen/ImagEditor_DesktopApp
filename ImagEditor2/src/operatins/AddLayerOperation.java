package operatins;

import drawables.Layer;
import main.Main;

public class AddLayerOperation implements Operation{

	private Layer layer;
	
	public AddLayerOperation(Layer layer) {
		super();
		this.layer = layer;
	}

	@Override
	public void undo() {
		Main.getBoard().getLayers().remove(layer);
		Main.getBoard().repaint();
		Main.updateLayersList();
	}

	@Override
	public void redo() {
		Main.getBoard().addLayer(layer);
	}

}
