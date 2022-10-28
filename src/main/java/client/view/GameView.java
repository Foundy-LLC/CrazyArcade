package client.view;

import client.component.BaseView;
import client.component.MapView;
import client.component.PlayerComponent;
import client.service.Api;
import client.service.MessageListener;
import client.util.ImageIcons;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import domain.model.Direction;
import domain.model.Offset;
import domain.model.Player;
import domain.state.GameState;
import lombok.NonNull;

import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class GameView extends BaseView {

    @NonNull
    private final List<PlayerComponent> playerObjects = new ArrayList<>();

    @NonNull
    private final MapView mapView = new MapView();

    private boolean isPlayerAdded = false;

    @NonNull
    private final Api api = Api.getInstance();

    private KeyboardListener keyboardListener;

    public GameView() {
        super(ImageIcons.GAME_BACKGROUND);
        initView();
        initListener();
    }

    private void initView() {
        add(mapView);
    }

    private void initListener() {
        keyboardListener = new KeyboardListener();
        addKeyListener(keyboardListener);
        api.addListener(messageListener);
    }

    private void updateView(@NonNull GameState state) {
        if (!isPlayerAdded) {
            isPlayerAdded = true;
            List<Player> players = state.getPlayers();
            players.forEach((player) -> {
                Offset offset = player.getOffset();
                PlayerComponent playerObject = new PlayerComponent();
                playerObject.setOffset(offset);

                playerObjects.add(playerObject);
                mapView.add(playerObject);
            });
            requestFocus();
        }

        updatePlayerObjects(state.getPlayers());

        mapView.repaint();
    }

    private void updatePlayerObjects(@NonNull List<Player> players) {
        final int size = players.size();
        for (int i = 0; i < size; ++i) {
            playerObjects.get(i).updateState(players.get(i));
        }
    }

    @Override
    protected void onDestroyed() {
        api.removeListener(messageListener);
        keyboardListener.stop();
    }

    public static class KeyboardListener extends KeyAdapter {

        /**
         * Stores currently pressed keys
         */
        private final HashSet<Integer> pressedKeys = new HashSet<>();
        private final Timer timer;

        public KeyboardListener() {
            timer = new Timer(6, arg0 -> {
                if (!pressedKeys.isEmpty()) {
                    for (Integer pressedKey : pressedKeys) {
                        switch (pressedKey) {
                            case KeyEvent.VK_UP -> Api.getInstance().movePlayer(Direction.UP);
                            case KeyEvent.VK_DOWN -> Api.getInstance().movePlayer(Direction.DOWN);
                            case KeyEvent.VK_LEFT -> Api.getInstance().movePlayer(Direction.LEFT);
                            case KeyEvent.VK_RIGHT -> Api.getInstance().movePlayer(Direction.RIGHT);
                        }
                        break;
                    }
                }
            });
            timer.start();
        }

        public void stop() {
            timer.stop();
        }

        @Override
        public void keyPressed(KeyEvent event) {
            int keyCode = event.getKeyCode();
            pressedKeys.add(keyCode);
        }

        @Override
        public void keyReleased(KeyEvent event) {
            int keyCode = event.getKeyCode();
            pressedKeys.remove(keyCode);
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
