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

    private static final Direction[] DIRECTIONS = Direction.values();

    @NonNull
    private List<Player> players;

    @NonNull
    private Map map;

    @NonNull
    private Integer remainingTimeSec;

    public void updateState() {
        updateWaterBombsState();
        updateWaterWavesState();
        updatePlayersState();
    }

    private void updateWaterBombsState() {
        WaterBomb[][] waterBomb2d = map.getWaterBomb2d();

        for (int y = 0; y < waterBomb2d.length; ++y) {
            for (int x = 0; x < waterBomb2d[y].length; ++x) {
                WaterBomb waterBomb = waterBomb2d[y][x];
                if (waterBomb == null) {
                    continue;
                }
                if (waterBomb.shouldExplode()) {
                    doChainExplode(y, x);
                }
            }
        }
    }

    /**
     * [y, x] 좌표의 물폭탄을 기점으로 사정거리내에 존재하는 물폭탄을 연쇄 폭발한다.
     */
    private void doChainExplode(int y, int x) {
        WaterBomb[][] waterBomb2d = map.getWaterBomb2d();
        Block[][] block2d = map.getBlock2D();
        WaterBomb waterBomb = waterBomb2d[y][x];

        if (waterBomb == null) {
            return;
        }
        int length = waterBomb.getLength();

        waterBomb2d[y][x] = null;
        createWaterWave(y, x, length);

        for (int dir = 0; dir < DIRECTIONS.length; ++dir) {
            for (int i = 1; i <= length; ++i) {
                int ny = y + i * Direction.DIR[dir][0];
                int nx = x + i * Direction.DIR[dir][1];

                if (isOutOfRange(ny, nx) || block2d[ny][nx] != null) {
                    break;
                }
                if (waterBomb2d[ny][nx] != null) {
                    doChainExplode(ny, nx);
                }
            }
        }
    }

    /**
     * 십자가 모양의 물줄기를 생성한다.
     *
     * @param y      물줄기의 중앙 y값 좌표
     * @param x      물줄기의 중앙 x값 좌표
     * @param length 물줄기의 길이
     */
    private void createWaterWave(int y, int x, int length) {
        WaterWave[][] waterWave2d = map.getWaterWave2d();
        Block[][] block2d = map.getBlock2D();

        waterWave2d[y][x] = new WaterWave(null, false);
        for (int dir = 0; dir < DIRECTIONS.length; ++dir) {
            for (int i = 1; i <= length; i++) {
                int ny = y + i * Direction.DIR[dir][0];
                int nx = x + i * Direction.DIR[dir][1];

                if (isOutOfRange(ny, nx) || block2d[ny][nx] != null) {
                    break;
                }
                waterWave2d[ny][nx] = new WaterWave(DIRECTIONS[dir], i == length);
            }
        }
    }

    private boolean isOutOfRange(int y, int x) {
        return 0 > x || x >= Sizes.TILE_ROW_COUNT || 0 > y || y >= Sizes.TILE_COLUMN_COUNT;
    }

    private void updateWaterWavesState() {
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

    private void updatePlayersState() {
        players.forEach((player) -> {
            Offset tileOffset = player.getCenterTileOffset();

            if (!player.isTrapped() && isOnWave(tileOffset)) {
                player.trapIntoWaterWave();
            }
            if (player.isDead()) {
                // TODO implements
            }
        });
    }

    private boolean isOnWave(Offset tileOffset) {
        WaterWave[][] waterWave2d = map.getWaterWave2d();
        return waterWave2d[tileOffset.y][tileOffset.x] != null;
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
