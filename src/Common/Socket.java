package Common;

public class Socket {
	private int address;
	private int port;

	public Socket(int address, int port) {
		this.address = address;
		this.port = port;
	}

	public int getAddress() {
		return address;
	}

	public int getPort() {
		return port;
	}
}
