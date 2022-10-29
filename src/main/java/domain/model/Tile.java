package domain.model;

import client.util.ImageIcons;

import javax.swing.*;
import java.io.Serializable;

public class Tile implements Serializable {

    public enum Type {
        T1, T2, T3, T4, T5, T6, T7, T8, T9, T10
    }

    private final Type type;

    public Tile() {
        double random = Math.random();
        if (random < 0.2) {
            type = Type.T5;
        } else if (random < 0.4) {
            type = Type.T6;
        } else if (random < 0.6) {
            type = Type.T7;
        } else if (random < 0.8) {
            type = Type.T8;
        } else {
            type = Type.T9;
        }
    }

    public ImageIcon getImageIcon() {
        return switch (type) {
            case T1 -> ImageIcons.TILE1;
            case T2 -> ImageIcons.TILE2;
            case T3 -> ImageIcons.TILE3;
            case T4 -> ImageIcons.TILE4;
            case T5 -> ImageIcons.TILE5;
            case T6 -> ImageIcons.TILE6;
            case T7 -> ImageIcons.TILE7;
            case T8 -> ImageIcons.TILE8;
            case T9 -> ImageIcons.TILE9;
            case T10 -> ImageIcons.TILE10;
        };
    }
}
