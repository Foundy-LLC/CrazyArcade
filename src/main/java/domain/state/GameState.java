package domain.state;

import java.io.Serializable;
import java.util.List;

import domain.constant.Sizes;
import domain.core.Pair;
import domain.model.*;
import lombok.*;

@Getter
@ToString
@Builder
public class GameState implements Serializable {

    private static final long serialVersionUID = 6601648199897535737L;

    private static final Direction[] DIRECTIONS = Direction.values();

    public static final double KILL_DISTANCE = 20;

    @NonNull
    private List<Player> players;

    @NonNull
    private Map map;

    @NonNull
    private Integer remainingTimeSec;

    @Getter
    private boolean isEnded;

    public void updateState() {
        updateBlocksState();
        updateWaterBombsState();
        updateWaterWavesState();
        updatePlayersState();

        if (isGameEnded()) {
            isEnded = true;
        }
    }

    private boolean isGameEnded() {
        return players.size() <= 1;
    }

    public Player getWinner() {
        if (!isEnded) {
            throw new IllegalStateException();
        }
        return players.isEmpty() ? null : players.get(0);
    }

    private void updateBlocksState() {
        Block[][] block2d = map.getBlock2D();

        for (int y = 0; y < block2d.length; ++y) {
            for (int x = 0; x < block2d[y].length; ++x) {
                if (block2d[y][x] == null) {
                    continue;
                }
                if (block2d[y][x].shouldDisappear()) {
                    block2d[y][x] = null;
                }
            }
        }
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

                if (isOutOfRange(ny, nx)) {
                    break;
                }
                if (block2d[ny][nx] != null) {
                    block2d[ny][nx].collideWithWaterWave();
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
        for (int i = 0; i < players.size(); ++i) {
            Player player = players.get(i);

            if (player.isAlive()) {
                for (var otherPlayer : players) {
                    if (otherPlayer != player &&
                            otherPlayer.isTrapped() &&
                            player.distance(otherPlayer) <= KILL_DISTANCE
                    ) {
                        otherPlayer.die();
                    }
                }
            }

            Pair<Offset> feetOffset = player.getFeetTileOffset();
            if (!player.isTrapped() && isOnWave(feetOffset)) {
                player.trapIntoWaterWave();
            }
            if (player.shouldBeRemoved()) {
                players.remove(player);
                --i;
            }
        }
    }

    private boolean isOnWave(Pair<Offset> feetOffset) {
        return isOnWave(feetOffset.getFirst()) && isOnWave(feetOffset.getSecond());
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

    public void removeTerminatedPlayer(Player player) {
        players.remove(player);
    }
}
