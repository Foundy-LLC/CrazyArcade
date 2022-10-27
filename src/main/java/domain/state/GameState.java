package domain.state;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import domain.model.Player;
import domain.model.Wall;
import domain.model.WaterBomb;
import lombok.*;

import static java.util.Collections.emptyList;

@Getter
@ToString
@Builder
public class GameState implements Serializable {
	
	private static final long serialVersionUID = 6601648199897535737L;

	@NonNull
	private List<Player> players;

	@NonNull
	private List<WaterBomb> waterBombs;

	@NonNull
	private List<Wall> walls;

	@NonNull
	private Integer remainingTimeSec;

	public static GameState getInitState(List<Player> players) {
		return new GameStateBuilder()
				.players(players)
				.waterBombs(emptyList())
				.walls(emptyList())
				.remainingTimeSec(60 * 3)
				.build();
	}
}
