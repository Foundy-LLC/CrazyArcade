package client.component;

import client.constant.ImageIcons;
import domain.model.Block;
import lombok.NonNull;

import javax.swing.*;
import java.awt.*;

public class BlockRenderElement extends RenderElement {

    private static final Dimension SIZE = new Dimension(68, 67);

    private static final int MAX_BLOCK_POP_FRAME = 5;

    private final Block block;

    public BlockRenderElement(@NonNull Block block) {
        this.block = block;
    }

    @Override
    protected ImageIcon getImageIcon() {
        int imageIndex = block.getBlockImageIndex();
        if (block.isDisappearing()) {
            return ImageIcons.BLOCK_POPS[imageIndex];
        }
        return ImageIcons.BLOCKS[imageIndex];
    }

    @Override
    protected Dimension getImageOneFrameSize() {
        return SIZE;
    }

    @Override
    protected int getFrame() {
        if (block.isDisappearing()) {
            long currentMilli = System.currentTimeMillis();
            long passedMilli = currentMilli - block.getWaterWaveCollideTimeMilli();
            int gap = Block.DISAPPEAR_ANIM_MILLI / MAX_BLOCK_POP_FRAME;
            return (int) ((passedMilli / gap));
        }
        return 0;
    }
}
