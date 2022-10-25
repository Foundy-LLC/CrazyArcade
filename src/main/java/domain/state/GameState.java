package domain.state;

import java.io.Serializable;
import java.util.List;

import domain.model.Player;
import domain.model.Wall;
import domain.model.WaterBomb;
import lombok.Data;

@Data
public class GameState implements Serializable {
	
	private static final long serialVersionUID = 6601648199897535737L;
	
	private List<Player> players;
	private List<WaterBomb> waterBombs;
	private List<Wall> walls;
	private int remainingTimeSec;
}
