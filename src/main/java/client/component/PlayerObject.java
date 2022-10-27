package client.component;

import client.util.ImageIcons;
import lombok.NonNull;

import java.awt.*;

public class PlayerObject extends GameObject {

    public static final Dimension SIZE = new Dimension(64,76);

    public PlayerObject() {
        super(ImageIcons.BAZZI_DOWN);
    }

    @Override
    @NonNull
    protected Dimension getSizeOfImage() {
        return SIZE;
    }
}
