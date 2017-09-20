package Stacks;

import Layers.*;

public class HostStack {
	private ApplicationLayer applicationLayer;
	private TransportLayer transportLayer;
	private NetworkLayer networkLayer;
	private LinkLayer linkLayer;
	private PhysicalLayer physicalLayer;
	public HostStack(int IP, int mask, int MAC) {
		//-- create layers
		this.applicationLayer = new ApplicationLayer();
		this.transportLayer = new TransportLayer();
		this.networkLayer = new NetworkLayer(IP, mask);
		this.linkLayer = new LinkLayer(MAC);
		this.physicalLayer = new PhysicalLayer();
		//-- set neighbors
		this.applicationLayer.setLower(transportLayer);
		this.transportLayer.setLower(networkLayer).setUpper(applicationLayer);
		this.networkLayer.setLower(linkLayer).setUpper(transportLayer);
		this.linkLayer.setLower(physicalLayer).setUpper(networkLayer);
		this.physicalLayer.setUpper(linkLayer);
	}
}
