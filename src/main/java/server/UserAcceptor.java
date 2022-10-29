package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

import domain.constant.Protocol;

public class UserAcceptor extends Thread {
	private ServerSocket socket; // 서버소켓
	public final Vector<UserService> userVector = new Vector<>();

	public void run() {
		try {
			socket = new ServerSocket(Protocol.PORT_NUMBER);
			LobbyStateRepository lobbyStateRepository = LobbyStateRepository.getInstance();
			lobbyStateRepository.initState();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		while (true) {
			try {
				System.out.println("Waiting clients ...");
				Socket clientSocket = socket.accept();
				System.out.println("새로운 참가자 from " + clientSocket);
				// User 당 하나씩 Thread 생성
				UserService newUser = new UserService(clientSocket, userVector, userVector::removeElement);
				userVector.add(newUser);
				System.out.println("사용자 입장. 현재 참가자 수 " + userVector.size());
				newUser.start();
			} catch (IOException e) {
				System.out.println("!!!! accept 에러 발생... !!!!");
			}
		}
	}
}
