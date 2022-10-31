package domain.model;

import java.io.Serializable;

public enum Sound implements Serializable {

    BOMB_SET("bomb_set.wav");

    public final String path;

    Sound(String fileName) {
        this.path = "assets/sound/" + fileName;
    }
}
