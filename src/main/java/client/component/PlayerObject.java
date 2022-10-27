package client.component;

import client.util.ImageIcons;
import domain.model.Offset;
import domain.model.Player;
import lombok.NonNull;

import java.awt.*;

public class PlayerObject extends GameObject<Player> {

    public static final Dimension SIZE = new Dimension(64,76);

    public PlayerObject() {
        super(ImageIcons.BAZZI_DOWN);
    }

    @Override
    @NonNull
    protected Dimension getSizeOfImage() {
        return SIZE;
    }

    @Override
    public void updateState(Player player) {
        Offset oldOffset = this.getOffset();
        Offset newOffset = player.getOffset();
        if (!newOffset.equals(oldOffset)){
            setOffset(newOffset);
        }
    }
}
