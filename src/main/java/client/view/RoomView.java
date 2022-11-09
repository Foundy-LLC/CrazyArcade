package client.view;

import client.constant.Fonts;
import client.constant.ImageIcons;
import client.core.ApiListenerView;
import client.core.Button;
import client.core.OutlinedLabel;
import client.service.Api;
import client.service.SoundController;
import client.util.Navigator;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import domain.constant.Protocol;
import domain.model.Sound;
import domain.state.RoomState;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import java.util.Optional;

public class RoomView extends ApiListenerView {

    private final JTextArea userListTextArea = new JTextArea();

    private final Button startGameButton = new Button("게임 시작");

    private final JLabel backButton = new JLabel(ImageIcons.BACK_BUTTON);

    public RoomView() {
        super(ImageIcons.LOBBY_BACKGROUND);

        SoundController.playLoop(Sound.LOBBY_VIEW_BGM);

        initView();
        requestRoomState();
    }

    private void initView() {
        OutlinedLabel userListTitle = new OutlinedLabel("대기 인원", 2);
        userListTitle.setBounds(470, 320, 200, 40);
        userListTitle.setForeground(Color.white);
        userListTitle.setOutlineColor(Color.blue);
        userListTitle.setFont(Fonts.H5.deriveFont(Font.BOLD));
        add(userListTitle);

        userListTextArea.setEditable(false);
        userListTextArea.setFont(Fonts.H6);
        userListTextArea.setBounds(420, 360, 200, 120);
        add(userListTextArea);

        startGameButton.setEnabled(false);
        startGameButton.setBounds(420, 600, 200, 60);
        startGameButton.addActionListener(gameStartListener);
        add(startGameButton);

        backButton.setBounds(100, 100, 80, 60);
        backButton.addMouseListener(backButtonClickListener);
        add(backButton);
    }

    private void requestRoomState() {
        Api api = Api.getInstance();
        api.requestRoomState();
    }

    private void updateView(RoomState state) {
        List<String> userNames = state.getUserNames();
        Optional<String> users = userNames.stream().reduce((prev, next) -> prev + "\n" + next);
        users.ifPresent(userListTextArea::setText);

        startGameButton.setEnabled(userNames.size() >= 2);
    }

    @Override
    protected void onMessageReceived(String message) {
        if (message.equals(Protocol.ERROR)) {
            showToast("서버와의 연결이 끊어졌습니다.");
        }

        if (message.equals(Protocol.GAME_START)) {
            navigateTo(new GameView());
            return;
        }

        try {
            RoomState state = new Gson().fromJson(message, RoomState.class);
            //noinspection ConstantConditions
            if (state.getId() != null) {
                updateView(state);
            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            showToast("잘못된 `RoomState`가 서버로부터 전달되었습니다.");
        }
    }

    private final ActionListener gameStartListener = (event) -> {
        Api.getInstance().startGame();
    };

    private final MouseAdapter backButtonClickListener = new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            Api.getInstance().exitRoom();
            Navigator.navigateTo(RoomView.this, new LobbyView());
        }
    };
}
