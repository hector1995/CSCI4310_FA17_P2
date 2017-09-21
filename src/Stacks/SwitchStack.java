package Stacks;

import Layers.*;

public class SwitchStack {
	private LinkLayer linkLayer;
	private PhysicalLayer physicalLayer;
	public SwitchStack(int MAC) {
		//-- create layers
		this.linkLayer = new LinkLayer(MAC);
		this.physicalLayer = new PhysicalLayer();
		//-- set neighbors
		this.linkLayer.setLower(physicalLayer);
		this.physicalLayer.setUpper(linkLayer);
	}
}
