package client.util;

import client.constant.ImageIcons;

import java.awt.*;

public class Cursor {
    public static java.awt.Cursor mouseCursor() {
        Toolkit tk = Toolkit.getDefaultToolkit();
        Image cursorImage = ImageIcons.MOUSE_CURSOR.getImage();//커서로 사용할 이미지
        Point point = new Point(3, 3);
        return tk.createCustomCursor(cursorImage, point, "customCursor");
    }
}
