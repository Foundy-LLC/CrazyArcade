package client.core;

import lombok.NonNull;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class OutlinedLabel extends JLabel {

    private static final long serialVersionUID = 1L;

    @NonNull
    private Color outlineColor = Color.WHITE;

    @NonNull
    private boolean isPaintingOutline = false;

    @NonNull
    private boolean forceTransparent = false;

    @NonNull
    private final int thickness;

    public OutlinedLabel(int thickness) {
        super();
        this.thickness = thickness;
        setBorder(thickness);
    }

    public OutlinedLabel(String text, int thickness) {
        super(text);
        this.thickness = thickness;
        setBorder(thickness);
    }

    public OutlinedLabel(String text, int horizontalAlignment,
                         int thickness) {
        super(text, horizontalAlignment);
        this.thickness = thickness;
        setBorder(thickness);
    }

    private void setBorder(int thickness) {
        Border border = getBorder();
        Border margin = new EmptyBorder(thickness, thickness + 3,
                thickness, thickness + 3);
        setBorder(new CompoundBorder(border, margin));
    }

    public Color getOutlineColor() {
        return outlineColor;
    }

    public void setOutlineColor(Color outlineColor) {
        this.outlineColor = outlineColor;
        this.invalidate();
    }

    @Override
    public Color getForeground() {
        if (isPaintingOutline) {
            return outlineColor;
        } else {
            return super.getForeground();
        }
    }

    @Override
    public boolean isOpaque() {
        if (forceTransparent) {
            return false;
        } else {
            return super.isOpaque();
        }
    }

    @Override
    public void paint(Graphics g) {
        String text = getText();
        if (text == null || text.length() == 0) {
            super.paint(g);
            return;
        }

        // 1 2 3
        // 8 9 4
        // 7 6 5

        if (isOpaque()) {
            super.paint(g);
        }

        forceTransparent = true;
        isPaintingOutline = true;
        g.translate(-thickness, -thickness);
        super.paint(g); // 1
        g.translate(thickness, 0);
        super.paint(g); // 2
        g.translate(thickness, 0);
        super.paint(g); // 3
        g.translate(0, thickness);
        super.paint(g); // 4
        g.translate(0, thickness);
        super.paint(g); // 5
        g.translate(-thickness, 0);
        super.paint(g); // 6
        g.translate(-thickness, 0);
        super.paint(g); // 7
        g.translate(0, -thickness);
        super.paint(g); // 8
        g.translate(thickness, 0); // 9
        isPaintingOutline = false;

        super.paint(g);
        forceTransparent = false;
    }
}