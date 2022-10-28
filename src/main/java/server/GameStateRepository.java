package server;

import domain.mockup.MockMaps;
import domain.model.Direction;
import domain.model.Offset;
import domain.model.Player;
import domain.model.WaterBomb;
import domain.state.GameState;

import java.util.ArrayList;
import java.util.List;

public class GameStateRepository {

    private GameState gameState;

    private GameStateRepository() {}

    public static GameStateRepository getInstance() {
        return Holder.INSTANCE;
    }

    private static class Holder {
        private static final GameStateRepository INSTANCE = new GameStateRepository();
    }

    public GameState initState(List<String> names) {
        List<Player> players = new ArrayList<>(8);
        names.forEach((name) -> {
            // TODO 위치 지정하기
            players.add(new Player(name, 0, 0));
        });

        gameState = GameState.builder()
                .players(players)
                .remainingTimeSec(3 * 60)
                .map(MockMaps.map1)
                .build();
        return gameState;
    }

    public GameState movePlayer(String name, Direction direction) {
        Player player = findPlayer(name);
        if (player != null) {
            player.setDirection(direction);
            player.move(direction, gameState.getMap());
        }
        return gameState;
    }

    public GameState installWaterBomb(String playerName) {
        Player player = findPlayer(playerName);
        WaterBomb[][] waterBomb2d = gameState.getMap().getWaterBomb2d();
        if (player != null) {
            Offset playerCenterTileOffset = player.getCenterTileOffset();
            if (waterBomb2d[playerCenterTileOffset.y][playerCenterTileOffset.x] == null) {
                waterBomb2d[playerCenterTileOffset.y][playerCenterTileOffset.x] = new WaterBomb();
            }
        }
        return gameState;
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
}
