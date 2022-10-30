package domain.model;

import client.util.ImageIcons;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;

public class Block implements Serializable {

    @Getter
    private final int blockImageIndex;

    public Block() {
        double random = Math.random() * ImageIcons.BLOCKS.length;
        blockImageIndex = (int) random;
    }
}
