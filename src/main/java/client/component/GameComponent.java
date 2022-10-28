package client.component;

import domain.model.Offset;
import lombok.Getter;
import lombok.NonNull;

import javax.swing.*;
import java.awt.*;

import static domain.constant.Sizes.TILE_SIZE;

public abstract class GameComponent<STATE> extends JLabel {

    private static final int FRAME_DELAY = 10;

    @NonNull
    @Getter
    private Image image;

    @NonNull
    private final Offset imageOffset;

    @NonNull
    @Getter
    private Offset offset = new Offset(0, 0);

    @NonNull
    private Integer frame = 0;

    @NonNull
    private final Dimension imageSize;

    public GameComponent(@NonNull ImageIcon imageIcon, @NonNull Dimension imageSize) {
        this.image = imageIcon.getImage();
        this.imageSize = imageSize;
        this.imageOffset = getImageOffset();

        setSize(imageSize.width, imageSize.height);
        setLayout(null);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int imageWidth = imageSize.width;
        g.drawImage(image, -imageWidth * (frame / FRAME_DELAY), 0, null);
        setOpaque(false);
    }

    private Offset getImageOffset() {
        int x = -(imageSize.width - TILE_SIZE.width) / 2;
        int y = -(imageSize.height - TILE_SIZE.height);
        return new Offset(x, y);
    }

    /**
     * 이미지 사이즈를 고려한 좌표를 설정한다.
     *
     * @param newOffset 새로운 좌표
     */
    public void setOffset(Offset newOffset) {
        offset = newOffset;
        int renderX = newOffset.x + imageOffset.x;
        int renderY = newOffset.y + imageOffset.y;
        setLocation(renderX, renderY);
    }

    public Offset getRenderOffset() {
        int renderX = offset.x + imageOffset.x;
        int renderY = offset.y + imageOffset.y;
        return new Offset(renderX, renderY);
    }

    public void setImageIcon(ImageIcon imageIcon) {
        this.image = imageIcon.getImage();
    }

    public abstract void updateState(STATE state);

    public void nextFrame(int maxFrame) {
        frame++;
        frame = frame % (maxFrame * FRAME_DELAY);
        setOffset(offset);
    }
}
