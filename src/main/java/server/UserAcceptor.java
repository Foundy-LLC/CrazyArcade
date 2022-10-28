package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

import domain.constant.Protocol;
import domain.util.Util;

public class UserAcceptor extends Thread {
	private ServerSocket socket; // 서버소켓
	public final Vector<UserService> userVector = new Vector<>();

	public void run() {
		try {
			socket = new ServerSocket(Protocol.PORT_NUMBER);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		while (true) {
			try {
				Util.appendText("Waiting clients ...");
				Socket clientSocket = socket.accept();
				Util.appendText("새로운 참가자 from " + clientSocket);
				// User 당 하나씩 Thread 생성
				UserService newUser = new UserService(clientSocket, userVector, userVector::removeElement);
				userVector.add(newUser);
				Util.appendText("사용자 입장. 현재 참가자 수 " + userVector.size());
				newUser.start();
			} catch (IOException e) {
				Util.appendText("!!!! accept 에러 발생... !!!!");
			}
		}
	}
}
