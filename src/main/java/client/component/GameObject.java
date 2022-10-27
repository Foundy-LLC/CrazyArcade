package client.component;

import domain.model.Offset;
import lombok.NonNull;

import javax.swing.*;
import java.awt.*;

import static domain.constant.Sizes.TILE_SIZE;

public abstract class GameObject extends JLabel {

    @NonNull
    private final Image imageIcon;

    @NonNull
    private final Offset imageOffset = getImageOffset();

    public GameObject(ImageIcon imageIcon) {
        this.imageIcon = imageIcon.getImage();

        Dimension size = getSizeOfImage();
        setSize(size.width, size.height);
        setLayout(null);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(imageIcon, 0, 0, null);
        setOpaque(false);
    }

    private Offset getImageOffset() {
        Dimension size = getSizeOfImage();
        int x = -(size.width - TILE_SIZE.width) / 2;
        int y = -(size.height - TILE_SIZE.height) / 2;
        return new Offset(x, y);
    }

    public void setOffset(Offset newOffset) {
        int renderX = newOffset.x + imageOffset.x;
        int renderY = newOffset.y + imageOffset.y;
        setLocation(renderX, renderY);
        invalidate();
    }

    @NonNull
    protected abstract Dimension getSizeOfImage();
}
