package domain.model;

public class Player extends GameObject {
	
	private Direction direction;
	
	public Player(int x, int y, Direction direction) {
		super(x, y);
		this.direction = direction;
	}
}
