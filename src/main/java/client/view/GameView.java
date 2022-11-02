package client.view;

import client.component.GameEndTextLabel;
import client.core.ApiListenerView;
import client.component.MapPanel;
import client.service.Api;
import client.constant.ImageIcons;
import client.service.SoundController;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import domain.model.*;
import domain.state.GameState;
import lombok.NonNull;

import javax.swing.Timer;
import java.awt.event.*;
import java.util.*;

public class GameView extends ApiListenerView {

    @NonNull
    private final MapPanel mapPanel = new MapPanel();

    private KeyboardListener keyboardListener;

    private boolean isGameEnded = false;

    public GameView() {
        super(ImageIcons.GAME_BACKGROUND);
        initView();
        initListener();
    }

    private void initView() {
        add(mapPanel);
    }

    private void initListener() {
        keyboardListener = new KeyboardListener();
        addKeyListener(keyboardListener);
    }

    private void updateView(GameState gameState) {
        if (isGameEnded) {
            return;
        }

        if (gameState.isEnded()) {
            isGameEnded = true;
            SoundController.stopLoop();
            playGameEndingEffect(gameState.getWinner());
            Timer timer = new Timer(3_000, arg0 -> navigateTo(new LobbyView()));
            timer.setRepeats(false);
            timer.start();
            return;
        }

        mapPanel.repaint(gameState.getMap(), gameState.getPlayers());

        requestFocus();
    }

    private void playGameEndingEffect(Player winner) {
        Api api = Api.getInstance();
        GameEndTextLabel.Type type;
        boolean draw = winner == null;
        Sound sound;
        if (draw) {
            type = GameEndTextLabel.Type.DRAW;
            sound = Sound.DRAW;
        } else {
            boolean win = winner.getName().equals(api.getUserName());
            if (win) {
                type = GameEndTextLabel.Type.WIN;
                sound = Sound.WIN;
            } else {
                type = GameEndTextLabel.Type.LOSE;
                sound = Sound.LOSE;
            }
        }

        SoundController.play(sound);

        GameEndTextLabel gameEndTextLabel = new GameEndTextLabel(type);
        add(gameEndTextLabel);
        setComponentZOrder(gameEndTextLabel, 0);
        repaint();
    }

    @Override
    protected void onMessageReceived(String message) {
        try {
            GameState state = new Gson().fromJson(message, GameState.class);
            // 상대 플레이어가 비정상 종료된 경우 LobbyState 정보가 전달되기 때문에
            //noinspection ConstantConditions
            if (state.getMap() != null) {
                updateView(state);
            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            showToast("잘못된 형식의 GameState 객체 전달됨");
        }
    }

    @Override
    protected void onDestroyed() {
        keyboardListener.stop();
        mapPanel.dispose();
    }

    public static class KeyboardListener extends KeyAdapter {

        /**
         * Stores currently pressed keys
         */
        private final HashSet<Integer> pressedKeys = new HashSet<>();
        private final Timer timer;

        public KeyboardListener() {
            final Api api = Api.getInstance();
            timer = new Timer(30, arg0 -> {
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
}
