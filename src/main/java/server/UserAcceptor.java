package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Vector;

import domain.constant.Protocol;

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
                System.out.println("Waiting clients ...");
                Socket clientSocket = socket.accept();
                System.out.println("새로운 참가자 from " + clientSocket);
                // User 당 하나씩 Thread 생성
                UserService newUser = new UserService(clientSocket, this::writeAll, this::writeToSome, userVector::removeElement);
                userVector.add(newUser);
                System.out.println("사용자 입장. 현재 참가자 수 " + userVector.size());
                newUser.start();
            } catch (IOException e) {
                System.out.println("!!!! accept 에러 발생... !!!!");
            }
        }
    }

    public void writeAll(String str) {
        for (int i = 0; i < userVector.size(); i++) {
            UserService user = userVector.elementAt(i);
            user.writeOne(str);
        }
    }

    public void writeToSome(String message, List<String> targetUserNames) {
        for (int i = 0; i < userVector.size(); i++) {
            UserService user = userVector.elementAt(i);
            String name = user.getUserName();
            if (targetUserNames.contains(name)) {
                user.writeOne(message);
            }
        }
    }
}
