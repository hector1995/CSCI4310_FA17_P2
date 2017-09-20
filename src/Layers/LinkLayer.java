package Layers;

import Enums.LayerType;

public class LinkLayer extends Layer {
	private int address;
	public LinkLayer(int address) {
		super(LayerType.L2);
		this.address = address;
	}
}
