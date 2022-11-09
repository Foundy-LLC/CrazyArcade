package client.view;

import client.core.ApiListenerView;
import client.core.Button;
import client.core.OutlinedLabel;
import client.core.TextField;
import client.service.Api;
import client.constant.Fonts;
import client.constant.ImageIcons;
import client.service.SoundController;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import domain.constant.Protocol;
import domain.model.RoomDto;
import domain.model.Sound;
import domain.state.LobbyState;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Vector;

public class LobbyView extends ApiListenerView {

    private final JList<RoomDto> roomList = new JList<>();

    private final TextField roomNameTextField = new TextField();

    private final Button makeRoomButton = new Button("방 만들기");

    public LobbyView() {
        super(ImageIcons.LOBBY_BACKGROUND);

        SoundController.playLoop(Sound.LOBBY_VIEW_BGM);

        initView();
        requestLobbyState();
    }

    private void initView() {
        OutlinedLabel userListTitle = new OutlinedLabel("방 목록", 2);
        userListTitle.setBounds(470, 320, 200, 40);
        userListTitle.setForeground(Color.white);
        userListTitle.setOutlineColor(Color.blue);
        userListTitle.setFont(Fonts.H5.deriveFont(Font.BOLD));
        add(userListTitle);

        roomList.setFont(Fonts.H6);
        roomList.setBounds(420, 360, 200, 120);
        roomList.addMouseListener(listMouseClickAdapter);
        add(roomList);

        roomNameTextField.setPlaceholder("방이름");
        roomNameTextField.setBounds(420, 530, 200, 60);
        roomNameTextField.addCaretListener((e) -> {
            String roomName = roomNameTextField.getText();
            makeRoomButton.setEnabled(!roomName.isEmpty());
        });
        add(roomNameTextField);

        makeRoomButton.setEnabled(false);
        makeRoomButton.setBounds(420, 600, 200, 60);
        makeRoomButton.addActionListener(makeRoomButtonListener);
        add(makeRoomButton);
    }

    private void requestLobbyState() {
        Api api = Api.getInstance();
        api.requestLobbyState();
    }

    private void updateView(LobbyState state) {
        List<RoomDto> roomDtoList = state.getRoomDtoList();
        Vector<RoomDto> vector = new Vector<>(roomDtoList);
        roomList.setListData(vector);
    }

    @Override
    protected void onMessageReceived(String message) {
        switch (message) {
            case Protocol.ERROR -> showToast("서버와의 연결이 끊어졌습니다.");
            case Protocol.MAKE_ROOM, Protocol.JOIN_ROOM -> {
                navigateTo(new RoomView());
                return;
            }
        }

        try {
            LobbyState state = new Gson().fromJson(message, LobbyState.class);
            updateView(state);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            showToast("잘못된 `LobbyState`가 서버로부터 전달되었습니다.");
        }
    }

    private final MouseAdapter listMouseClickAdapter = new MouseAdapter() {
        public void mouseClicked(MouseEvent evt) {
            if (evt.getClickCount() == 2) {
                int index = roomList.locationToIndex(evt.getPoint());
                RoomDto roomDto = roomList.getModel().getElementAt(index);
                Api.getInstance().joinRoom(roomDto.getId());
            }
        }
    };

    private final ActionListener makeRoomButtonListener = (event) -> Api.getInstance().makeRoom(roomNameTextField.getText());
}
