package client.component;

import domain.constant.Sizes;
import domain.model.Offset;

import javax.swing.*;
import java.awt.*;

public class MapView extends JPanel {

    public static final Offset MAP_LEFT_TOP = new Offset(26, 53);

    public MapView() {
        setLayout(null);
        setBounds(MAP_LEFT_TOP.x, MAP_LEFT_TOP.y, Sizes.MAP_WIDTH, Sizes.MAP_HEIGHT);
        setBackground(Color.BLACK);
    }
}
