package domain.model;

import lombok.Getter;

import java.io.Serializable;

public class WaterBomb implements Serializable {

    private static final int EXPLOSION_MILLI = 3_000;

    @Getter
    private final long installedMilli = System.currentTimeMillis();

    @Getter
    private final Player installer;

    public WaterBomb(Player installer) {
        this.installer = installer;
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

    public int getLength() {
        // TODO use player water bomb length
        return 1;
    }
}
