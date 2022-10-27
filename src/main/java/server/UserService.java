package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import com.google.gson.Gson;
import domain.constant.Protocol;
import domain.model.Direction;
import domain.model.Player;
import domain.state.GameState;
import domain.util.Util;
import lombok.NonNull;

public class UserService extends Thread {
    private InputStream is;
    private OutputStream os;
    private DataInputStream dis;
    private DataOutputStream dos;
    private Socket client_socket;
    private Vector<UserService> user_vc;
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
            UserName = msg[0].trim().substring(1);
        } catch (Exception e) {
            // AppendText("userService error");
        }
    }

    // 모든 User들에게 방송. 각각의 UserService Thread의 WriteONe() 을 호출한다.
    public void WriteAll(String str) {
        for (int i = 0; i < user_vc.size(); i++) {
            UserService user = (UserService) user_vc.elementAt(i);
            user.WriteOne(str);
        }
    }

    public void WriteOne(String msg) {
        try {
            // dos.writeUTF(msg);
            byte[] bb;
            bb = Util.MakePacket(msg);
            dos.write(bb, 0, bb.length);
            System.out.println("--------------send----------------");
            System.out.println(msg);
            System.out.println("-------------------------------------");
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
                System.out.println("--------------receive----------------");
                System.out.println(msg);
                System.out.println("-------------------------------------");
                try {
                    // String[] msgArr = msg.split(Constants.MESSAGE_SEPARATOR.toString());
                    String[] msgArr = msg.split(" ");
                    System.out.println(Arrays.toString(msgArr));
                    GameStateRepository gameStateRepository = GameStateRepository.getInstance();

                    switch (msgArr[1]) {
                        case "startGame": {
                            List<String> userNames = new ArrayList<>(8);
                            user_vc.forEach((userService -> {
                                userNames.add(userService.UserName);
                            }));
                            GameState gameState = gameStateRepository.createGameState(userNames);
                            String stateJson = new Gson().toJson(gameState);
                            WriteAll(stateJson);
                            break;
                        }
                        case "up":
                        case "down":
                        case "left":
                        case "right": {
                            String name = msgArr[0].substring(1);
                            String action = msgArr[1];
                            Direction direction = Direction.valueOf(action.toUpperCase());
                            GameState newState = gameStateRepository.movePlayer(name, direction);
                            String stateJson = new Gson().toJson(newState);
                            WriteAll(stateJson);
                            break;
                        }
                        case "bomb": {
                        }
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    System.out.println("Message Separate Error");
                    e.printStackTrace();
                }
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
