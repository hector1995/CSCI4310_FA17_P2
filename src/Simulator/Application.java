package Simulator;

import Common.Socket;

public class Application {
	private Socket localSocket;
	private Socket remoteSocket;

	public Application(Socket localSocket, Socket remoteSocket) {
		this.localSocket = localSocket;
		this.remoteSocket = remoteSocket;
	}
}
