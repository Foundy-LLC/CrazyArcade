package domain.model;

import lombok.Getter;
import lombok.NonNull;

import java.io.Serializable;

public class WaterBomb implements Serializable {

    public enum State {WAITING, EXPOSING, DESTROYED}

    private static final int EXPLOSION_MILLI = 3_000;
    private static final int DESTROY_MILLI = 4_000;

    public static final int FRAME_DELAY_MILLI = 300;

    private final long installedMilli = System.currentTimeMillis();

    @Getter
    private final int length;

    @Getter
    @NonNull
    private WaterBomb.State state = State.WAITING;

    public WaterBomb(int length) {
        this.length = length;
    }

    public int getFrame() {
        long currentMilli = System.currentTimeMillis();
        return (int) (((currentMilli - installedMilli) / FRAME_DELAY_MILLI) % 4);
    }

    public void updateState() {
        long currentMilli = System.currentTimeMillis();
        long passedMilli = currentMilli - installedMilli;

        if (passedMilli >= DESTROY_MILLI) {
            state = State.DESTROYED;
        } else if (passedMilli >= EXPLOSION_MILLI) {
            state = State.EXPOSING;
        }
    }

    public boolean isDestroyed() {
        return state == State.DESTROYED;
    }

    public boolean isWaiting() {
        return state == State.WAITING;
    }
}
