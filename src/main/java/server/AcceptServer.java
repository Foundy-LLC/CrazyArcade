package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

import model.Constants;
import model.Util;

public class AcceptServer extends Thread {
	private ServerSocket socket; // 서버소켓
	private Socket client_socket; // accept() 에서 생성된 client 소켓
	public final Vector UserVec = new Vector();

	private final OnUserRemove userRemover = UserVec::removeElement;

	@SuppressWarnings("unchecked")
	public void run() {
		try {
			socket = new ServerSocket(Constants.PORT_NUMBER);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		while (true) { // 사용자 접속을 계속해서 받기 위해 while문
			try {
				Util.AppendText("Waiting clients ...");
				client_socket = socket.accept(); // accept가 일어나기 전까지는 무한 대기중
				Util.AppendText("새로운 참가자 from " + client_socket);
				// User 당 하나씩 Thread 생성
				UserService new_user = new UserService(client_socket, UserVec, userRemover);
				UserVec.add(new_user); // 새로운 참가자 배열에 추가
				Util.AppendText("사용자 입장. 현재 참가자 수 " + UserVec.size());
				new_user.start(); // 만든 객체의 스레드 실행
			} catch (IOException e) {
				Util.AppendText("!!!! accept 에러 발생... !!!!");
			}
		}
	}
}
