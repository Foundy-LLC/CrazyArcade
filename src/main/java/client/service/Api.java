package client.service;

import domain.constant.Protocol;
import domain.model.Direction;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

import static domain.constant.Protocol.BUF_LEN;

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

    private byte[] makePacket(String message) {
        byte[] packet = new byte[BUF_LEN];
        byte[] bb = null;
        int i;
        for (i = 0; i < BUF_LEN; i++) {
            packet[i] = 0;
        }
        try {
            bb = message.getBytes("euc-kr");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.exit(0);
        }
        for (i = 0; i < bb.length; i++) {
            packet[i] = bb[i];
        }
        return packet;
    }

    private void sendMessage(String message) {
        assertDidInit();

        byte[] packet = makePacket(message);
        try {
            outputStream.write(packet, 0, packet.length);
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

    public void startGame() {
        sendMessage("/" + userName + " startGame");
    }

    public void movePlayer(Direction direction) {
        sendMessage("/" + userName + " " + direction.name().toLowerCase());
    }

    class NetworkSubscriber extends Thread {
        public void run() {
            while (true) {
                try {
                    byte[] packet = new byte[BUF_LEN];
                    int receiveSize = inputStream.read(packet);
                    if (receiveSize < 0) {
                        throw new IOException();
                    }
                    int messageLength;
                    for (messageLength = 0 ; messageLength < BUF_LEN; ++messageLength) {
                        if (packet[messageLength] == '\u0000') {
                            break;
                        }
                    }

                    String message = new String(packet, "euc-kr".trim());
                    System.out.println("============= MESSAGE ==============");
                    System.out.println(message);
                    message = message.substring(0, messageLength);
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
