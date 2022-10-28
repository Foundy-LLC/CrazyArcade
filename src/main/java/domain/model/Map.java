package domain.model;

import domain.constant.Sizes;
import lombok.Getter;
import lombok.NonNull;

import java.io.Serializable;

@Getter
public class Map implements Serializable {

    @NonNull
    private final Block[][] block2D;

    public Map(Block[][] block2D) {
        assert(block2D.length == Sizes.TILE_COLUMN_COUNT);
        assert(block2D[0].length == Sizes.TILE_ROW_COUNT);

        this.block2D = block2D;
    }
}
