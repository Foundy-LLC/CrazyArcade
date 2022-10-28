package domain.state;

import java.io.Serializable;
import java.util.List;

import domain.model.*;
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
        WaterWave[][] waterWave2D = map.getWaterWave2D();

        for (int y = 0; y < waterBomb2d.length; ++y) {
            for (int x = 0; x < waterBomb2d[y].length; ++x) {
                WaterBomb waterBomb = waterBomb2d[y][x];
                if (waterBomb == null) {
                    continue;
                }
                if (waterBomb.shouldExplode()) {
                    waterWave2D[y][x] = new WaterWave(null);
                    // TODO: 물줄기 십자가 모양으로 생성하기(함수로 빼서)
                    // TODO: 동시 폭발 구현하기

                    waterBomb2d[y][x] = null;
                }
            }
        }
    }

    public void updateWaterCourseStates() {
        WaterWave[][] waterWave2D = map.getWaterWave2D();

        for (int y = 0; y < waterWave2D.length; ++y) {
            for (int x = 0; x < waterWave2D[y].length; ++x) {
                WaterWave waterWave = waterWave2D[y][x];
                if (waterWave == null) {
                    continue;
                }
                if (waterWave.shouldDisappear()) {
                    waterWave2D[y][x] = null;
                }
            }
        }
    }

    public boolean canInstallWaterBombAt(Offset tileOffset) {
        WaterBomb[][] waterBomb2d = map.getWaterBomb2d();
        WaterBomb waterBomb = waterBomb2d[tileOffset.y][tileOffset.x];
        return waterBomb == null || !waterBomb.isWaiting();
    }

    public void installWaterBomb(WaterBomb waterBomb, Offset tileOffset) {
        WaterBomb[][] waterBomb2d = map.getWaterBomb2d();
        waterBomb2d[tileOffset.y][tileOffset.x] = waterBomb;
    }
}
