package server;

import java.io.*;
import java.net.Socket;

import domain.model.Direction;
import domain.state.GameState;
import domain.state.LobbyState;

public class UserService extends Thread {

    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;

    private final Socket clientSocket;

    private String userName = "";

    private final Callback<Object> writeAll;

    private final Callback<UserService> onUserRemove;

    public UserService(Socket clientSocket, Callback<Object> writeAll, Callback<UserService> onUserRemove) {
        this.clientSocket = clientSocket;
        this.writeAll = writeAll;
        this.onUserRemove = onUserRemove;
        try {
            InputStream is = clientSocket.getInputStream();
            inputStream = new ObjectInputStream(is);
            OutputStream os = clientSocket.getOutputStream();
            outputStream = new ObjectOutputStream(os);
            outputStream.flush();
            String line = readMessage();
            String[] msgArr = line.split(" ");
            userName = msgArr[0].trim().substring(1);
            addUser(userName);
            writeLobbyStateToAll();
        } catch (Exception e) {
            // AppendText("userService error");
        }
    }

    private String readMessage() throws IOException {
        try {
            Object object = inputStream.readObject();
            if (object instanceof String) {
                return ((String) object).trim();
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        throw new IllegalStateException();
    }

    public void writeToThis(Object object) {
        try {
            outputStream.writeObject(object);
            outputStream.flush();

            System.out.println("=============== SEND ===============");
            System.out.println("TO: " + userName);
            System.out.println("DATA: " + object);
            System.out.println("====================================");
        } catch (IOException e) {
            System.out.println("writeToThis error");
            try {
                outputStream.close();
                inputStream.close();
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
            outputStream.close();
            inputStream.close();
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

    private void writeLobbyStateToAll() {
        LobbyState lobbyState = LobbyStateRepository.getInstance().getLobbyState();
        writeAll.call(lobbyState);
    }

    public void run() {
        while (true) {
            try {
                String msg = readMessage();
                System.out.println("============= RECEIVE ==============");
                System.out.println("FROM: " + userName);
                System.out.println("MSG: " + msg);
                System.out.println("====================================");
                try {
                    // String[] msgArr = msg.split(Constants.MESSAGE_SEPARATOR.toString());
                    String[] msgArr = msg.split(" ");
                    GameStateRepository gameStateRepository = GameStateRepository.getInstance();

                    switch (msgArr[1]) {
                        case "getLobbyState" -> {
                            LobbyStateRepository lobbyStateRepository = LobbyStateRepository.getInstance();
                            LobbyState lobbyState = lobbyStateRepository.getLobbyState();
                            writeToThis(lobbyState);
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

                            GameState initGameState = gameStateRepository.initState(lobbyStateRepository.getLobbyUserNames());
                            writeAll.call(initGameState);

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
                            GameState newState = gameStateRepository.movePlayer(name, direction);
                            writeAll.call(newState);
                        }
                        case "waterBomb" -> {
                            if (gameStateRepository.isEnded()) {
                                continue;
                            }
                            String playerName = msgArr[0].substring(1);
                            GameState newState = gameStateRepository.installWaterBomb(playerName);
                            writeAll.call(newState);
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

                GameState state = stateRepository.getGameState();
                writeAll.call(state);

                if (state.isEnded()) {
                    stateRepository.clear();
                    break;
                }
            }
        }
    }
}