package domain.model;

import lombok.Getter;

import java.io.Serializable;

public class WaterWave implements Serializable {

    public static final int DURATION_MILLI = 600;

    @Getter
    private final Direction direction;

    @Getter
    private final boolean isEnd;

    @Getter
    private final long createdMilli = System.currentTimeMillis();

    /**
     *
     * @param direction `null`인 경우 정중앙에 위치한 물줄기이다.
     */
    public WaterWave(Direction direction, boolean isEnd) {
        this.direction = direction;
        this.isEnd = isEnd;

    }

    public boolean shouldDisappear() {
        long currentMilli = System.currentTimeMillis();
        long passedMilli = currentMilli - createdMilli;

        return passedMilli >= DURATION_MILLI;
    }
}
