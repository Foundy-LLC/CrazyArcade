package client.component;

import domain.constant.Sizes;
import domain.model.Offset;
import domain.model.WaterCourse;
import lombok.NonNull;

import javax.swing.*;
import java.awt.*;

public class WaterCourseComponent extends GameComponent<WaterCourse> {

    public WaterCourseComponent(@NonNull ImageIcon imageIcon, @NonNull Dimension imageSize, Offset tileOffset) {
        super(imageIcon, imageSize);
        int x = tileOffset.x * Sizes.TILE_SIZE.width;
        int y = tileOffset.y * Sizes.TILE_SIZE.height;
        Offset offset = new Offset(x, y);
        setOffset(offset);
    }

    @Override
    public void updateState(WaterCourse waterCourse) {

    }
}
