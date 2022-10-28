package domain.state;

import java.io.Serializable;
import java.util.List;

import domain.constant.Sizes;
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
        WaterWave[][] waterWave2d = map.getWaterWave2d();

        for (int y = 0; y < waterBomb2d.length; ++y) {
            for (int x = 0; x < waterBomb2d[y].length; ++x) {
                WaterBomb waterBomb = waterBomb2d[y][x];
                if (waterBomb == null) {
                    continue;
                }
                if (waterBomb.shouldExplode()) {
                    createWaterCourse(waterBomb, waterWave2d, x, y);
                    // TODO: 동시 폭발 구현하기

                    waterBomb2d[y][x] = null;
                }
            }
        }
    }

    private void createWaterCourse(WaterBomb waterBomb, WaterWave[][] waterWave2d, int x, int y) {
        int waterBombLength = waterBomb.getLength();
        waterWave2d[y][x] = new WaterWave(null, false);
        for (int i = 1; i <= waterBombLength; i++) {
            boolean isEnd = i == waterBombLength;
            if (inRange(y + i, x)) {
                waterWave2d[y + i][x] = new WaterWave(Direction.DOWN, isEnd);
            }
            if (inRange(y - i, x)) {
                waterWave2d[y - i][x] = new WaterWave(Direction.UP, isEnd);
            }
            if (inRange(y, x + i)) {
                waterWave2d[y][x + i] = new WaterWave(Direction.RIGHT, isEnd);
            }
            if (inRange(y, x - i)) {
                waterWave2d[y][x - i] = new WaterWave(Direction.LEFT, isEnd);
            }
        }
    }

    private boolean inRange(int y, int x) {
        return 0 <= x && x < Sizes.TILE_ROW_COUNT && 0 <= y && y < Sizes.TILE_COLUMN_COUNT;
    }

    public void updateWaterCourseStates() {
        WaterWave[][] waterWave2d = map.getWaterWave2d();

        for (int y = 0; y < waterWave2d.length; ++y) {
            for (int x = 0; x < waterWave2d[y].length; ++x) {
                WaterWave waterWave = waterWave2d[y][x];
                if (waterWave == null) {
                    continue;
                }
                if (waterWave.shouldDisappear()) {
                    waterWave2d[y][x] = null;
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
