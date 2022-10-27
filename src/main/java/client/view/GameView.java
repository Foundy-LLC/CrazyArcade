package client.view;

import client.component.BaseView;
import client.component.Map;
import client.component.PlayerObject;
import client.service.Api;
import client.service.MessageListener;
import client.util.ImageIcons;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import domain.mockup.MockPlayers;
import domain.model.Direction;
import domain.model.Offset;
import domain.model.Player;
import domain.state.GameState;
import lombok.NonNull;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class GameView extends BaseView {

    @NonNull
    private final List<PlayerObject> playerObjects = new ArrayList<>();

    @NonNull
    private final Map map = new Map();

    @NonNull
    private boolean isPlayerAdded = false;

    @NonNull
    private final Api api = Api.getInstance();

    public GameView() {
        super(ImageIcons.GAME_BACKGROUND);
        initView();
        initListener();

        new MyThread().start();
    }

    private void initView() {
        add(map);
    }

    private void initListener() {
        addKeyListener(new KeyboardListener());
        api.addListener(messageListener);
    }

    private void updateView(@NonNull GameState state) {
        if (!isPlayerAdded) {
            isPlayerAdded = true;
            List<Player> players = state.getPlayers();
            players.forEach((player) -> {
                Offset offset = player.getOffset();
                PlayerObject playerObject = new PlayerObject();
                playerObject.setOffset(offset);

                playerObjects.add(playerObject);
                map.add(playerObject);
            });
        }

        updatePlayerObjects(state.getPlayers());

        map.repaint();
    }

    private void updatePlayerObjects(@NonNull List<Player> players) {
        final int size = players.size();
        for (int i = 0; i < size; ++i) {
            playerObjects.get(i).updateState(players.get(i));
        }
    }

    @Override
    protected void onRemoved() {
        api.removeListener(messageListener);
    }

    private class MyThread extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    sleep(50);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                Api.getInstance().movePlayer(Direction.DOWN);
            }
        }
    }

    private static class KeyboardListener extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            Direction direction = switch (e.getKeyCode()) {
                case KeyEvent.VK_UP -> Direction.UP;
                case KeyEvent.VK_DOWN -> Direction.DOWN;
                case KeyEvent.VK_LEFT -> Direction.LEFT;
                case KeyEvent.VK_RIGHT -> Direction.RIGHT;
                default -> throw new IllegalStateException();
            };
            Api.getInstance().movePlayer(direction);
        }
    }

    private final MessageListener messageListener = (message) -> {
        try {
            GameState state = new Gson().fromJson(message, GameState.class);
            updateView(state);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            showToast("잘못된 형식의 GameState 객체 전달됨");
        }
    };
}
