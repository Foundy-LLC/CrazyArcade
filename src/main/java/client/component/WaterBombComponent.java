package client.component;

import client.util.ImageIcons;
import domain.constant.Sizes;
import domain.model.Block;
import domain.model.Offset;

import javax.swing.*;
import java.awt.*;

public class WaterBombComponent extends GameComponent<Block> {

    public WaterBombComponent(Offset tileOffset) {
        super(ImageIcons.WATER_BOMB_1, new Dimension(56, 54));
        int x = tileOffset.x * Sizes.TILE_SIZE.width;
        int y = tileOffset.y * Sizes.TILE_SIZE.height;
        Offset offset = new Offset(x, y);
        setOffset(offset);
    }

    @Override
    public void updateState(Block block) {

    }
}