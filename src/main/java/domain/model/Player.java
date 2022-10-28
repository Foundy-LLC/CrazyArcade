package domain.model;

import domain.constant.Sizes;
import lombok.Getter;
import lombok.NonNull;

public class Player extends GameObject {

    @NonNull
    @Getter
    private final String name;

    @NonNull
    @Getter
    private Direction direction;

    public Player(String name, int x, int y) {
        super(x, y);
        this.name = name;
        this.direction = Direction.DOWN;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public void move(Direction direction) {
        Offset newOffset = switch (direction) {
            case UP -> new Offset(offset.x, offset.y - SPEED);
            case DOWN -> new Offset(offset.x, offset.y + SPEED);
            case LEFT -> new Offset(offset.x - SPEED, offset.y);
            case RIGHT -> new Offset(offset.x + SPEED, offset.y);
        };
        if (isInRange(newOffset)) {
            offset = newOffset;
        }
    }

    private boolean isInRange(Offset offset) {
        final int maxX = Sizes.MAP_WIDTH - Sizes.TILE_SIZE.width;
        final int maxY = Sizes.MAP_HEIGHT - Sizes.TILE_SIZE.height;

        return 0 <= offset.x && 0 <= offset.y &&
                offset.x < maxX && offset.y < maxY;
    }
}
