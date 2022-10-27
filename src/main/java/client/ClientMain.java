package client;

import javax.swing.JFrame;

import client.view.LobbyView;
import client.view.LoginView;
import domain.constant.Sizes;

public class ClientMain extends JFrame {

	private static final long serialVersionUID = 7614529197166228473L;

	public static void main(String[] args) {
		new ClientMain();
	}
	
	private ClientMain() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(Sizes.SCREEN_WIDTH, Sizes.SCREEN_HEIGHT);
		getContentPane().add(new LoginView());
		setResizable(false);
		setVisible(true);
	}
}
