package domain.model;

import domain.constant.Sizes;
import lombok.Getter;
import lombok.NonNull;

import java.io.Serializable;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Map implements Serializable {

    @NonNull
    private final Offset[] startingPoints;

    @Getter
    @NonNull
    private final Tile[][] tile2d;

    @Getter
    @NonNull
    private final Block[][] block2D;

    @Getter
    @NonNull
    private final WaterBomb[][] waterBomb2d;

    @Getter
    @NonNull
    private final WaterWave[][] waterWave2d;

    public Map(Offset[] startingPoints, Tile[][] tile2d, Block[][] block2D, WaterBomb[][] waterBomb2d, WaterWave[][] waterWave2d) {
        assert (startingPoints.length == 8);
        assert (tile2d.length == Sizes.TILE_COLUMN_COUNT);
        assert (tile2d[0].length == Sizes.TILE_ROW_COUNT);
        assert (block2D.length == Sizes.TILE_COLUMN_COUNT);
        assert (block2D[0].length == Sizes.TILE_ROW_COUNT);
        assert (waterBomb2d.length == Sizes.TILE_COLUMN_COUNT);
        assert (waterBomb2d[0].length == Sizes.TILE_ROW_COUNT);
        assert (waterWave2d.length == Sizes.TILE_COLUMN_COUNT);
        assert (waterWave2d[0].length == Sizes.TILE_ROW_COUNT);

        this.startingPoints = startingPoints;
        this.tile2d = tile2d;
        this.block2D = block2D;
        this.waterBomb2d = waterBomb2d;
        this.waterWave2d = waterWave2d;
    }

    public Offset[] getShuffledStartingPoints() {
        shuffleArray(startingPoints);
        return startingPoints;
    }

    static void shuffleArray(Offset[] arr) {
        Random rnd = ThreadLocalRandom.current();
        for (int i = arr.length - 1; i > 0; i--) {
            int index = rnd.nextInt(i + 1);
            Offset temp = arr[index];
            arr[index] = arr[i];
            arr[i] = temp;
        }
    }
}
