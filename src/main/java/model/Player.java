package model;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class Player extends GameObject {
	
	private Direction direction;
	
	public Player(int x, int y, JLabel imageLabel) {
		super(x, y, imageLabel);
	}
}
