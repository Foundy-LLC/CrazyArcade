package domain.state;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

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

    @Getter
    private final List<Sound> shouldBePlayedSounds = new LinkedList<>();

    public boolean updateState() {
        boolean didUpdate;

        didUpdate = updateBlocksState();
        didUpdate = updateWaterBombsState() || didUpdate;
        didUpdate = updateWaterWavesState() || didUpdate;
        didUpdate = updatePlayersState() || didUpdate;

        if (isGameEnded()) {
            isEnded = true;
        }
        return didUpdate;
    }

    public void exitPlayer(String userName) {
        Optional<Player> playerOptional = players.stream().filter((player -> player.getName().equals(userName))).findFirst();
        if (playerOptional.isPresent()) {
            Player player = playerOptional.get();
            players.remove(player);
        }
    }

    public void didPlaySounds(List<Sound> sounds) {
        shouldBePlayedSounds.removeAll(sounds);
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

    private boolean updateBlocksState() {
        Block[][] block2d = map.getBlock2d();
        Item[][] item2d = map.getItem2d();
        boolean didUpdate = false;

        for (int y = 0; y < block2d.length; ++y) {
            for (int x = 0; x < block2d[y].length; ++x) {
                if (block2d[y][x] == null) {
                    continue;
                }
                if (block2d[y][x].shouldDisappear()) {
                    item2d[y][x] = block2d[y][x].createItem();
                    block2d[y][x] = null;
                    didUpdate = true;
                }
            }
        }
        return didUpdate;
    }

    private boolean updateWaterBombsState() {
        WaterBomb[][] waterBomb2d = map.getWaterBomb2d();
        boolean didExplode = false;

        for (int y = 0; y < waterBomb2d.length; ++y) {
            for (int x = 0; x < waterBomb2d[y].length; ++x) {
                WaterBomb waterBomb = waterBomb2d[y][x];
                if (waterBomb == null) {
                    continue;
                }
                if (waterBomb.shouldExplode()) {
                    doChainExplode(y, x);
                    didExplode = true;
                }
            }
        }

        if (didExplode) {
            shouldBePlayedSounds.add(Sound.WATER_WAVE);
        }
        return didExplode;
    }

    /**
     * [y, x] ????????? ???????????? ???????????? ?????????????????? ???????????? ???????????? ?????? ????????????.
     */
    private void doChainExplode(int y, int x) {
        WaterBomb[][] waterBomb2d = map.getWaterBomb2d();
        Block[][] block2d = map.getBlock2d();
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
     * ????????? ????????? ???????????? ????????????.
     * <p>
     * ????????? ????????? ?????? ????????? ???????????? ?????? ?????????. ???????????? ???????????? ????????? ????????????.
     *
     * @param y      ???????????? ?????? y??? ??????
     * @param x      ???????????? ?????? x??? ??????
     * @param length ???????????? ??????
     */
    private void createWaterWave(int y, int x, int length) {
        WaterWave[][] waterWave2d = map.getWaterWave2d();
        Block[][] block2d = map.getBlock2d();
        Item[][] item2d = map.getItem2d();

        waterWave2d[y][x] = new WaterWave(null, false);
        item2d[y][x] = null;

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

                item2d[ny][nx] = null;
                waterWave2d[ny][nx] = new WaterWave(DIRECTIONS[dir], i == length);
            }
        }
    }

    private boolean isOutOfRange(int y, int x) {
        return 0 > x || x >= Sizes.TILE_ROW_COUNT || 0 > y || y >= Sizes.TILE_COLUMN_COUNT;
    }

    private boolean updateWaterWavesState() {
        WaterWave[][] waterWave2d = map.getWaterWave2d();
        boolean didUpdate = false;

        for (int y = 0; y < waterWave2d.length; ++y) {
            for (int x = 0; x < waterWave2d[y].length; ++x) {
                WaterWave waterWave = waterWave2d[y][x];
                if (waterWave == null) {
                    continue;
                }
                if (waterWave.shouldDisappear()) {
                    waterWave2d[y][x] = null;
                    didUpdate = true;
                }
            }
        }
        return didUpdate;
    }

    private boolean updatePlayersState() {
        Item[][] item2d = map.getItem2d();
        boolean didUpdate = false;

        for (int i = 0; i < players.size(); ++i) {
            Player player = players.get(i);

            if (player.isAlive()) {
                for (var otherPlayer : players) {
                    if (otherPlayer != player &&
                            otherPlayer.isTrapped() &&
                            player.distance(otherPlayer) <= KILL_DISTANCE
                    ) {
                        otherPlayer.die();
                        shouldBePlayedSounds.add(Sound.PLAYER_DIE);
                        didUpdate = true;
                    }
                }

                Offset centerOffset = player.getCenterTileOffset();
                Item item = item2d[centerOffset.y][centerOffset.x];
                if (item != null) {
                    player.collectItem(item);
                    shouldBePlayedSounds.add(Sound.EAT_ITEM);
                    item2d[centerOffset.y][centerOffset.x] = null;
                    didUpdate = true;
                }
            }

            Pair<Offset> feetOffset = player.getFeetTileOffset();
            if (!player.isTrapped() && isOnWave(feetOffset)) {
                shouldBePlayedSounds.add(Sound.PLAYER_TRAP);
                player.trapIntoWaterWave();
                didUpdate = true;
            }
            if (player.shouldBeRemoved()) {
                players.remove(player);
                --i;
                didUpdate = true;
            }
        }
        return didUpdate;
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

    public int countPlayerWaterBombs(Player player) {
        WaterBomb[][] waterBomb2d = map.getWaterBomb2d();
        int result = 0;
        for (var waterBombs : waterBomb2d) {
            for (var waterBomb : waterBombs) {
                if (waterBomb != null && player == waterBomb.getInstaller()) {
                    result++;
                }
            }
        }
        return result;
    }
}
