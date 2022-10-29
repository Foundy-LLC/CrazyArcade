package client.component;

import client.util.ImageIcons;
import domain.model.Direction;
import domain.model.Offset;
import domain.model.Player;
import lombok.NonNull;

import javax.swing.*;
import java.awt.*;

public class PlayerComponent extends GameComponent<Player> {

    private static final int NORMAL_FRAME_DELAY = 10;
    public static final Dimension NORMAL_IMAGE_SIZE = new Dimension(64, 76);
    public static final Dimension TRAP_IMAGE_SIZE = new Dimension(88, 82);

    @NonNull
    private Direction direction = Direction.DOWN;

    private int moveTick = 0;

    public PlayerComponent() {
        super(ImageIcons.BAZZI_DOWN, NORMAL_IMAGE_SIZE);
    }

    @Override
    public void updateState(Player player) {
        boolean isUpdated = false;
        final ImageIcon oldImageIcon = getImageIcon();
        final ImageIcon newImageIcon = player.isTrapped() ? ImageIcons.BAZZI_TRAP : getImageBy(direction);
        final Dimension imageSize = player.isTrapped() ? TRAP_IMAGE_SIZE : NORMAL_IMAGE_SIZE;
        final Offset oldOffset = this.getOffset();
        final Offset newOffset = player.getOffset();
        final Direction newDirection = player.getDirection();
        final boolean isTrapped = player.isTrapped();

        if (!newOffset.equals(oldOffset)) {
            setOffset(newOffset);
            isUpdated = true;

            if (newDirection == direction && !isTrapped) {
                moveToNextFrameWhenNormal();
            }
        }

        if (oldImageIcon != newImageIcon) {
            setImageIcon(newImageIcon, imageSize);
            isUpdated = true;
        }

        if (newDirection != direction) {
            direction = newDirection;
            if (!isTrapped) {
                setImageIcon(newImageIcon, imageSize);
                moveToNextFrameWhenNormal();
            }
            isUpdated = true;
        }

        if (isTrapped) {
            int newFrame = player.getFrameOfTrapImage();
            int oldFrame = getFrame();
            if (oldFrame != newFrame) {
                setFrame(newFrame);
                isUpdated = true;
            }
        }

        if (isUpdated) {
            repaint();
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

    private void moveToNextFrameWhenNormal() {
        int maxFrame = getMaxFrameWhenNormal();

        moveTick++;
        if (moveTick / NORMAL_FRAME_DELAY >= maxFrame) {
            moveTick = 0;
        }
        int frame = (moveTick / NORMAL_FRAME_DELAY) % maxFrame;
        setFrame(frame);
    }

    private int getMaxFrameWhenNormal() {
        return switch (direction) {
            case UP, DOWN -> 8;
            case LEFT, RIGHT -> 6;
        };
    }
}
