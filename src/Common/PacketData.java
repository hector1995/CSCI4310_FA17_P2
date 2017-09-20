package Common;

import java.util.Arrays;

public class PacketData {
	private byte[] headerData;
	private byte[] messageData;
	private byte[] tailData;
	public PacketData(PacketData packetData) {
		this.headerData = Arrays.copyOf(packetData.headerData, packetData.headerData.length);
		this.messageData = Arrays.copyOf(packetData.messageData, packetData.messageData.length);
		this.tailData = Arrays.copyOf(packetData.tailData, packetData.tailData.length);
	}
}
