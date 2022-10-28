package server;

import domain.mockup.MockMaps;
import domain.model.Direction;
import domain.model.Player;
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
                .waterBombs(new ArrayList<>(8))
                .build();
        return gameState;
    }

    public GameState movePlayer(String name, Direction direction) {
        List<Player> players = gameState.getPlayers();
        players.forEach((player) -> {
            if (player.getName().equals(name)) {
                player.setDirection(direction);
                player.move(direction, gameState.getMap());
            }
        });
        return gameState;
    }
}
