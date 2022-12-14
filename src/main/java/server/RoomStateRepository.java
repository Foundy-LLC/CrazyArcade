package server;

import domain.mockup.MockMaps;
import domain.model.*;
import domain.state.GameState;
import domain.state.RoomState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

public class RoomStateRepository {

    private final List<RoomState> roomStateList = new ArrayList<>(12);

    private final HashMap<RoomState, GameStateTicker> tickerHashMap = new HashMap<>();

    public RoomStateRepository() {
    }

    public static RoomStateRepository getInstance() {
        return RoomStateRepository.Holder.INSTANCE;
    }

    private static class Holder {
        private static final RoomStateRepository INSTANCE = new RoomStateRepository();
    }

    public void createAndJoinRoom(String creatorName, String roomName) {
        RoomState newRoom = new RoomState(creatorName, roomName);
        roomStateList.add(newRoom);
    }

    public RoomState joinRoom(String joinerName, String roomId) {
        Optional<RoomState> roomStateOptional = roomStateList.stream()
                .filter((room) -> room.getId().equals(roomId))
                .findFirst();
        if (roomStateOptional.isPresent()) {
            RoomState room = roomStateOptional.get();
            if (room.isFull() || room.gameInProgress()) {
                return null;
            }
            roomStateOptional.ifPresent((roomState) -> roomState.join(joinerName));
            return room;
        }
        return null;
    }

    public RoomState exitRoom(String userName) {
        RoomState room = requireRoomByUserName(userName);
        room.exit(userName);
        if (room.isEmpty()) {
            roomStateList.remove(room);
        }
        return room;
    }

    public List<RoomDto> getCurrentRoomDtoList() {
        ArrayList<RoomDto> roomDtos = new ArrayList<>(8);
        roomStateList.forEach((roomState -> {
            RoomDto roomDto = new RoomDto(
                    roomState.getId(),
                    roomState.getRoomName(),
                    roomState.getUserCount(),
                    roomState.gameInProgress()
            );
            roomDtos.add(roomDto);
        }));
        return roomDtos;
    }

    public RoomState startGame(
            List<String> userNames,
            BiConsumer<Sound, RoomState> onSoundShouldPlay,
            Callback<RoomState> onGameStateUpdated,
            Runnable onGameEnd
    ) {
        RoomState room = requireRoomByUserName(userNames.get(0));
        Map map = MockMaps.generateMap();
        Offset[] startingPoints = map.getShuffledStartingPoints();
        List<Player> players = new ArrayList<>(8);

        for (int i = 0; i < userNames.size(); ++i) {
            Offset startingPoint = startingPoints[i];
            players.add(new Player(userNames.get(i), startingPoint.x, startingPoint.y));
        }

        GameState gameState = GameState.builder()
                .players(players)
                .remainingTimeSec(3 * 60)
                .map(map)
                .build();
        room.startGame(gameState);

        GameStateTicker gameStateTicker = new GameStateTicker(
                gameState,
                (sound) -> onSoundShouldPlay.accept(sound, room),
                (empty) -> onGameStateUpdated.call(room),
                (Void) -> {
                    room.endGame();
                    tickerHashMap.remove(room);
                    onGameEnd.run();
                }
        );
        gameStateTicker.start();
        tickerHashMap.put(room, gameStateTicker);

        return room;
    }

    public RoomState movePlayer(String userName, Direction direction) {
        RoomState room = requireRoomByUserName(userName);
        if (!room.gameInProgress()) {
            throw new IllegalStateException("?????? ???????????? ????????????.");
        }
        Player player = room.requirePlayerByName(userName);
        if (player != null) {
            player.setDirection(direction);
            player.move(direction, room.getGameState().getMap());
        }
        return room;
    }

    public boolean installWaterBomb(String playerName) {
        RoomState room = requireRoomByUserName(playerName);
        if (!room.gameInProgress()) {
            throw new IllegalStateException("?????? ???????????? ????????????.");
        }
        GameState gameState = room.getGameState();
        Player player = room.requirePlayerByName(playerName);
        if (player != null && !player.isTrapped()) {
            int installedCount = gameState.countPlayerWaterBombs(player);
            if (installedCount == player.getMaxWaterBombCount()) {
                return false;
            }

            Offset playerCenterTileOffset = player.getCenterTileOffset();
            if (gameState.canInstallWaterBombAt(playerCenterTileOffset)) {
                WaterBomb waterBomb = player.createWaterBomb();
                gameState.installWaterBomb(waterBomb, playerCenterTileOffset);
                return true;
            }
        }
        return false;
    }

    public RoomState removeTerminatedUser(String userName) {
        RoomState room = requireRoomByUserName(userName);
        exitRoom(userName);
        if (room.gameInProgress()) {
            room.getGameState().updateState();
        }
        return room;
    }

    public RoomState requireRoomByUserName(String userName) {
        Optional<RoomState> roomStateOptional = roomStateList.stream()
                .filter((room) -> room.hasUser(userName))
                .findFirst();
        if (roomStateOptional.isPresent()) {
            return roomStateOptional.get();
        }
        throw new IllegalStateException("?????? ????????? ????????? ?????? ????????????.");
    }
}
