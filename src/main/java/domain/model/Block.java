package domain.model;

import client.util.ImageIcons;

import javax.swing.*;
import java.io.Serializable;

public class Block implements Serializable {


    private final int blockImageIndex;

    public Block() {
        double random = Math.random() * ImageIcons.BLOCKS.length;
        blockImageIndex = (int) random;
    }

    public ImageIcon getImageIcon() {
        return ImageIcons.BLOCKS[blockImageIndex];
    }
}
