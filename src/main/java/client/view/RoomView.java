package client.view;

import client.component.BackgroundSoundToggleButton;
import client.constant.Fonts;
import client.constant.ImageIcons;
import client.core.ApiListenerView;
import client.core.Button;
import client.core.OutlinedLabel;
import client.core.TextField;
import client.service.Api;
import client.service.SoundController;
import client.util.Navigator;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import domain.constant.Protocol;
import domain.model.Sound;
import domain.state.RoomState;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.Optional;

public class RoomView extends ApiListenerView {

    private final JTextArea userListTextArea = new JTextArea();
    private final JTextArea chattingTextArea = new JTextArea();

    private final TextField chattingTextInput = new TextField();
    private final JScrollPane scrollChattingTextArea = new JScrollPane(chattingTextArea);

    private final Button startGameButton = new Button("게임 시작");
    private final JButton sendButton = new JButton("전송");

    private final JLabel backButton = new JLabel(ImageIcons.BACK_BUTTON);

    public RoomView() {
        super(ImageIcons.ROOM_BACKGROUND);

        SoundController.changeLoopIfPlaying(Sound.LOBBY_VIEW_BGM);

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

        BackgroundSoundToggleButton backgroundSoundToggleButton = new BackgroundSoundToggleButton(
                Sound.LOBBY_VIEW_BGM,
                SoundController.isLoopPlaying()
        );
        add(backgroundSoundToggleButton);

        userListTextArea.setEditable(false);
        userListTextArea.setFont(Fonts.H6);
        userListTextArea.setBounds(420, 360, 200, 120);
        add(userListTextArea);

        chattingTextArea.setEditable(false);
        chattingTextArea.setFont(Fonts.BODY1);
        chattingTextArea.setBounds(720, 160, 257, 360);
        chattingTextArea.setLineWrap(true);

        scrollChattingTextArea.setBounds(720, 160, 257, 360);
        add(scrollChattingTextArea);

        chattingTextInput.setBounds(718, 535, 185, 40);
        chattingTextInput.setColumns(10);
        chattingTextInput.setFont(Fonts.BODY1);
        chattingTextInput.getDocument().addDocumentListener(chattingTextFieldChangeListener);
        chattingTextInput.addKeyListener(chattingKeyEventListener);
        add(chattingTextInput);

        sendButton.setBounds(903, 535, 76, 40);
        sendButton.setEnabled(false);
        sendButton.addActionListener(chattingSendListener);
        add(sendButton);

        startGameButton.setEnabled(false);
        startGameButton.setBounds(420, 600, 200, 60);
        startGameButton.addActionListener(gameStartListener);
        add(startGameButton);

        backButton.setBounds(20, 10, 64, 64);
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

    public void appendChattingText(String msg) {
        chattingTextArea.append(msg + "\n");
        chattingTextArea.setCaretPosition(chattingTextArea.getText().length());
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

        if (message.startsWith(Protocol.SEND_MESSAGE)) {
            String[] messageArray = message.split(" ");
            appendChattingText(messageArray[1] + " : " + messageArray[2]);
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

    private void sendChatMessage() {
        Api.getInstance().chat(chattingTextInput.getText());
        chattingTextInput.setText("");
    }

    private final ActionListener gameStartListener = (event) -> Api.getInstance().startGame();

    private final ActionListener chattingSendListener = (event) -> sendChatMessage();

    private final KeyAdapter chattingKeyEventListener = new KeyAdapter() {
        @Override
        public void keyPressed(KeyEvent event) {
            if (event.getKeyCode() == KeyEvent.VK_ENTER) {
                sendChatMessage();
            }
        }
    };

    private final MouseAdapter backButtonClickListener = new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            Api.getInstance().exitRoom();
            Navigator.navigateTo(RoomView.this, new LobbyView());
        }
    };

    private final DocumentListener chattingTextFieldChangeListener = new DocumentListener() {
        private void observeTextArea() {
            sendButton.setEnabled(chattingTextInput.getText().length() > 0);
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            observeTextArea();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            observeTextArea();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            observeTextArea();
        }
    };
}
