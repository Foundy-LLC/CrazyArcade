package client.component;

import client.util.ImageIcons;
import domain.constant.Sizes;
import domain.model.Direction;
import domain.model.WaterWave;
import lombok.NonNull;

import javax.swing.*;
import java.awt.*;

public class WaterWaveRenderElement extends RenderElement {

    private final WaterWave waterWave;
    private final int maxFrame;

    public WaterWaveRenderElement(WaterWave waterWave) {
        this.waterWave = waterWave;
        this.maxFrame = waterWave.getDirection() != null ? 6 : 11;
    }

    @Override
    protected @NonNull ImageIcon getImageIcon() {
        Direction direction = waterWave.getDirection();
        if (direction == null) {
            return ImageIcons.WATER_BOMB_POP;
        }
        return switch (direction) {
            case UP -> waterWave.isEnd() ? ImageIcons.WATER_WAVE_UP_END : ImageIcons.WATER_WAVE_UP;
            case DOWN -> waterWave.isEnd() ? ImageIcons.WATER_WAVE_DOWN_END : ImageIcons.WATER_WAVE_DOWN;
            case LEFT -> waterWave.isEnd() ? ImageIcons.WATER_WAVE_LEFT_END : ImageIcons.WATER_WAVE_LEFT;
            case RIGHT -> waterWave.isEnd() ? ImageIcons.WATER_WAVE_RIGHT_END : ImageIcons.WATER_WAVE_RIGHT;
        };
    }

    @Override
    protected @NonNull Dimension getImageOneFrameSize() {
        return Sizes.TILE_SIZE;
    }

    protected int getFrame() {
        long currentMilli = System.currentTimeMillis();
        int gap = (WaterWave.DURATION_MILLI / (maxFrame - 1));
        return (int) (((currentMilli - waterWave.getCreatedMilli()) / gap) % maxFrame);
    }
}
