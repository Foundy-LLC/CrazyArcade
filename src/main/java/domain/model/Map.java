package domain.model;

import domain.constant.Sizes;
import lombok.Getter;
import lombok.NonNull;

import java.io.Serializable;

@Getter
public class Map implements Serializable {

    @NonNull
    private final Block[][] block2D;

    @NonNull
    private final WaterBomb[][] waterBomb2d;

    @NonNull
    private final WaterCourse[][] waterCourse2d;

    public Map(Block[][] block2D, WaterBomb[][] waterBomb2d, WaterCourse[][] waterCourse2d) {
        assert(block2D.length == Sizes.TILE_COLUMN_COUNT);
        assert(block2D[0].length == Sizes.TILE_ROW_COUNT);
        assert(waterBomb2d.length == Sizes.TILE_COLUMN_COUNT);
        assert(waterBomb2d[0].length == Sizes.TILE_ROW_COUNT);
        assert(waterCourse2d.length == Sizes.TILE_COLUMN_COUNT);
        assert(waterCourse2d[0].length == Sizes.TILE_ROW_COUNT);

        this.block2D = block2D;
        this.waterBomb2d = waterBomb2d;
        this.waterCourse2d = waterCourse2d;
    }
}
