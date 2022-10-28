package client.view;

import client.component.BaseView;
import client.component.MapView;
import client.component.PlayerComponent;
import client.service.Api;
import client.service.MessageListener;
import client.util.ImageIcons;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import domain.model.*;
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

    private boolean isComponentInitialized = false;

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

    private void initPlayerComponents(List<Player> players) {
        players.forEach((player) -> {
            Offset offset = player.getOffset();
            PlayerComponent playerObject = new PlayerComponent();
            playerObject.setOffset(offset);

            playerObjects.add(playerObject);
            mapView.add(playerObject);
        });
    }

    private void updateView(@NonNull GameState state) {
        if (!isComponentInitialized) {
            isComponentInitialized = true;
            initPlayerComponents(state.getPlayers());

            requestFocus();
        }

        updatePlayerObjects(state.getPlayers());
        mapView.updateMap(state.getMap());

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
        mapView.dispose();
    }

    public static class KeyboardListener extends KeyAdapter {

        /**
         * Stores currently pressed keys
         */
        private final HashSet<Integer> pressedKeys = new HashSet<>();
        private final Timer timer;

        public KeyboardListener() {
            final Api api = Api.getInstance();
            timer = new Timer(6, arg0 -> {
                if (!pressedKeys.isEmpty()) {
                    for (Integer pressedKey : pressedKeys) {
                        switch (pressedKey) {
                            case KeyEvent.VK_UP -> api.movePlayer(Direction.UP);
                            case KeyEvent.VK_DOWN -> api.movePlayer(Direction.DOWN);
                            case KeyEvent.VK_LEFT -> api.movePlayer(Direction.LEFT);
                            case KeyEvent.VK_RIGHT -> api.movePlayer(Direction.RIGHT);
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

        @Override
        public void keyTyped(KeyEvent e) {
            if (e.getKeyChar() == ' ') {
                Api.getInstance().installWaterBomb();
            }
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
