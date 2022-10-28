package client.component;

import client.util.ImageIcons;
import domain.constant.Sizes;
import domain.model.Block;
import domain.model.Offset;
import lombok.NonNull;

import java.awt.*;

public class BlockComponent extends GameComponent<Block>{

    private static final Dimension SIZE = new Dimension(68, 67);

    public BlockComponent(Offset tileOffset) {
        super(ImageIcons.BLOCK1);
        int x = tileOffset.x * Sizes.TILE_SIZE.width;
        int y = tileOffset.y * Sizes.TILE_SIZE.height;
        Offset offset = new Offset(x, y);
        setOffset(offset);
    }

    @Override
    protected @NonNull Dimension getSizeOfImage() {
        return SIZE;
    }

    @Override
    public void updateState(Block block) {

    }
}