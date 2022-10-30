package client.core;

import client.util.Navigator;
import client.util.Toast;
import lombok.NonNull;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.*;

public class BaseView extends JPanel {

	private static final long serialVersionUID = -2782978815613046608L;

	@NonNull
	private final Image backgroundImageIcon;

	public BaseView(ImageIcon backgroundImageIcon) {
		this.backgroundImageIcon = backgroundImageIcon.getImage();
		setLayout(null);
	}
	
	protected void onDestroyed() {}

	protected void navigateTo(JComponent panel) {
		onDestroyed();
		Navigator.navigateTo(this, panel);
	}

	protected void showToast(String message) {
		Toast.show(message, this);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(backgroundImageIcon, 0, 0, null);
		setOpaque(false);
	}
}
