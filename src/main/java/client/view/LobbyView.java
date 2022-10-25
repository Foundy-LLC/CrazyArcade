package client.view;

import client.base.BaseView;
import client.service.Api;
import client.service.MessageListener;
import client.util.ImageIcons;
import domain.constant.Protocol;

public class LobbyView extends BaseView {

	public LobbyView() {
		super(ImageIcons.LOBBY_BACKGROUND);

		Api.getInstance().addListener(messageListener);
	}

	private final MessageListener messageListener = message -> {
		if (message.equals(Protocol.ERROR)) {
			showToast("서버와의 연결이 끊어졌습니다.");
		}
	};
}
