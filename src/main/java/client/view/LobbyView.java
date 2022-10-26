package client.view;

import client.base.BaseView;
import client.service.Api;
import client.service.MessageListener;
import client.util.ImageIcons;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import domain.constant.Protocol;
import domain.state.LobbyState;

import javax.swing.*;
import java.util.Optional;

public class LobbyView extends BaseView {

    private final JLabel userListLabel = new JLabel();

    public LobbyView() {
        super(ImageIcons.LOBBY_BACKGROUND);

        add(userListLabel);

        Api.getInstance().addListener(messageListener);
    }

    @Override
    protected void onRemoved() {
        Api.getInstance().removeListener(messageListener);
    }

    private final MessageListener messageListener = message -> {
        if (message.equals(Protocol.ERROR)) {
            showToast("서버와의 연결이 끊어졌습니다.");
        }

        try {
            LobbyState state = new Gson().fromJson(message, LobbyState.class);
            Optional<String> users = state.getUserNames().stream().reduce((user, prev) -> user + "\n");
            users.ifPresent(userListLabel::setText);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            showToast("잘못된 `LobbyState`가 서버로부터 전달되었습니다.");
        }
    };
}
