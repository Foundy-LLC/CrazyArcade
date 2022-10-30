package client.component;

import client.util.ImageIcons;
import domain.constant.Sizes;
import domain.model.Offset;
import domain.model.Player;

import javax.swing.*;
import java.awt.*;

public class PlayerRenderElement extends RenderElement {

    private static final int NORMAL_VERTICAL_IMAGE_MAX_FRAME = 8;
    private static final int NORMAL_HORIZONTAL_IMAGE_MAX_FRAME = 6;
    private static final int TRAP_IMAGE_MAX_FRAME = 23;
    private static final int DEAD_IMAGE_MAX_FRAME = 14;

    public static final Dimension NORMAL_ONE_FRAME_SIZE = new Dimension(64, 76);
    public static final Dimension TRAP_ONE_FRAME_SIZE = new Dimension(88, 82);
    public static final Dimension DIE_ONE_FRAME_SIZE = new Dimension(88, 144);

    private final Player player;

    public PlayerRenderElement(Player player) {
        this.player = player;
    }

    @Override
    protected ImageIcon getImageIcon() {
        if (player.isDead()) {
            return ImageIcons.BAZZI_DIE;
        }
        if (player.isTrapped()) {
            return ImageIcons.BAZZI_TRAP;
        }
        return switch (player.getDirection()) {
            case UP -> ImageIcons.BAZZI_UP;
            case DOWN -> ImageIcons.BAZZI_DOWN;
            case LEFT -> ImageIcons.BAZZI_LEFT;
            case RIGHT -> ImageIcons.BAZZI_RIGHT;
        };
    }

    @Override
    protected Dimension getImageOneFrameSize() {
        if (player.isDead()) {
            return DIE_ONE_FRAME_SIZE;
        }
        if (player.isTrapped()) {
            return TRAP_ONE_FRAME_SIZE;
        }
        return NORMAL_ONE_FRAME_SIZE;
    }

    @Override
    protected int getFrame() {
        if (player.isDead()) {
            return getFrameOfDeadImage();
        }
        if (player.isTrapped()) {
            return getFrameOfTrapImage();
        }
        if (!player.isMoving()) {
            return 0;
        }
        Offset offset = player.getOffset();
        int sum = (offset.y + offset.x) / 10;
        int maxFrame = switch (player.getDirection()) {
            case UP, DOWN -> NORMAL_VERTICAL_IMAGE_MAX_FRAME;
            case LEFT, RIGHT -> NORMAL_HORIZONTAL_IMAGE_MAX_FRAME;
        };
        return sum % maxFrame;
    }

    private int getFrameOfTrapImage() {
        if (!player.isTrapped()) {
            throw new IllegalStateException();
        }
        long currentMilli = System.currentTimeMillis();
        long gap = Player.MAX_ALIVE_TIME_IN_TRAP / (TRAP_IMAGE_MAX_FRAME - 1);
        return (int) ((currentMilli - player.getTrappedTimeMilli()) / gap) % TRAP_IMAGE_MAX_FRAME;
    }

    private int getFrameOfDeadImage() {
        if (!player.isDead()) {
            throw new IllegalStateException();
        }
        long currentMilli = System.currentTimeMillis();
        long passedMilli = currentMilli - player.getTrappedTimeMilli() - Player.MAX_ALIVE_TIME_IN_TRAP;
        long gap = Player.DEAD_ANIMATION_MILLI / (DEAD_IMAGE_MAX_FRAME - 1);
        return (int) (passedMilli / gap) % DEAD_IMAGE_MAX_FRAME;
    }
}
