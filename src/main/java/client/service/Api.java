package client.service;

import domain.constant.Protocol;
import domain.model.Direction;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public class Api {

    private String ipAddress;
    private String portNumber;
    private Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;

    private String userName;

    private final Set<MessageListener> listeners = new HashSet<>();

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
        inputStream = new DataInputStream(socket.getInputStream());
        outputStream = new DataOutputStream(socket.getOutputStream());

        NetworkSubscriber subscriber = new NetworkSubscriber();
        subscriber.start();
    }

    public void addListener(MessageListener listener) {
        listeners.add(listener);
    }

    private void notifyToListeners(String message) {
        for (MessageListener listener : listeners) {
            listener.onReceive(message);
        }
    }

    public void removeListener(MessageListener listener) {
        listeners.remove(listener);
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
            outputStream.writeUTF(message);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.exit(0);
        }
    }

    public void login(String userName) {
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
                    String message = inputStream.readUTF();
                    System.out.println("============= MESSAGE ==============");
                    System.out.println(message);
                    notifyToListeners(message);
                } catch (IOException e) {
                    try {
                        outputStream.close();
                        inputStream.close();
                        socket.close();
                    } catch (Exception ignored) {
                    }
                    break;
                }
            }
            notifyToListeners(Protocol.ERROR);
        }
    }
}
