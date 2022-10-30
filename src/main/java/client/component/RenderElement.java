package client.component;

import domain.model.Offset;
import lombok.NonNull;

import javax.swing.*;
import java.awt.*;

import static domain.constant.Sizes.TILE_SIZE;

public abstract class RenderElement {

    public final void draw(Graphics g, Offset objectOffset) {
        Offset renderOffset = getRenderOffset(objectOffset);
        Dimension renderSize = getImageOneFrameSize();
        int frame = getFrame();

        g.drawImage(
                getImageIcon().getImage(),
                renderOffset.x,
                renderOffset.y,
                renderOffset.x + renderSize.width,
                renderOffset.y + renderSize.height,
                frame * renderSize.width,
                0,
                (frame + 1) * renderSize.width,
                renderSize.height,
                null);
    }

    private Offset getRenderOffset(Offset objectOffset) {
        Dimension imageSize = getImageOneFrameSize();
        int addX = -(imageSize.width - TILE_SIZE.width) / 2;
        int addY = -(imageSize.height - TILE_SIZE.height);
        int renderX = objectOffset.x + addX;
        int renderY = objectOffset.y + addY;

        return new Offset(renderX, renderY);
    }

    @NonNull
    protected abstract ImageIcon getImageIcon();

    @NonNull
    protected abstract Dimension getImageOneFrameSize();
    protected abstract int getFrame();
}
