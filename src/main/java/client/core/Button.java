package client.core;

import java.awt.Color;
import javax.swing.JButton;

import client.constant.Fonts;

public class Button extends JButton {

	private static final long serialVersionUID = 3015155307153314729L;

	public Button(String label) {
		super(label);
		
		setForeground(Color.blue);
		setFont(Fonts.BUTTON);
		setOpaque(false);
		addSoundPlayingToActionListener();
	}
	
	private void addSoundPlayingToActionListener() {
		// TODO: 구현 
//		addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				SoundController.play(Sounds.BUTTON_CLICK);
//			}
//		});
	}
}
