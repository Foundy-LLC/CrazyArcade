package client.component;

import client.util.ImageIcons;
import domain.model.Block;
import lombok.NonNull;

import javax.swing.*;
import java.awt.*;

public class BlockRenderElement extends RenderElement {

    private static final Dimension SIZE = new Dimension(68, 67);

    private final Block block;

    public BlockRenderElement(@NonNull Block block) {
        this.block = block;
    }

    @Override
    protected ImageIcon getImageIcon() {
        return ImageIcons.BLOCKS[block.getBlockImageIndex()];
    }

    @Override
    protected Dimension getImageOneFrameSize() {
        return SIZE;
    }

    @Override
    protected int getFrame() {
        return 0;
    }
}
