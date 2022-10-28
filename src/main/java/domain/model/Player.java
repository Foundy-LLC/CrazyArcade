package domain.model;

import domain.constant.Sizes;
import lombok.Getter;
import lombok.NonNull;

import java.awt.Rectangle;
import java.util.List;

public class Player extends GameObject {

    private static final int COLLIDE_TOLERANCES = 10;

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

    public void move(Direction direction, Map map) {
        Offset newOffset = switch (direction) {
            case UP -> new Offset(offset.x, offset.y - SPEED);
            case DOWN -> new Offset(offset.x, offset.y + SPEED);
            case LEFT -> new Offset(offset.x - SPEED, offset.y);
            case RIGHT -> new Offset(offset.x + SPEED, offset.y);
        };

        if (isInRange(newOffset)) {
            Rectangle newRectangle = getPlayerRectangleAt(newOffset);
            Block[][] block2d = map.getBlock2D();

            Offset leftTopTile = getTileOffsetBy(new Offset(newRectangle.x, newRectangle.y));
            Offset leftBottomTile = getTileOffsetBy(new Offset(newRectangle.x, newRectangle.y + newRectangle.height));
            Offset rightBottomTile = getTileOffsetBy(new Offset(newRectangle.x + newRectangle.width, newRectangle.y + newRectangle.height));
            Offset rightTopTile = getTileOffsetBy(new Offset(newRectangle.x + newRectangle.width, newRectangle.y));

            Boolean collideLeftTop = block2d[leftTopTile.y][leftTopTile.x] != null;
            Boolean collideLeftBottom = block2d[leftBottomTile.y][leftBottomTile.x] != null;
            Boolean collideRightBottom = block2d[rightBottomTile.y][rightBottomTile.x] != null;
            Boolean collideRightTop = block2d[rightTopTile.y][rightTopTile.x] != null;

            List<Boolean> collideList = List.of(collideLeftTop, collideLeftBottom, collideRightBottom, collideRightTop);
            int collideCount = 0;
            for (Boolean isCollide : collideList) {
                if (isCollide) collideCount++;
            }

            if (collideCount == 0) {
                offset = newOffset;
            } else if (collideCount == 1) {
                switch (direction) {
                    case UP -> {
                        if (collideLeftTop) {
                            int blockRightX = Sizes.TILE_SIZE.width * (leftTopTile.x + 1);
                            int diff = blockRightX - newRectangle.x;
                            if (diff < COLLIDE_TOLERANCES) {
                                offset = new Offset(newRectangle.x + diff, newRectangle.y);
                            }
                        } else if (collideRightTop) {
                            int blockLeftX = Sizes.TILE_SIZE.width * rightTopTile.x;
                            int diff = (newRectangle.x + newRectangle.width) - blockLeftX;
                            if (diff < COLLIDE_TOLERANCES) {
                                offset = new Offset(newRectangle.x - diff - 1, newRectangle.y);
                            }
                        }
                    }
                    case DOWN -> {
                        if (collideLeftBottom) {
                            int blockRightX = Sizes.TILE_SIZE.width * (leftBottomTile.x + 1);
                            int diff = blockRightX - newRectangle.x;
                            if (diff < COLLIDE_TOLERANCES) {
                                offset = new Offset(newRectangle.x + diff, newRectangle.y);
                            }
                        } else if (collideRightBottom) {
                            int blockLeftX = Sizes.TILE_SIZE.width * rightBottomTile.x;
                            int diff = (newRectangle.x + newRectangle.width) - blockLeftX;
                            if (diff < COLLIDE_TOLERANCES) {
                                offset = new Offset(newRectangle.x - diff - 1, newRectangle.y);
                            }
                        }
                    }
                    case LEFT -> {
                        if (collideLeftTop) {
                            int blockTopY = Sizes.TILE_SIZE.height * (leftTopTile.y + 1);
                            int diff = blockTopY - newRectangle.y;
                            if (diff < COLLIDE_TOLERANCES) {
                                offset = new Offset(newRectangle.x, newRectangle.y + diff);
                            }
                        } else if (collideLeftBottom) {
                            int blockBottomY = Sizes.TILE_SIZE.height * leftBottomTile.y;
                            int diff = (newRectangle.y + newRectangle.width) - blockBottomY;
                            if (diff < COLLIDE_TOLERANCES) {
                                offset = new Offset(newRectangle.x, newRectangle.y - diff - 1);
                            }
                        }
                    }
                    case RIGHT -> {
                        if (collideRightTop) {
                            int blockTopY = Sizes.TILE_SIZE.height * (leftTopTile.y + 1);
                            int diff = blockTopY - newRectangle.y;
                            if (diff < COLLIDE_TOLERANCES) {
                                offset = new Offset(newRectangle.x, newRectangle.y + diff);
                            }
                        } else if (collideRightBottom) {
                            int blockBottomY = Sizes.TILE_SIZE.height * leftBottomTile.y;
                            int diff = (newRectangle.y + newRectangle.width) - blockBottomY;
                            if (diff < COLLIDE_TOLERANCES) {
                                offset = new Offset(newRectangle.x, newRectangle.y - diff - 1);
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean isInRange(Offset offset) {
        final int maxX = Sizes.MAP_WIDTH - Sizes.TILE_SIZE.width;
        final int maxY = Sizes.MAP_HEIGHT - Sizes.TILE_SIZE.height;

        return 0 <= offset.x && 0 <= offset.y &&
                offset.x < maxX && offset.y < maxY;
    }

    private Rectangle getPlayerRectangleAt(Offset offset) {
        return new Rectangle(offset.x, offset.y, Sizes.TILE_SIZE.width - 1, Sizes.TILE_SIZE.height - 1);
    }

    private Offset getTileOffsetBy(Offset renderOffset) {
        return new Offset(renderOffset.x / WIDTH, renderOffset.y / HEIGHT);
    }

    /**
     * @return 오브젝트의 중앙 위치를 셀로 치환한 {@link Offset}을 반환한다. 이는 오브젝트 좌표와 크기를 이용하여 계산된다.
     */
    public Offset getCenterTileOffset() {
        Offset centerOffset = getCenterOffset();
        return new Offset(centerOffset.x / WIDTH, centerOffset.y / HEIGHT);
    }

    /**
     * @return 오브젝트의 중앙 위치를 반환한다. 이는 오브젝트 좌표와 크기를 이용하여 계산된다.
     */
    private Offset getCenterOffset() {
        int widthHalf = WIDTH / 2;
        int heightHalf = HEIGHT / 2;
        return new Offset(offset.x + widthHalf, offset.y + heightHalf);
    }
}
