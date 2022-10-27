package client.view;

import client.component.BaseView;
import client.component.Map;
import client.component.PlayerObject;
import client.util.ImageIcons;
import domain.model.Offset;
import domain.model.Player;
import domain.state.GameState;
import lombok.NonNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static domain.mockup.MockPlayers.player1;

public class GameView extends BaseView {

    @NonNull
    private GameState state = GameState.getInitState(List.of(player1));

    @NonNull
    private final List<PlayerObject> playerObjects = new ArrayList<>();

    public GameView() {
        super(ImageIcons.GAME_BACKGROUND);
        initView();
    }

    private void initView() {
        final Map map = new Map();
        add(map);

        List<Player> players = state.getPlayers();
        players.forEach((player) -> {
            Offset offset = player.getOffset();
            PlayerObject playerObject = new PlayerObject();
            playerObject.setOffset(offset);

            playerObjects.add(playerObject);
            map.add(playerObject);
        });
    }

    private void updateView() {
        List<Player> players = state.getPlayers();
    }
}
