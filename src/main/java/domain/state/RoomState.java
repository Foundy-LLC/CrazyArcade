package domain.state;

import domain.model.Player;
import lombok.Getter;
import lombok.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RoomState implements Serializable {

    public static final int MAX_USER = 8;

    @NonNull
    @Getter
    private final String id;

    @NonNull
    @Getter
    private final String roomName;

    @NonNull
    @Getter
    private String adminName;

    @NonNull
    @Getter
    private final List<String> userNames = new ArrayList<>(MAX_USER);

    @Getter
    private GameState gameState = null;

    public RoomState(@NonNull String creatorName, @NonNull String roomName) {
        this.id = UUID.randomUUID().toString();
        this.roomName = roomName;
        this.adminName = creatorName;
        join(creatorName);
    }

    public void join(@NonNull String userName) {
        userNames.add(userName);
    }

    public void exit(@NonNull String userName) {
        userNames.remove(userName);
        if (adminName.equals(userName) && !isEmpty()) {
            adminName = userNames.get(0);
        }
        if (gameInProgress()) {
            gameState.exitPlayer(userName);
        }
    }

    public boolean isEmpty() {
        return userNames.isEmpty();
    }

    public boolean isFull() {
        return userNames.size() >= MAX_USER;
    }

    public boolean hasUser(String userName) {
        return userNames.contains(userName);
    }

    public void startGame(GameState gameState) {
        this.gameState = gameState;
    }

    public void endGame() {
        this.gameState = null;
    }

    public boolean gameInProgress() {
        return gameState != null;
    }

    public Player requirePlayerByName(String name) {
        List<Player> players = gameState.getPlayers();
        for (Player player : players) {
            if (player.getName().equals(name)) {
                return player;
            }
        }
        throw new IllegalArgumentException("해당 유저는 없습니다.");
    }

    public int getUserCount() {
        return userNames.size();
    }
}
