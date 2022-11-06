package client.service;

import client.core.ChangeNotifier;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import domain.constant.Protocol;
import domain.model.Direction;
import domain.model.Sound;
import lombok.Getter;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.UUID;

public class Api extends ChangeNotifier {

    private String ipAddress;
    private String portNumber;
    private Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;

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
        inputStream = new DataInputStream(socket.getInputStream());
        outputStream = new DataOutputStream(socket.getOutputStream());

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
            outputStream.writeUTF(message);
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
        sendMessage("/" + userName + " installWaterBomb");
    }

    class NetworkSubscriber extends Thread {

        private boolean checkAndPlaySound(String message) {
            try {
                Sound sound = new Gson().fromJson(message, Sound.class);
                if (sound != null && sound.path != null) {
                    SoundController.play(sound);
                    return true;
                }
            } catch (JsonSyntaxException ignored) {
            }
            return false;
        }

        public void run() {
            while (true) {
                try {
                    String message = inputStream.readUTF();
                    System.out.println("============= MESSAGE ==============");
                    System.out.println(message);

                    if (checkAndPlaySound(message)) {
                        continue;
                    }
                    if (message.equals("/startGame")) {
                        SoundController.playLoop(Sound.GAME_VIEW_BGM);
                    }
                    notifyListeners(message);
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
            notifyListeners(Protocol.ERROR);
        }
    }
}
