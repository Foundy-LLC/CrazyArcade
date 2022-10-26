package client.view;

import client.base.BaseView;
import client.component.Button;
import client.component.OutlinedLabel;
import client.service.Api;
import client.service.MessageListener;
import client.util.Fonts;
import client.util.ImageIcons;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import domain.constant.Protocol;
import domain.state.LobbyState;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Optional;

public class LobbyView extends BaseView {

    private final JTextArea userListTextArea = new JTextArea();

    public LobbyView() {
        super(ImageIcons.LOBBY_BACKGROUND);
        initView();
        initListener();
    }

    private void initView() {
        OutlinedLabel userListTitle = new OutlinedLabel("대기 인원", 2);
        userListTitle.setBounds(470, 320, 200, 40);
        userListTitle.setForeground(Color.white);
        userListTitle.setOutlineColor(Color.blue);
        userListTitle.setFont(Fonts.H5.deriveFont(Font.BOLD));
        add(userListTitle);

        userListTextArea.setEditable(false);
        userListTextArea.setFont(Fonts.BODY1);
        userListTextArea.setBounds(420, 360, 200, 120);
        add(userListTextArea);

        Button startGameButton = new Button("게임 시작");
        startGameButton.setBounds(420, 600, 200, 60);
        startGameButton.addActionListener(gameStartListener);
        add(startGameButton);
    }

    private void initListener() {
        Api.getInstance().addListener(messageListener);
    }

    @Override
    protected void onRemoved() {
        Api.getInstance().removeListener(messageListener);
    }

    private final ActionListener gameStartListener = (event) -> {
        navigateTo(new GameView());
    };

    private final MessageListener messageListener = message -> {
        if (message.equals(Protocol.ERROR)) {
            showToast("서버와의 연결이 끊어졌습니다.");
        }

        try {
            LobbyState state = new Gson().fromJson(message, LobbyState.class);
            Optional<String> users = state.getUserNames().stream().reduce((user, prev) -> user + "\n");
            users.ifPresent(userListTextArea::setText);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            showToast("잘못된 `LobbyState`가 서버로부터 전달되었습니다.");
        }
    };
}
