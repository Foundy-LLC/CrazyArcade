package server;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import com.google.gson.Gson;
import domain.model.Direction;
import domain.state.GameState;

public class UserService extends Thread {
    private DataInputStream dis;
    private DataOutputStream dos;
    private final Socket clientSocket;
    private final Vector<UserService> users;
    private String UserName = "";

    private final OnUserRemove onUserRemove;

    public UserService(Socket clientSocket, Vector<UserService> users, OnUserRemove onUserRemove) {
        this.clientSocket = clientSocket;
        this.users = users;
        this.onUserRemove = onUserRemove;
        try {
            InputStream is = clientSocket.getInputStream();
            dis = new DataInputStream(is);
            OutputStream os = clientSocket.getOutputStream();
            dos = new DataOutputStream(os);
            String line = dis.readUTF();
            String[] msgArr = line.split(" ");
            UserName = msgArr[0].trim().substring(1);
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
            dos.writeUTF(msg);
            System.out.println("--------------send----------------");
            System.out.println(msg);
            System.out.println("-------------------------------------");
        } catch (IOException e) {
            System.out.println("dos.write() error");
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

    private void closeAll() throws IOException {
        dos.close();
        dis.close();
        clientSocket.close();
        onUserRemove.call(this);
    }

    public void run() {
        while (true) { // 사용자 접속을 계속해서 받기 위해 while문
            try {
                String msg = dis.readUTF();
                msg = msg.trim();
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
                            writeAll("/startGame");

                            List<String> userNames = new ArrayList<>(8);
                            users.forEach((userService -> userNames.add(userService.UserName)));
                            GameState initGameState = gameStateRepository.createGameState(userNames);
                            String stateJson = new Gson().toJson(initGameState);
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
                System.out.println("dis.read() error");
                try {
                    closeAll();
                    break;
                } catch (Exception ee) {
                    break;
                }
            }
        }
    }
}
