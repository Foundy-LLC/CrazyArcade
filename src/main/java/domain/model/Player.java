package domain.model;

import domain.constant.Sizes;
import lombok.Getter;
import lombok.NonNull;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

public class Player extends GameObject {

    private static final int COLLIDE_TOLERANCES = 10;
    private static final int MAX_ALIVE_TIME_IN_TRAP = 7_000;
    private static final int TRAP_IMAGE_MAX_FRAME = 13;

    @NonNull
    @Getter
    private final String name;

    @NonNull
    @Getter
    private Direction direction;

    @NonNull
    @Getter
    private Integer waterBombLength = 1;

    @Getter
    private Long trappedTimeMilli = null;

    public Player(String name, int tileX, int tileY) {
        super(tileX * Sizes.TILE_SIZE.width, tileY * Sizes.TILE_SIZE.height);
        this.name = name;
        this.direction = Direction.DOWN;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public void trapIntoWaterWave() {
        trappedTimeMilli = System.currentTimeMillis();
    }

    public boolean isTrapped() {
        return trappedTimeMilli != null;
    }

    public int getFrameOfTrapImage() {
        if (!isTrapped()) {
            throw new IllegalStateException();
        }
        long currentMilli = System.currentTimeMillis();
        long gap = MAX_ALIVE_TIME_IN_TRAP / (TRAP_IMAGE_MAX_FRAME - 1);
        return (int) ((currentMilli - trappedTimeMilli) / gap) % TRAP_IMAGE_MAX_FRAME;
    }

    public boolean isDead() {
        if (trappedTimeMilli == null) {
            return false;
        }
        long currentMilli = System.currentTimeMillis();
        return currentMilli - trappedTimeMilli >= MAX_ALIVE_TIME_IN_TRAP;
    }

    public int getSpeed() {
        if (isTrapped()) {
            return 1;
        }
        return 3;
    }

    public void move(Direction direction, Map map) {
        int speed = getSpeed();
        Offset newOffset = switch (direction) {
            case UP -> new Offset(offset.x, offset.y - speed);
            case DOWN -> new Offset(offset.x, offset.y + speed);
            case LEFT -> new Offset(offset.x - speed, offset.y);
            case RIGHT -> new Offset(offset.x + speed, offset.y);
        };

        if (isInRange(newOffset)) {
            Rectangle rectangle = getPlayerRectangleAt(newOffset);
            Block[][] block2d = map.getBlock2D();
            WaterBomb[][] waterBomb2d = deepCopy(map.getWaterBomb2d());

            // 플레이어가 물폭탄을 설치하여 플레이어 밑에 물폭탄이 있는 경우 충돌에서 제외한다.
            ArrayList<Offset> waterBombUnderUserOffsets = getWaterBombUnderUserOffsets(waterBomb2d);
            for (Offset waterBombUnderUserOffset : waterBombUnderUserOffsets) {
                waterBomb2d[waterBombUnderUserOffset.y][waterBombUnderUserOffset.x] = null;
            }

            Offset leftTopTile = getTileOffsetBy(new Offset(rectangle.x, rectangle.y));
            Offset leftBottomTile = getTileOffsetBy(new Offset(rectangle.x, rectangle.y + rectangle.height));
            Offset rightBottomTile = getTileOffsetBy(new Offset(rectangle.x + rectangle.width, rectangle.y + rectangle.height));
            Offset rightTopTile = getTileOffsetBy(new Offset(rectangle.x + rectangle.width, rectangle.y));

            Boolean collideLeftTop = isCollide(leftTopTile, block2d) || isCollide(leftTopTile, waterBomb2d);
            Boolean collideLeftBottom = isCollide(leftBottomTile, block2d) || isCollide(leftBottomTile, waterBomb2d);
            Boolean collideRightBottom = isCollide(rightBottomTile, block2d) || isCollide(rightBottomTile, waterBomb2d);
            Boolean collideRightTop = isCollide(rightTopTile, block2d) || isCollide(rightTopTile, waterBomb2d);

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
                            int diff = blockRightX - rectangle.x;
                            if (diff < COLLIDE_TOLERANCES) {
                                offset = new Offset(rectangle.x + diff, rectangle.y);
                            }
                        } else if (collideRightTop) {
                            int blockLeftX = Sizes.TILE_SIZE.width * rightTopTile.x;
                            int diff = (rectangle.x + rectangle.width) - blockLeftX;
                            if (diff < COLLIDE_TOLERANCES) {
                                offset = new Offset(rectangle.x - diff - 1, rectangle.y);
                            }
                        }
                    }
                    case DOWN -> {
                        if (collideLeftBottom) {
                            int blockRightX = Sizes.TILE_SIZE.width * (leftBottomTile.x + 1);
                            int diff = blockRightX - rectangle.x;
                            if (diff < COLLIDE_TOLERANCES) {
                                offset = new Offset(rectangle.x + diff, rectangle.y);
                            }
                        } else if (collideRightBottom) {
                            int blockLeftX = Sizes.TILE_SIZE.width * rightBottomTile.x;
                            int diff = (rectangle.x + rectangle.width) - blockLeftX;
                            if (diff < COLLIDE_TOLERANCES) {
                                offset = new Offset(rectangle.x - diff - 1, rectangle.y);
                            }
                        }
                    }
                    case LEFT -> {
                        if (collideLeftTop) {
                            int blockTopY = Sizes.TILE_SIZE.height * (leftTopTile.y + 1);
                            int diff = blockTopY - rectangle.y;
                            if (diff < COLLIDE_TOLERANCES) {
                                offset = new Offset(rectangle.x, rectangle.y + diff);
                            }
                        } else if (collideLeftBottom) {
                            int blockBottomY = Sizes.TILE_SIZE.height * leftBottomTile.y;
                            int diff = (rectangle.y + rectangle.width) - blockBottomY;
                            if (diff < COLLIDE_TOLERANCES) {
                                offset = new Offset(rectangle.x, rectangle.y - diff - 1);
                            }
                        }
                    }
                    case RIGHT -> {
                        if (collideRightTop) {
                            int blockTopY = Sizes.TILE_SIZE.height * (leftTopTile.y + 1);
                            int diff = blockTopY - rectangle.y;
                            if (diff < COLLIDE_TOLERANCES) {
                                offset = new Offset(rectangle.x, rectangle.y + diff);
                            }
                        } else if (collideRightBottom) {
                            int blockBottomY = Sizes.TILE_SIZE.height * leftBottomTile.y;
                            int diff = (rectangle.y + rectangle.width) - blockBottomY;
                            if (diff < COLLIDE_TOLERANCES) {
                                offset = new Offset(rectangle.x, rectangle.y - diff - 1);
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean isCollide(Offset offset, Block[][] block2d) {
        return block2d[offset.y][offset.x] != null;
    }

    private boolean isCollide(Offset offset, WaterBomb[][] waterBomb2d) {
        WaterBomb waterBomb = waterBomb2d[offset.y][offset.x];
        return waterBomb != null && waterBomb.isWaiting();
    }

    private @NonNull ArrayList<Offset> getWaterBombUnderUserOffsets(WaterBomb[][] waterBomb2d) {
        ArrayList<Offset> result = new ArrayList<>(4);
        Rectangle rectangle = getPlayerRectangleAt(offset);
        Offset leftTopTile = getTileOffsetBy(new Offset(rectangle.x, rectangle.y));
        Offset leftBottomTile = getTileOffsetBy(new Offset(rectangle.x, rectangle.y + rectangle.height));
        Offset rightBottomTile = getTileOffsetBy(new Offset(rectangle.x + rectangle.width, rectangle.y + rectangle.height));
        Offset rightTopTile = getTileOffsetBy(new Offset(rectangle.x + rectangle.width, rectangle.y));

        if (isCollide(leftTopTile, waterBomb2d)) {
            result.add(leftTopTile);
        }
        if (isCollide(leftBottomTile, waterBomb2d)) {
            result.add(leftBottomTile);
        }
        if (isCollide(rightBottomTile, waterBomb2d)) {
            result.add(rightBottomTile);
        }
        if (isCollide(rightTopTile, waterBomb2d)) {
            result.add(rightTopTile);
        }
        return result;
    }

    private boolean isInRange(Offset offset) {
        final int maxX = Sizes.MAP_WIDTH - Sizes.TILE_SIZE.width;
        final int maxY = Sizes.MAP_HEIGHT - Sizes.TILE_SIZE.height;

        return 0 <= offset.x && 0 <= offset.y &&
                offset.x <= maxX && offset.y <= maxY;
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

    private static WaterBomb[][] deepCopy(WaterBomb[][] arr) {
        WaterBomb[][] result = new WaterBomb[arr.length][arr[0].length];
        for (int i = 0; i < arr.length; i++) {
            result[i] = arr[i].clone();
        }
        return result;
    }

    public WaterBomb createWaterBomb() {
        return new WaterBomb(waterBombLength);
    }
}
