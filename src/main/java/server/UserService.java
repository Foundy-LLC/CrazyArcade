package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.Vector;

import domain.constant.Protocol;
import domain.util.Util;

public class UserService extends Thread {
	private InputStream is;
	private OutputStream os;
	private DataInputStream dis;
	private DataOutputStream dos;
	private Socket client_socket;
	private Vector user_vc;
	private String UserName = "";

	private final OnUserRemove onUserRemove;

	public UserService(Socket client_socket, Vector UserVec, OnUserRemove onUserRemove) {
		// TODO Auto-generated constructor stub
		// 매개변수로 넘어온 자료 저장
		this.client_socket = client_socket;
		this.user_vc = UserVec;
		this.onUserRemove = onUserRemove;
		try {
			is = client_socket.getInputStream();
			dis = new DataInputStream(is);
			os = client_socket.getOutputStream();
			dos = new DataOutputStream(os);
			// line1 = dis.readUTF();
			// /login user1 ==> msg[0] msg[1]
			byte[] b = new byte[Protocol.BUF_LEN];
			dis.read(b);
			String line1 = new String(b);
			String[] msg = line1.split(" ");
			UserName = msg[1].trim();
//			AppendText("새로운 참가자 " + UserName + " 입장.");
//			Util.AppendText("새로운 참가자 " + UserName + " 입장.");
			WriteOne("Welcome to Java chat server\n");
			WriteAll("새로운 참가자 " + UserName + " 입장.");
//			WriteOne(UserName + "님 환영합니다.\n"); // 연결된 사용자에게 정상접속을 알림
		} catch (Exception e) {
			// AppendText("userService error");
		}
	}

	// 모든 User들에게 방송. 각각의 UserService Thread의 WriteONe() 을 호출한다.
	public void WriteAll(String str) {
		for (int i = 0; i < user_vc.size(); i++) {
			UserService user = (UserService) user_vc.elementAt(i);
			System.out.println(user.UserName);
			user.WriteOne(str);
		}
	}

	public void WriteOne(String msg) {
		try {
			// dos.writeUTF(msg);
			byte[] bb;
			bb = Util.MakePacket(msg);
			dos.write(bb, 0, bb.length);
		} catch (IOException e) {
			Util.AppendText("dos.write() error");
			try {
				dos.close();
				dis.close();
				client_socket.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			onUserRemove.call(this); // 에러가난 현재 객체를 벡터에서 지운다
		}
	}

	public void run() {
		while (true) { // 사용자 접속을 계속해서 받기 위해 while문
			try {
				// String msg = dis.readUTF();
				byte[] b = new byte[Protocol.BUF_LEN];
				int ret;
				ret = dis.read(b);
				if (ret < 0) {
					Util.AppendText("dis.read() < 0 error");
					try {
						dos.close();
						dis.close();
						client_socket.close();
						onUserRemove.call(this);
						break;
					} catch (Exception ee) {
						break;
					} // catch문 끝
				}
				String msg = new String(b, "euc-kr");
				msg = msg.trim(); // 앞뒤 blank NULL, \n 모두 제거
				try {
					// String[] msgArr = msg.split(Constants.MESSAGE_SEPARATOR.toString());
					String[] msgArr = msg.split(" ");
					System.out.println(Arrays.toString(msgArr));

					switch (msgArr[1]) {
					case "/login": {

					}
					case "startGame": {

					}
					case "up": {

					}
					case "down": {

					}
					case "left": {

					}
					case "right": {

					}
					case "bomb": {

					}
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					System.out.println("Message Separate Error");
					e.printStackTrace();
				}
				Util.AppendText(msg); // server 화면에 출력
				WriteAll(msg + "\n"); // Write All
			} catch (IOException e) {
				Util.AppendText("dis.read() error");
				try {
					dos.close();
					dis.close();
					client_socket.close();
					onUserRemove.call(this); // 에러가난 현재 객체를 벡터에서 지운다
					break;
				} catch (Exception ee) {
					break;
				} // catch문 끝
			} // 바깥 catch문끝
		} // while
	} // run
}
