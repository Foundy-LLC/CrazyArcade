package client.component;

import client.constant.ImageIcons;
import lombok.NonNull;

import javax.swing.*;
import java.awt.*;

class ArrowRenderElement extends RenderElement {

    private static final Dimension SIZE = new Dimension(40, 46);

    @Override
    protected @NonNull ImageIcon getImageIcon() {
        return ImageIcons.SINGLE_PLAYER_ARROW;
    }

    @Override
    protected @NonNull Dimension getImageOneFrameSize() {
        return SIZE;
    }

    @Override
    protected int getFrame() {
        return 0;
    }
}