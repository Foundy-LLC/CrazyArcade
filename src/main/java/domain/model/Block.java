package domain.model;

import client.util.ImageIcons;
import lombok.Getter;

import java.io.Serializable;

public class Block implements Serializable {

    public static final int DISAPPEAR_ANIM_MILLI = 300;

    @Getter
    private final int blockImageIndex;

    @Getter
    private Long waterWaveCollideTimeMilli = null;

    private final Item.Type itemType;

    public Block() {
        this(null);
    }

    public Block(Item.Type itemType) {
        this.itemType = itemType;
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

    public Item createItem() {
        if (itemType == null) {
            return null;
        }
        return new Item(itemType);
    }
}
