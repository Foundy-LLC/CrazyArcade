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
        WaterCourse[][] waterCourse2d = map.getWaterCourse2d();

        for (int y = 0; y < waterBomb2d.length; ++y) {
            for (int x = 0; x < waterBomb2d[y].length; ++x) {
                WaterBomb waterBomb = waterBomb2d[y][x];
                if (waterBomb == null) {
                    continue;
                }
                if (waterBomb.shouldExplode()) {
                    waterCourse2d[y][x] = new WaterCourse(null);
                    // TODO: 물줄기 십자가 모양으로 생성하기(함수로 빼서)
                    // TODO: 동시 폭발 구현하기

                    waterBomb2d[y][x] = null;
                }
            }
        }
    }

    public void updateWaterCourseStates() {
        WaterCourse[][] waterCourse2d = map.getWaterCourse2d();

        for (int y = 0; y < waterCourse2d.length; ++y) {
            for (int x = 0; x < waterCourse2d[y].length; ++x) {
                WaterCourse waterCourse = waterCourse2d[y][x];
                if (waterCourse == null) {
                    continue;
                }
                if (waterCourse.shouldDisappear()) {
                    waterCourse2d[y][x] = null;
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
