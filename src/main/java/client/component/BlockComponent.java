package client.component;

import client.util.ImageIcons;
import domain.constant.Sizes;
import domain.model.Block;
import domain.model.Offset;
import lombok.NonNull;

import java.awt.*;

public class BlockComponent extends GameComponent<Block>{

    private static final Dimension SIZE = new Dimension(68, 67);

    public BlockComponent(Offset offsetInMap) {
        super(ImageIcons.BLOCK1);
        int renderX = offsetInMap.x * Sizes.TILE_SIZE.width;
        int renderY = offsetInMap.y * Sizes.TILE_SIZE.height;
        Offset renderOffset = new Offset(renderX, renderY);
        setOffset(renderOffset);
    }

    @Override
    protected @NonNull Dimension getSizeOfImage() {
        return SIZE;
    }

    @Override
    public void updateState(Block block) {

    }
}
