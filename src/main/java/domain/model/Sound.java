package domain.model;

import java.io.Serializable;

public enum Sound implements Serializable {

    // TODO 물방울 효과음으로 바꾸기
    PLAYER_TRAP("pt_in_react.wav"),

    EAT_ITEM("eat_item.wav"),

    BOMB_SET("bomb_set.wav"),

    WATER_WAVE("wave.wav");

    public final String path;

    Sound(String fileName) {
        this.path = "assets/sound/" + fileName;
    }
}
