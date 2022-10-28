package client.view;

import client.component.BaseView;
import client.component.BlockComponent;
import client.component.MapView;
import client.component.PlayerComponent;
import client.service.Api;
import client.service.MessageListener;
import client.util.ImageIcons;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import domain.constant.Sizes;
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

    private void initMapComponent(Map map) {
        Block[][] block2D = map.getBlock2D();
        for (int i = 0; i < Sizes.TILE_COLUMN_COUNT; ++i) {
            for (int j = 0; j < Sizes.TILE_ROW_COUNT; ++j) {
                if (block2D[i][j] != null) {
                    mapView.add(new BlockComponent(new Offset(j, i)));
                }
            }
        }
    }

    private void updateView(@NonNull GameState state) {
        if (!isComponentInitialized) {
            isComponentInitialized = true;
            initPlayerComponents(state.getPlayers());
            initMapComponent(state.getMap());

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
