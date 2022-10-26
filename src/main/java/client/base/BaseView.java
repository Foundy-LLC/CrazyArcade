package client.base;

import client.util.Navigator;
import client.util.Toast;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class BaseView extends JPanel {

	private static final long serialVersionUID = -2782978815613046608L;
	
	private final Image backgroundImage;
	
	public BaseView(ImageIcon backgroundImageIcon) {
		this.backgroundImage = backgroundImageIcon.getImage();
		setLayout(null);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
	    super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 0, null);
        setOpaque(false);
	}

	protected void onRemoved() {}

	protected void navigateTo(JPanel panel) {
		Navigator.navigateTo(this, panel);
		onRemoved();
	}

	protected void showToast(String message) {
		Toast.show(message, this);
	}
}
