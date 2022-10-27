package client.component;

import client.util.ImageIcons;
import domain.model.Direction;
import domain.model.Offset;
import domain.model.Player;
import lombok.NonNull;

import javax.swing.*;
import java.awt.*;

public class PlayerComponent extends GameComponent<Player> {

    public static final Dimension SIZE = new Dimension(64,76);

    @NonNull
    private Direction direction = Direction.DOWN;

    public PlayerComponent() {
        super(ImageIcons.BAZZI_DOWN);
    }

    @Override
    @NonNull
    protected Dimension getSizeOfImage() {
        return SIZE;
    }

    @Override
    public void updateState(Player player) {
        boolean isUpdated = false;
        Offset oldOffset = this.getOffset();
        Offset newOffset = player.getOffset();
        if (!newOffset.equals(oldOffset)){
            setOffset(newOffset);
            isUpdated = true;
        }

        Direction newDirection = player.getDirection();
        if (newDirection != direction) {
            direction = newDirection;
            ImageIcon newIcon = getImageBy(direction);
            setImageIcon(newIcon);
            isUpdated = true;
        }

        if (isUpdated) {
            invalidate();
        }
    }

    private ImageIcon getImageBy(Direction direction) {
        return switch (direction) {
            case UP -> ImageIcons.BAZZI_UP;
            case DOWN -> ImageIcons.BAZZI_DOWN;
            case LEFT -> ImageIcons.BAZZI_LEFT;
            case RIGHT -> ImageIcons.BAZZI_RIGHT;
        };
    }
}
