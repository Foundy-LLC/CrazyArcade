package client.view;

import client.core.ApiListenerView;
import client.core.Button;
import client.core.OutlinedLabel;
import client.service.Api;
import client.constant.Fonts;
import client.constant.ImageIcons;
import client.service.SoundController;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import domain.constant.Protocol;
import domain.model.Sound;
import domain.state.LobbyState;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Optional;

public class LobbyView extends ApiListenerView {

    private final JTextArea roomListTextArea = new JTextArea();

    private final Button joinRoomButton = new Button("방 참여");

    public LobbyView() {
        super(ImageIcons.LOBBY_BACKGROUND);

        SoundController.playLoop(Sound.LOBBY_VIEW_BGM);

        initView();
        requestLobbyState();
    }

    private void initView() {
        OutlinedLabel userListTitle = new OutlinedLabel("대기 인원", 2);
        userListTitle.setBounds(470, 320, 200, 40);
        userListTitle.setForeground(Color.white);
        userListTitle.setOutlineColor(Color.blue);
        userListTitle.setFont(Fonts.H5.deriveFont(Font.BOLD));
        add(userListTitle);

        roomListTextArea.setEditable(false);
        roomListTextArea.setFont(Fonts.H6);
        roomListTextArea.setBounds(420, 360, 200, 120);
        add(roomListTextArea);

        joinRoomButton.setEnabled(false);
        joinRoomButton.setBounds(420, 600, 200, 60);
        joinRoomButton.addActionListener(gameStartListener);
        add(joinRoomButton);
    }

    private void requestLobbyState() {
        Api api = Api.getInstance();
        api.requestLobbyState();
    }

    private void updateView(LobbyState state) {
        List<String> userNames = state.getUserNames();
        Optional<String> users = userNames.stream().reduce((prev, next) -> prev + "\n" + next);
        users.ifPresent(roomListTextArea::setText);

        joinRoomButton.setEnabled(userNames.size() >= 2);
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
            LobbyState state = new Gson().fromJson(message, LobbyState.class);
            updateView(state);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            showToast("잘못된 `LobbyState`가 서버로부터 전달되었습니다.");
        }
    }

    private final ActionListener gameStartListener = (event) -> {
        Api.getInstance().startGame();
    };
}
