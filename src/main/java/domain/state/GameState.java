package domain.state;

import java.io.Serializable;
import java.util.List;

import domain.model.Map;
import domain.model.Offset;
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
	private Map map;

	@NonNull
	private Integer remainingTimeSec;

	public void updateWaterBombStates() {
		WaterBomb[][] waterBomb2d = map.getWaterBomb2d();
		for (int y = 0; y < waterBomb2d.length; ++y) {
			for (int x = 0; x < waterBomb2d[y].length; ++x) {
				WaterBomb waterBomb = waterBomb2d[y][x];
				if (waterBomb == null) {
					continue;
				}
				waterBomb.updateState();
				if (waterBomb.isDestroyed()) {
					waterBomb2d[y][x] = null;
				}
			}
		}
	}

	public boolean canInstallWaterBombAt(Offset tileOffset) {
		WaterBomb[][] waterBomb2d = map.getWaterBomb2d();
		WaterBomb waterBomb = waterBomb2d[tileOffset.y][tileOffset.x];
		return waterBomb == null || waterBomb.isWaiting();
	}

	public void installWaterBomb(WaterBomb waterBomb, Offset tileOffset) {
		WaterBomb[][] waterBomb2d = map.getWaterBomb2d();
		waterBomb2d[tileOffset.y][tileOffset.x] = waterBomb;
	}
}
