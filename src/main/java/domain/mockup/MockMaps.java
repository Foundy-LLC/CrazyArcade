package domain.mockup;

import domain.constant.Sizes;
import domain.model.Map;
import domain.model.Block;
import domain.model.WaterBomb;
import domain.model.WaterCourse;

public class MockMaps {
    private static final Block[][] WALLS_1 = {
            {null, null, null, null, null, null, null, null, null, null, null, null, null, null, new Block()},
            {null, null, null, null, null, null, new Block(), null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, new Block(), null, new Block(), null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, new Block(), null, new Block(), null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
            {new Block(), null, null, null, null, null, null, null, null, null, null, null, null, null, new Block()},
    };

    private static final WaterBomb[][] WATER_BOMBS_1 = new WaterBomb[Sizes.TILE_COLUMN_COUNT][Sizes.TILE_ROW_COUNT];

    private static final WaterCourse[][] WATER_COURSES = new WaterCourse[Sizes.TILE_COLUMN_COUNT][Sizes.TILE_ROW_COUNT];

    public static final Map map1 = new Map(WALLS_1, WATER_BOMBS_1, WATER_COURSES);
}
