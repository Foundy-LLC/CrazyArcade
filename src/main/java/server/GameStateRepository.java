package server;

import domain.mockup.MockMaps;
import domain.model.Direction;
import domain.model.Offset;
import domain.model.Player;
import domain.model.WaterBomb;
import domain.state.GameState;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class GameStateRepository {

    @Getter
    private GameState gameState;

    private StateUpdater stateUpdater;

    private GameStateRepository() {
    }

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

        stateUpdater = new StateUpdater(gameState);
        stateUpdater.start();

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
        if (player != null) {
            Offset playerCenterTileOffset = player.getCenterTileOffset();
            if (gameState.canInstallWaterBombAt(playerCenterTileOffset)) {
                WaterBomb waterBomb = player.createWaterBomb();
                gameState.installWaterBomb(waterBomb, playerCenterTileOffset);
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

    public void clear() {
        stateUpdater.interrupt();
        stateUpdater = null;
    }

    private static class StateUpdater extends Thread {

        private final GameState state;

        private StateUpdater(GameState state) {
            this.state = state;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    sleep(100);
                } catch (InterruptedException e) {
                    break;
                }

                state.updateState();
            }
        }
    }
}
