package domain.model;

import lombok.Getter;
import lombok.NonNull;

public class Player extends GameObject {

	@NonNull
	@Getter
	private final String name;

	@NonNull
	private Direction direction;
	
	public Player(String name, int x, int y) {
		super(x, y);
		this.name = name;
		this.direction = Direction.DOWN;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	}
}
