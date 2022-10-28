package client.component;

import domain.constant.Sizes;
import domain.model.Block;
import domain.model.Offset;

import javax.swing.*;
import java.awt.*;

public class WaterBombComponent extends GameComponent<Block> {

    public WaterBombComponent(ImageIcon imageIcon, Dimension imageSize, Offset tileOffset) {
        super(imageIcon, imageSize);
        int x = tileOffset.x * Sizes.TILE_SIZE.width;
        int y = tileOffset.y * Sizes.TILE_SIZE.height;
        Offset offset = new Offset(x, y);
        setOffset(offset);
    }

    @Override
    public void updateState(Block block) {

    }
}