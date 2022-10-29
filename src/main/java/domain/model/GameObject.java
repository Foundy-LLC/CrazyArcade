package domain.model;

import domain.constant.Sizes;
import lombok.Getter;
import lombok.NonNull;

import java.io.Serializable;

public abstract class GameObject implements Serializable {

    public static final int WIDTH = Sizes.MAP_WIDTH / Sizes.TILE_ROW_COUNT;
    public static final int HEIGHT = Sizes.MAP_HEIGHT / Sizes.TILE_COLUMN_COUNT;

    @Getter
    @NonNull
    protected Offset offset;

    public GameObject(int x, int y) {
        this.offset = new Offset(x, y);
    }
}
