package server;

import domain.mockup.MockMaps;
import domain.model.*;
import domain.state.GameState;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class GameStateRepository {

    @Getter
    private GameState gameState;

    private GameStateRepository() {
    }

    public static GameStateRepository getInstance() {
        return Holder.INSTANCE;
    }

    private static class Holder {
        private static final GameStateRepository INSTANCE = new GameStateRepository();
    }

    public boolean isEnded() {
        return gameState == null || gameState.isEnded();
    }

    public GameState initState(List<String> names) {
        Map map = MockMaps.map1;
        Offset[] startingPoints = map.getShuffledStartingPoints();
        List<Player> players = new ArrayList<>(8);

        for (int i = 0; i < names.size(); ++i) {
            Offset startingPoint = startingPoints[i];
            players.add(new Player(names.get(i), startingPoint.x, startingPoint.y));
        }

        gameState = GameState.builder()
                .players(players)
                .remainingTimeSec(3 * 60)
                .map(MockMaps.map1)
                .build();

        return gameState;
    }

    public void updateState() {
        gameState.updateState();
    }

    public void movePlayer(String name, Direction direction) {
        if (gameState.isEnded()) {
            return;
        }
        Player player = findPlayer(name);
        if (player != null) {
            player.setDirection(direction);
            player.move(direction, gameState.getMap());
        }
    }

    /**
     * @return 설치에 성공하면 `true`를 반환한다.
     */
    public boolean installWaterBomb(String playerName) {
        if (gameState.isEnded()) {
            return false;
        }
        Player player = findPlayer(playerName);
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

    private Player findPlayer(String name) {
        List<Player> players = gameState.getPlayers();
        for (Player player : players) {
            if (player.getName().equals(name)) {
                return player;
            }
        }
        return null;
    }

    public void removeTerminatedUser(String userName) {
        if (isEnded()) {
            return;
        }
        List<Player> players = gameState.getPlayers();
        Player terminatedPlayer = players.stream()
                .filter(player -> userName.equals(player.getName()))
                .findAny()
                .orElse(null);
        if (terminatedPlayer == null) {
            return;
        }
        terminatedPlayer.terminatePlayer();

        gameState.removeTerminatedPlayer(terminatedPlayer);
        gameState.updateState();
    }

    public void clear() {
        gameState = null;
    }
}
