package client.component;

import client.constant.ImageIcons;
import domain.model.Item;
import lombok.NonNull;

import javax.swing.*;
import java.awt.*;

public class ItemRenderElement extends RenderElement {

    private static final Dimension SIZE = new Dimension(56, 70);

    private static final int MAX_FRAME = 2;

    private final Item item;

    public ItemRenderElement(Item item) {
        this.item = item;
    }

    @Override
    protected @NonNull ImageIcon getImageIcon() {
        return switch (item.getType()) {
            case BUBBLE -> ImageIcons.ITEM_BUBBLE;
            case FLUID -> ImageIcons.ITEM_FLUID;
            case ULTRA -> ImageIcons.ITEM_ULTRA;
            case ROLLER -> ImageIcons.ITEM_ROLLER;
        };
    }

    @Override
    protected @NonNull Dimension getImageOneFrameSize() {
        return SIZE;
    }

    @Override
    protected int getFrame() {
        long currentMilli = System.currentTimeMillis();
        long createdMilli = item.getCreatedMilli();

        return (int) (((currentMilli - createdMilli) / 600) % MAX_FRAME);
    }
}
