package Layers;

import java.util.ArrayList;
import Enums.LayerType;
import Common.PacketData;

public abstract class Layer extends Thread {
	//-- Private attributes
	protected LayerType layerType;
	private Layer layerUpper;
	private Layer layerLower;
	private ArrayList<PacketData> recvBuffer, sendBuffer;
	private enum From {
		sendBuffer, recvBuffer;
	}
	//-- Public methods
	public PacketData getPacket(From from) {
		ArrayList<PacketData> buffer = (from == From.recvBuffer) ? this.recvBuffer : this.sendBuffer;
		return buffer.size() > 0 ? buffer.remove(0) : null;
	}
	public static PacketData encapsulate(PacketData packetData) {
		return packetData != null ? new PacketData(packetData) : null;
	}
	public static PacketData decapsulate(PacketData packetData) {
		return packetData != null ? new PacketData(packetData) : null;
	}
	public Layer(LayerType layerType) {
		this.layerType = layerType;
		this.layerLower = null;
		this.layerUpper = null;
		this.recvBuffer = new ArrayList<>();
		this.sendBuffer = new ArrayList<>();
		this.start();
	}
	public Layer setUpper(Layer layerUpper) {
		this.layerUpper = layerUpper;
		return this;
	}
	public Layer setLower(Layer layerLower) {
		this.layerLower = layerLower;
		return this;
	}
	@Override
	public void run() {
		while(true) {
			try {
				//-- Every once in a while we inform the neighbor layers about availability of the data
				Thread.sleep(5000);
				if(this.layerUpper != null && recvBuffer.size() > 0) {
					this.layerUpper.interrupt();
				}
				if(this.layerLower != null && sendBuffer.size() > 0) {
					this.layerLower.interrupt();
				}
			}
			catch (InterruptedException e) {
				//-- Interrupted  by the lower layer
				PacketData tempPacket = null;
				if(this.layerLower != null) {
					tempPacket = decapsulate(this.layerLower.getPacket(From.recvBuffer));
					if(tempPacket != null) {
						this.recvBuffer.add(tempPacket);
					}
				}
				if(this.layerUpper != null) {
					tempPacket = encapsulate(this.layerUpper.getPacket(From.sendBuffer));
					if(tempPacket != null) {
						this.sendBuffer.add(tempPacket);
					}
				}
			}
		}
	}
}
