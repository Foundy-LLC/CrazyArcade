package domain.model;

import java.io.Serializable;

public class Offset implements Serializable {
	public final int x;
	public final int y;

	public Offset(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public boolean equals(Offset other) {
		return this.x == other.x && this.y == other.y;
	}
}
