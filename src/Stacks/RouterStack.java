package Stacks;

import Layers.*;

public class RouterStack {
	private NetworkLayer networkLayer;
	private LinkLayer linkLayer;
	private PhysicalLayer physicalLayer;
	public RouterStack(int IP, int mask, int MAC) {
		//-- create layers
		this.networkLayer = new NetworkLayer(IP, mask);
		this.linkLayer = new LinkLayer(MAC);
		this.physicalLayer = new PhysicalLayer();
		//-- set neighbors
		this.networkLayer.setLower(linkLayer);
		this.linkLayer.setLower(physicalLayer).setUpper(networkLayer);
		this.physicalLayer.setUpper(linkLayer);
	}
}
