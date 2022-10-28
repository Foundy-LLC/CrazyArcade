package domain.model;

import lombok.Getter;
import lombok.NonNull;

import java.io.Serializable;

public class WaterBomb implements Serializable {

    private static final int EXPLOSION_MILLI = 3_000;

    public static final int FRAME_DELAY_MILLI = 300;

    private final long installedMilli = System.currentTimeMillis();

    @Getter
    private final int length;

    public WaterBomb(int length) {
        this.length = length;
    }

    public int getFrame() {
        long currentMilli = System.currentTimeMillis();
        return (int) (((currentMilli - installedMilli) / FRAME_DELAY_MILLI) % 4);
    }

    public boolean shouldExplode() {
        long currentMilli = System.currentTimeMillis();
        long passedMilli = currentMilli - installedMilli;

        return passedMilli >= EXPLOSION_MILLI;
    }

    public boolean isWaiting() {
        long currentMilli = System.currentTimeMillis();
        long passedMilli = currentMilli - installedMilli;

        return passedMilli < EXPLOSION_MILLI;
    }
}
