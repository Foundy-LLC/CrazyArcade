package domain.state;

import java.io.Serializable;
import java.util.List;

import domain.model.Map;
import domain.model.Player;
import domain.model.WaterBomb;
import lombok.*;

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
	private Map map;

	@NonNull
	private Integer remainingTimeSec;
}
