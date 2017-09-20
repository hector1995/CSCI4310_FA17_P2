package Layers;

import Enums.LayerType;

public class NetworkLayer extends Layer {
	private int address;
	private int mask;
	public NetworkLayer(int address, int mask) {
		super(LayerType.L3);
		this.address = address;
		this.mask = mask;
	}
}
