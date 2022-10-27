package domain.model;

public class Player extends GameObject {
	
	private Direction direction;
	
	public Player(int x, int y) {
		super(x, y);
		this.direction = Direction.DOWN;
	}
}
