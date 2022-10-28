package domain.model;

import lombok.Getter;

import java.io.Serializable;

public class WaterCourse implements Serializable {

    public static final int DURATION_MILLI = 1_000;

    @Getter
    private final Direction direction;

    private final long createdMilli = System.currentTimeMillis();

    /**
     *
     * @param direction `null`인 경우 정중앙에 위치한 물줄기이다.
     */
    public WaterCourse(Direction direction) {
        this.direction = direction;
    }

    public int getFrame() {
        long currentMilli = System.currentTimeMillis();
        return (int) (((currentMilli - createdMilli) / 200) % 4);
    }

    public boolean shouldDisappear() {
        long currentMilli = System.currentTimeMillis();
        long passedMilli = currentMilli - createdMilli;

        return passedMilli >= DURATION_MILLI;
    }
}
