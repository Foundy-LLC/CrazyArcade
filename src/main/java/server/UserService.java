package server;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;

import com.google.gson.Gson;
import domain.constant.Protocol;
import domain.model.Direction;
import domain.model.RoomDto;
import domain.model.Sound;
import domain.state.GameState;
import domain.state.LobbyState;
import domain.state.RoomState;
import lombok.Getter;

public class UserService extends Thread {
    private DataInputStream dis;
    private DataOutputStream dos;
    private final Socket clientSocket;

    @Getter
    private String userName = "";

    private final Callback<String> writeAll;

    private final BiConsumer<String, List<String>> writeToSome;

    private final Callback<UserService> onUserRemove;

    public UserService(
            Socket clientSocket,
            Callback<String> writeAll,
            BiConsumer<String, List<String>> writeToSome,
            Callback<UserService> onUserRemove
    ) {
        this.clientSocket = clientSocket;
        this.writeAll = writeAll;
        this.writeToSome = writeToSome;
        this.onUserRemove = onUserRemove;
        try {
            InputStream is = clientSocket.getInputStream();
            dis = new DataInputStream(is);
            OutputStream os = clientSocket.getOutputStream();
            dos = new DataOutputStream(os);
            String line = dis.readUTF();
            String[] msgArr = line.split(" ");
            userName = msgArr[0].trim();
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
        RoomStateRepository roomStateRepository = RoomStateRepository.getInstance();
        roomStateRepository.removeTerminatedUser(this.userName);
        onUserRemove.call(this);
        //writeLobbyStateToAll();
        try {
            dos.close();
            dis.close();
            clientSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writeSoundToOne(Sound sound) {
        String json = new Gson().toJson(sound);
        writeOne(json);
    }

    private void writeSoundToAll(Sound sound, RoomState roomState) {
        String json = new Gson().toJson(sound);
        List<String> users = roomState.getUserNames();
        writeToSome.accept(json, users);
    }

    private void writeRoomStateToAllIn(RoomState room) {
        String stateJson = new Gson().toJson(room);
        List<String> users = room.getUserNames();
        writeToSome.accept(stateJson, users);
    }

    /**
     * 방에 참여한 사람들에게 게임 상태를 전달한다.
     */
    private void writeGameStateToAllIn(RoomState room) {
        GameState gameState = room.getGameState();
        List<String> users = room.getUserNames();
        String stateJson = new Gson().toJson(gameState);
        writeToSome.accept(stateJson, users);
    }

    private void writeLobbyStateToAll() {
        RoomStateRepository roomStateRepository = RoomStateRepository.getInstance();
        List<RoomDto> roomDtoList = roomStateRepository.getCurrentRoomDtoList();
        LobbyState lobbyState = new LobbyState(roomDtoList);
        String stateJson = new Gson().toJson(lobbyState);
        writeAll.call(stateJson);
    }

    private void writeLobbyStateToOne() {
        RoomStateRepository roomStateRepository = RoomStateRepository.getInstance();
        List<RoomDto> roomDtoList = roomStateRepository.getCurrentRoomDtoList();
        LobbyState lobbyState = new LobbyState(roomDtoList);
        String stateJson = new Gson().toJson(lobbyState);
        writeOne(stateJson);
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
                    final String userName = msgArr[0];
                    System.out.println(Arrays.toString(msgArr));
                    RoomStateRepository roomStateRepository = RoomStateRepository.getInstance();

                    switch (msgArr[1]) {
                        case Protocol.GET_LOBBY_STATE -> writeLobbyStateToOne();
                        case Protocol.GAME_START -> {
                            RoomState room = roomStateRepository.requireRoomByUserName(userName);
                            if (room.getUserCount() < 2) {
                                System.out.println("----------------error----------------");
                                System.out.println("인원 부족");
                                System.out.println("-------------------------------------");
                                continue;
                            }

                            writeToSome.accept(Protocol.GAME_START, room.getUserNames());

                            RoomState roomState = roomStateRepository.startGame(
                                    room.getUserNames(),
                                    this::writeSoundToAll,
                                    this::writeGameStateToAllIn
                            );
                            writeGameStateToAllIn(roomState);
                        }
                        case Protocol.MOVE -> {
                            String action = msgArr[2];
                            Direction direction = Direction.valueOf(action.toUpperCase());
                            RoomState roomState = roomStateRepository.movePlayer(userName, direction);
                            writeGameStateToAllIn(roomState);
                        }
                        case Protocol.INSTALL_WATER_BOMB -> {
                            String playerName = msgArr[0];
                            boolean installed = roomStateRepository.installWaterBomb(playerName);
                            RoomState roomState = roomStateRepository.requireRoomByUserName(playerName);
                            if (installed) {
                                writeSoundToOne(Sound.BOMB_SET);
                            }
                            writeGameStateToAllIn(roomState);
                        }
                        case Protocol.GET_ROOM_STATE -> {
                            RoomState roomState = roomStateRepository.requireRoomByUserName(userName);
                            String json = new Gson().toJson(roomState);
                            writeOne(json);
                        }
                        case Protocol.MAKE_ROOM -> {
                            String roomName = msgArr[2];
                            roomStateRepository.createAndJoinRoom(userName, roomName);
                            writeOne(Protocol.MAKE_ROOM);
                            writeLobbyStateToAll();
                        }
                        case Protocol.JOIN_ROOM -> {
                            String roomId = msgArr[2];
                            RoomState roomState = roomStateRepository.joinRoom(userName, roomId);
                            if (roomState == null) {
                                writeLobbyStateToOne();
                            } else {
                                writeOne(Protocol.JOIN_ROOM);
                                writeRoomStateToAllIn(roomState);
                            }
                        }
                        case Protocol.EXIT_ROOM -> {
                            RoomState roomState = roomStateRepository.exitRoom(userName);
                            writeRoomStateToAllIn(roomState);
                            writeLobbyStateToAll();
                        }
                        default -> throw new IllegalArgumentException("존재하지 않는 프로토콜을 수신했습니다.");
                    }
                } catch (Exception e) {
                    System.out.println("Message Separate Error");
                    e.printStackTrace();
                }
            } catch (IOException e) {
                System.out.println("dis.read() error");

                RoomStateRepository roomStateRepository = RoomStateRepository.getInstance();
                RoomState roomState = roomStateRepository.removeTerminatedUser(this.userName);
                writeRoomStateToAllIn(roomState);

                e.printStackTrace();
                break;
            }
        }
        close();
    }
}