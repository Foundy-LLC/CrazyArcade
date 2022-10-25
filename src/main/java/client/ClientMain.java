package client;

import javax.swing.JFrame;

import client.view.LoginView;
import model.Constants;

public class ClientMain extends JFrame {

	private static final long serialVersionUID = 7614529197166228473L;

	public static void main(String[] args) {
		new ClientMain();
	}
	
	private ClientMain() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
		getContentPane().add(new LoginView());
		setResizable(false);
		setVisible(true);
	}
}
