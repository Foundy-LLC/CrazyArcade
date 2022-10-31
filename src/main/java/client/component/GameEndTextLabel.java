package client.component;

import client.constant.ImageIcons;

import javax.swing.*;
import java.awt.*;

public class GameEndTextLabel extends JLabel {

    private static final Dimension SIZE = new Dimension(300, 120);

    public enum Type {WIN, DRAW, LOSE}

    public GameEndTextLabel(Type type) {
        ImageIcon imageIcon = switch (type) {
            case WIN -> ImageIcons.WIN_TEXT;
            case DRAW -> ImageIcons.DRAW_TEXT;
            case LOSE -> ImageIcons.LOSE_TEXT;
        };
        setIcon(imageIcon);
        setBounds(266, 240, SIZE.width, SIZE.height);
        setOpaque(false);
    }
}
