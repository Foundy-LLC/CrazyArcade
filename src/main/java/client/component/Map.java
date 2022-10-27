package client.component;

import domain.constant.Sizes;
import domain.model.Offset;

import javax.swing.*;
import java.awt.*;

public class Map extends JPanel {

    public static final Offset MAP_LEFT_TOP = new Offset(26, 53);

    public Map() {
        setLayout(null);
        setBounds(MAP_LEFT_TOP.x, MAP_LEFT_TOP.y, Sizes.MAP_WIDTH, Sizes.MAP_HEIGHT);
        setBackground(Color.BLACK);
    }
}
