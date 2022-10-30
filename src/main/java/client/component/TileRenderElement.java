package client.component;

import client.util.ImageIcons;
import domain.constant.Sizes;
import domain.model.Tile;
import lombok.NonNull;

import javax.swing.*;
import java.awt.*;

public class TileRenderElement extends RenderElement {

    private final Tile tile;

    public TileRenderElement(@NonNull Tile tile) {
        this.tile = tile;
    }

    @Override
    protected ImageIcon getImageIcon() {
        return switch (tile.getType()) {
            case T1 -> ImageIcons.TILE1;
            case T2 -> ImageIcons.TILE2;
            case T3 -> ImageIcons.TILE3;
            case T4 -> ImageIcons.TILE4;
            case T5 -> ImageIcons.TILE5;
            case T6 -> ImageIcons.TILE6;
            case T7 -> ImageIcons.TILE7;
            case T8 -> ImageIcons.TILE8;
            case T9 -> ImageIcons.TILE9;
            case T10 -> ImageIcons.TILE10;
        };
    }

    @Override
    protected Dimension getImageOneFrameSize() {
        return Sizes.TILE_SIZE;
    }

    @Override
    protected int getFrame() {
        return 0;
    }
}
