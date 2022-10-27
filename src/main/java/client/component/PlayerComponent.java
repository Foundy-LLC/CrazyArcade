package client.component;

import client.util.ImageIcons;
import domain.model.Direction;
import domain.model.Offset;
import domain.model.Player;
import lombok.NonNull;

import javax.swing.*;
import java.awt.*;

public class PlayerComponent extends GameComponent<Player> {

    public static final Dimension SIZE = new Dimension(64, 76);

    @NonNull
    private Direction direction = Direction.DOWN;

    private int frame;

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
        final Offset oldOffset = this.getOffset();
        final Offset newOffset = player.getOffset();
        final Direction newDirection = player.getDirection();

        if (!newOffset.equals(oldOffset)) {
            setOffset(newOffset);
            isUpdated = true;

            if (newDirection == direction) {
                nextFrame();
            }
        }

        if (newDirection != direction) {
            direction = newDirection;
            ImageIcon newIcon = getImageBy(direction);
            setImageIcon(newIcon);
            nextFrame();
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

    private void nextFrame() {
        switch (direction) {
            case UP, DOWN -> nextFrame(8);
            case LEFT, RIGHT -> nextFrame(6);
        }
    }
}
