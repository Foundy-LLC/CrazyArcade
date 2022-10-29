package client.view;

import client.component.GameEndTextLabel;
import client.core.BaseView;
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

import javax.swing.Timer;
import java.awt.event.*;
import java.util.*;

public class GameView extends BaseView {

    @NonNull
    private final HashMap<String, PlayerComponent> playerMap = new HashMap<>();

    @NonNull
    private final MapView mapView = new MapView();

    private boolean isComponentInitialized = false;

    @NonNull
    private final Api api = Api.getInstance();

    private KeyboardListener keyboardListener;

    private boolean isGameEnded = false;

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

            playerMap.put(player.getName(), playerObject);
            mapView.add(playerObject);
        });
    }

    private void updateView(GameState gameState) {
        if (isGameEnded) {
            return;
        }

        if (!isComponentInitialized) {
            isComponentInitialized = true;
            initPlayerComponents(gameState.getPlayers());

            requestFocus();
        }

        if (gameState.isEnded()) {
            isGameEnded = true;
            showGameEndText(gameState.getWinner());
            Timer timer = new Timer(3_000, arg0 -> navigateTo(new LobbyView()));
            timer.setRepeats(false);
            timer.start();
            return;
        }

        updatePlayerObjects(gameState.getPlayers());
        mapView.updateMap(gameState.getMap());

        mapView.repaint();
    }

    private void updatePlayerObjects(@NonNull List<Player> players) {
        // 사망자가 발생한 경우
        if (players.size() != playerMap.size()) {
            List<String> deadPlayerNames = new ArrayList<>(List.copyOf(playerMap.keySet()));
            for (Player alivePlayer : players) {
                deadPlayerNames.remove(alivePlayer.getName());
            }

            deadPlayerNames.forEach((deadPlayerName) -> {
                PlayerComponent deadPlayerComponent = playerMap.get(deadPlayerName);
                playerMap.remove(deadPlayerName);
                mapView.remove(deadPlayerComponent);
            });
        }

        for (var entry : playerMap.entrySet()) {
            String name = entry.getKey();
            PlayerComponent component = entry.getValue();
            Optional<Player> player = players.stream().filter((element) -> element.getName().equals(name)).findFirst();
            player.ifPresent(component::updateState);
        }
    }

    private void showGameEndText(Player winner) {
        GameEndTextLabel.Type type;
        boolean draw = winner == null;
        if (draw) {
            type = GameEndTextLabel.Type.DRAW;
        } else {
            boolean win = winner.getName().equals(api.getUserName());
            if (win) {
                type = GameEndTextLabel.Type.WIN;
            } else {
                type = GameEndTextLabel.Type.LOSE;
            }
        }

        GameEndTextLabel gameEndTextLabel = new GameEndTextLabel(type);
        add(gameEndTextLabel);
        setComponentZOrder(gameEndTextLabel, 0);
        repaint();
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
            timer = new Timer(24, arg0 -> {
                if (!pressedKeys.isEmpty()) {
                    for (Integer pressedKey : pressedKeys) {
                        switch (pressedKey) {
                            case KeyEvent.VK_UP -> api.movePlayer(Direction.UP);
                            case KeyEvent.VK_DOWN -> api.movePlayer(Direction.DOWN);
                            case KeyEvent.VK_LEFT -> api.movePlayer(Direction.LEFT);
                            case KeyEvent.VK_RIGHT -> api.movePlayer(Direction.RIGHT);
                            case KeyEvent.VK_SPACE -> {
                                continue;
                            }
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
            // 스페이스바를 꾹 누르고 있어도 처음 한 번만 호출한다.
            if (!pressedKeys.contains(keyCode) && event.getKeyChar() == ' ') {
                Api.getInstance().installWaterBomb();
            }

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
