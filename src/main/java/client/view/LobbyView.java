package client.view;

import client.core.ApiListenerView;
import client.core.Button;
import client.core.OutlinedLabel;
import client.service.Api;
import client.util.Fonts;
import client.util.ImageIcons;
import domain.constant.Protocol;
import domain.state.LobbyState;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Optional;

public class LobbyView extends ApiListenerView {

    private final JTextArea userListTextArea = new JTextArea();

    private final Button startGameButton = new Button("게임 시작");

    public LobbyView() {
        super(ImageIcons.LOBBY_BACKGROUND);
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

        userListTextArea.setEditable(false);
        userListTextArea.setFont(Fonts.H6);
        userListTextArea.setBounds(420, 360, 200, 120);
        add(userListTextArea);

        startGameButton.setEnabled(false);
        startGameButton.setBounds(420, 600, 200, 60);
        startGameButton.addActionListener(gameStartListener);
        add(startGameButton);
    }

    private void requestLobbyState() {
        Api api = Api.getInstance();
        api.requestLobbyState();
    }

    private void updateView(LobbyState state) {
        List<String> userNames = state.getUserNames();
        Optional<String> users = userNames.stream().reduce((prev, next) -> prev + "\n" + next);
        users.ifPresent(userListTextArea::setText);

        startGameButton.setEnabled(userNames.size() >= 2);
    }

    @Override
    protected void onMessageReceived(Object object) {
        if (object instanceof String) {
            String message = (String) object;

            if (message.equals(Protocol.ERROR)) {
                showToast("서버와의 연결이 끊어졌습니다.");
            }

            if (message.startsWith("/startGame")) {
                navigateTo(new GameView());
            }
            return;
        }

        if (object instanceof LobbyState) {
            updateView((LobbyState) object);
        }
    }

    private final ActionListener gameStartListener = (event) -> {
        Api.getInstance().startGame();
    };
}
