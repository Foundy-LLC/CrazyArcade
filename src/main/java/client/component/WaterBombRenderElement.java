package client.component;

import client.constant.ImageIcons;
import domain.model.WaterBomb;

import javax.swing.*;
import java.awt.*;

public class WaterBombRenderElement extends RenderElement {

    private static final Dimension SIZE =  new Dimension(56, 54);

    public static final int FRAME_DELAY_MILLI = 300;

    private final WaterBomb waterBomb;

    public WaterBombRenderElement(WaterBomb waterBomb) {
        this.waterBomb = waterBomb;
    }

    @Override
    protected ImageIcon getImageIcon() {
        return ImageIcons.WATER_BOMB_1;
    }

    @Override
    protected Dimension getImageOneFrameSize() {
        return SIZE;
    }

    @Override
    protected int getFrame() {
        long currentMilli = System.currentTimeMillis();
        return (int) (((currentMilli - waterBomb.getInstalledMilli()) / FRAME_DELAY_MILLI) % 4);
    }
}