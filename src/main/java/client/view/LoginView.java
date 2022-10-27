package client.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import client.component.BaseView;
import client.component.Button;
import client.component.TextField;
import client.service.Api;
import client.util.ImageIcons;

public class LoginView extends BaseView {

	private static final long serialVersionUID = 1289572712792973518L;
	
	private final TextField userNameTextField;
	private final TextField ipTextField;
	private final TextField portTextField;
	
	public LoginView() {
		super(ImageIcons.LOGIN_BACKGROUND);
		
		this.userNameTextField = new TextField();
		userNameTextField.setPlaceholder("닉네임");
		userNameTextField.setBounds(420, 360, 200, 60);
		add(userNameTextField);
		
		this.ipTextField = new TextField("127.0.0.1");
		ipTextField.setPlaceholder("주소");
		ipTextField.setBounds(420, 440, 200, 60);
		add(ipTextField);
		
		this.portTextField = new TextField("30000");
		portTextField.setPlaceholder("포트 번호");
		portTextField.setBounds(420, 520, 200, 60);
		add(portTextField);

		Button loginButton = new Button("로그인");
		loginButton.setBounds(420, 600, 200, 60);
		loginButton.addActionListener(new LoginActionListener());
		add(loginButton);
	}
	
	private class LoginActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			String userName = userNameTextField.getText().trim();
			String ipAddress = ipTextField.getText().trim();
			String portNumber = portTextField.getText().trim();
			
			if (userName.isEmpty()) {
				showToast("이름을 입력해주세요.");
				return;
			}
			if (ipAddress.isEmpty()) {
				showToast("IP 주소를 입력해주세요.");
				return;
			}
			if (portNumber.isEmpty()) {
				showToast("포트 번호를 입력해주세요.");
				return;
			}	
			
			Api api = Api.getInstance();
			try {
				api.init(ipAddress, portNumber);
			} catch (NumberFormatException e1) {
				e1.printStackTrace();
				showToast("포트 번호는 숫자를 입력해주세요.");
				return;
			} catch (IOException e1) {
				e1.printStackTrace();
				showToast("연결에 실패했습니다.");
				return;
			}
			
			api.login(userName);
			navigateTo(new LobbyView());
		}
	}
}
