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
import domain.state.GameState;
import domain.util.Util;

public class UserService extends Thread {
    private DataInputStream dis;
    private DataOutputStream dos;
    private final Socket clientSocket;
    private final Vector<UserService> users;
    private String UserName = "";

    private final OnUserRemove onUserRemove;

    public UserService(Socket clientSocket, Vector<UserService> users, OnUserRemove onUserRemove) {
        // TODO Auto-generated constructor stub
        // 매개변수로 넘어온 자료 저장
        this.clientSocket = clientSocket;
        this.users = users;
        this.onUserRemove = onUserRemove;
        try {
            InputStream is = clientSocket.getInputStream();
            dis = new DataInputStream(is);
            OutputStream os = clientSocket.getOutputStream();
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
    public void writeAll(String str) {
        for (int i = 0; i < users.size(); i++) {
            UserService user = users.elementAt(i);
            user.writeOne(str);
        }
    }

    public void writeOne(String msg) {
        try {
            // dos.writeUTF(msg);
            byte[] bb;
            bb = Util.makePacket(msg);
            dos.write(bb, 0, bb.length);
            System.out.println("--------------send----------------");
            System.out.println(msg);
            System.out.println("-------------------------------------");
        } catch (IOException e) {
            Util.appendText("dos.write() error");
            try {
                dos.close();
                dis.close();
                clientSocket.close();
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
                    Util.appendText("dis.read() < 0 error");
                    try {
                        dos.close();
                        dis.close();
                        clientSocket.close();
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
                        case "startGame" -> {
                            List<String> userNames = new ArrayList<>(8);
                            users.forEach((userService -> {
                                userNames.add(userService.UserName);
                            }));
                            GameState gameState = gameStateRepository.createGameState(userNames);
                            String stateJson = new Gson().toJson(gameState);
                            writeAll(stateJson);
                        }
                        case "up", "down", "left", "right" -> {
                            String name = msgArr[0].substring(1);
                            String action = msgArr[1];
                            Direction direction = Direction.valueOf(action.toUpperCase());
                            GameState newState = gameStateRepository.movePlayer(name, direction);
                            String stateJson = new Gson().toJson(newState);
                            writeAll(stateJson);
                        }
                        case "bomb" -> {
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Message Separate Error");
                    e.printStackTrace();
                }
            } catch (IOException e) {
                Util.appendText("dis.read() error");
                try {
                    dos.close();
                    dis.close();
                    clientSocket.close();
                    onUserRemove.call(this); // 에러가난 현재 객체를 벡터에서 지운다
                    break;
                } catch (Exception ee) {
                    break;
                }
            }
        }
    }
}
