package server;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

import com.google.gson.Gson;
import domain.model.Direction;
import domain.model.Sound;
import domain.state.GameState;
import domain.state.LobbyState;

public class UserService extends Thread {
    private DataInputStream dis;
    private DataOutputStream dos;
    private final Socket clientSocket;
    private String userName = "";

    private final Callback<String> writeAll;

    private final Callback<UserService> onUserRemove;

    public UserService(Socket clientSocket, Callback<String> writeAll, Callback<UserService> onUserRemove) {
        this.clientSocket = clientSocket;
        this.writeAll = writeAll;
        this.onUserRemove = onUserRemove;
        try {
            InputStream is = clientSocket.getInputStream();
            dis = new DataInputStream(is);
            OutputStream os = clientSocket.getOutputStream();
            dos = new DataOutputStream(os);
            String line = dis.readUTF();
            String[] msgArr = line.split(" ");
            userName = msgArr[0].trim().substring(1);
            addUser(userName);
            writeLobbyStateToAll();
        } catch (Exception e) {
            // AppendText("userService error");
        }
    }

    public void writeOne(String msg) {
        try {
            dos.writeUTF(msg);
            System.out.println("--------------send----------------");
            System.out.println(msg);
            System.out.println("-------------------------------------");
        } catch (IOException e) {
            System.out.println("writeOne error");
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

    private void close() {
        removeUser(this.userName);
        onUserRemove.call(this);
        writeLobbyStateToAll();
        try {
            dos.close();
            dis.close();
            clientSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addUser(String userName) {
        LobbyStateRepository lobbyStateRepository = LobbyStateRepository.getInstance();
        lobbyStateRepository.addUserName(userName);
    }

    private void removeUser(String userName) {
        LobbyStateRepository lobbyStateRepository = LobbyStateRepository.getInstance();
        lobbyStateRepository.removeLobbyUser(userName);
    }

    private void writeSoundToOne(Sound sound) {
        String json = new Gson().toJson(sound);
        writeOne(json);
    }

    private void writeSoundToAll(Sound sound) {
        String json = new Gson().toJson(sound);
        writeAll.call(json);
    }

    private void writeLobbyStateToAll() {
        LobbyState lobbyState = LobbyStateRepository.getInstance().getLobbyState();
        String stateJson = new Gson().toJson(lobbyState);
        writeAll.call(stateJson);
    }

    private void writeGameStateToAll() {
        GameState gameState = GameStateRepository.getInstance().getGameState();
        String stateJson = new Gson().toJson(gameState);
        writeAll.call(stateJson);
    }

    public void run() {
        while (true) {
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
                        case "getLobbyState" -> {
                            LobbyStateRepository lobbyStateRepository = LobbyStateRepository.getInstance();
                            LobbyState lobbyState = lobbyStateRepository.getLobbyState();
                            String stateJson = new Gson().toJson(lobbyState);
                            writeOne(stateJson);
                        }
                        case "startGame" -> {
                            LobbyStateRepository lobbyStateRepository = LobbyStateRepository.getInstance();

                            if (lobbyStateRepository.getLobbyUserCounts() < 2) {
                                System.out.println("----------------error----------------");
                                System.out.println("인원 부족");
                                System.out.println("-------------------------------------");
                                continue;
                            }

                            writeAll.call("/startGame");

                            gameStateRepository.initState(lobbyStateRepository.getLobbyUserNames());
                            writeGameStateToAll();

                            GameStateTicker gameStateTicker = new GameStateTicker();
                            gameStateTicker.start();
                        }
                        case "up", "down", "left", "right" -> {
                            if (gameStateRepository.isEnded()) {
                                continue;
                            }
                            String name = msgArr[0].substring(1);
                            String action = msgArr[1];
                            Direction direction = Direction.valueOf(action.toUpperCase());
                            gameStateRepository.movePlayer(name, direction);
                            writeGameStateToAll();
                        }
                        case "installWaterBomb" -> {
                            if (gameStateRepository.isEnded()) {
                                continue;
                            }
                            String playerName = msgArr[0].substring(1);
                            boolean installed = gameStateRepository.installWaterBomb(playerName);
                            if (installed) {
                                writeSoundToOne(Sound.BOMB_SET);
                            }
                            writeGameStateToAll();
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Message Separate Error");
                    e.printStackTrace();
                }
            } catch (IOException e) {
                System.out.println("dis.read() error");

                GameStateRepository gameStateRepository = GameStateRepository.getInstance();
                gameStateRepository.removeTerminatedUser(this.userName);

                e.printStackTrace();
                break;
            }
        }
        close();
    }

    private class GameStateTicker extends Thread {

        private void writeSoundIfExists() {
            GameStateRepository stateRepository = GameStateRepository.getInstance();
            GameState state = stateRepository.getGameState();
            List<Sound> sounds = state.getShouldBePlayedSounds();

            sounds.forEach((UserService.this::writeSoundToAll));

            state.playedSounds(sounds);
        }

        @Override
        public void run() {
            while (true) {
                try {
                    sleep(100);
                } catch (InterruptedException e) {
                    break;
                }

                GameStateRepository stateRepository = GameStateRepository.getInstance();
                stateRepository.updateState();
                writeSoundIfExists();

                writeGameStateToAll();

                GameState state = stateRepository.getGameState();
                if (state.isEnded()) {
                    stateRepository.clear();
                    break;
                }
            }
        }
    }
}