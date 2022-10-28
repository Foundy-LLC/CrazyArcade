package server;

public class ServerMain {
	public static void main(String[] args) {
		UserAcceptor userAcceptor = new UserAcceptor();
		userAcceptor.start();
	}
}
