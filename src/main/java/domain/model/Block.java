package domain.model;

import client.util.ImageIcons;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;

public class Block implements Serializable {

    public static final int DISAPPEAR_ANIM_MILLI = 300;

    @Getter
    private final int blockImageIndex;

    @Getter
    private Long waterWaveCollideTimeMilli = null;

    public Block() {
        double random = Math.random() * ImageIcons.BLOCKS.length;
        blockImageIndex = (int) random;
    }

    public void collideWithWaterWave() {
        if (!isDisappearing()) {
            waterWaveCollideTimeMilli = System.currentTimeMillis();
        }
    }

    public boolean isDisappearing() {
        return waterWaveCollideTimeMilli != null;
    }

    public boolean shouldDisappear() {
        if (waterWaveCollideTimeMilli == null) {
            return false;
        }
        long currentMilli = System.currentTimeMillis();
        return currentMilli - waterWaveCollideTimeMilli >= DISAPPEAR_ANIM_MILLI;
    }
}
