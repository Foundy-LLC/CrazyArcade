package server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class ServerMain {

	public static void main(String[] args) {
		AcceptServer acceptserver = new AcceptServer();
		acceptserver.start();
	}

}
