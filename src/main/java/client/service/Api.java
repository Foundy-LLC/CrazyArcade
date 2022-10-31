package client.service;

import client.core.ChangeNotifier;
import domain.constant.Protocol;
import domain.model.Direction;
import lombok.Getter;

import java.io.*;
import java.net.Socket;
import java.util.UUID;

public class Api extends ChangeNotifier {

    private String ipAddress;
    private String portNumber;
    private Socket socket;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;

    @Getter
    private String userName;

    private Api() {
    }

    public static Api getInstance() {
        return Holder.INSTANCE;
    }

    private static class Holder {
        private static final Api INSTANCE = new Api();
    }

    public void init(String ipAddress, String portNumber) throws NumberFormatException, IOException {
        this.ipAddress = ipAddress;
        this.portNumber = portNumber;

        socket = new Socket(ipAddress, Integer.parseInt(portNumber));
        outputStream = new ObjectOutputStream(socket.getOutputStream());
        outputStream.flush();
        inputStream = new ObjectInputStream(socket.getInputStream());

        NetworkSubscriber subscriber = new NetworkSubscriber();
        subscriber.start();
    }

    private void assertDidInit() {
        assert (ipAddress != null);
        assert (portNumber != null);
        assert (socket != null);
        assert (inputStream != null);
        assert (outputStream != null);
    }

    private void sendMessage(String message) {
        assertDidInit();
        try {
            outputStream.writeObject(message);
            outputStream.flush();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.exit(0);
        }
    }

    public void login(String userName) {
        userName += "-" + UUID.randomUUID().toString().substring(0, 4);
        this.userName = userName;
        sendMessage("/" + userName + " " + ipAddress + " " + portNumber);
    }

    public void requestLobbyState() {
        sendMessage("/" + userName + " getLobbyState");
    }

    public void startGame() {
        sendMessage("/" + userName + " startGame");
    }

    public void movePlayer(Direction direction) {
        sendMessage("/" + userName + " " + direction.name().toLowerCase());
    }

    public void installWaterBomb() {
        sendMessage("/" + userName + " waterBomb");
    }

    class NetworkSubscriber extends Thread {
        public void run() {
            while (true) {
                try {
                    Object object = inputStream.readObject();
                    System.out.println("========== MESSAGE RECEIVED ==========");
                    System.out.println(object);
                    System.out.println("======================================");
                    notifyListeners(object);
                } catch (IOException e) {
                    try {
                        outputStream.close();
                        inputStream.close();
                        socket.close();
                    } catch (Exception ignored) {
                    }
                    break;
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
            notifyListeners(Protocol.ERROR);
        }
    }
}
