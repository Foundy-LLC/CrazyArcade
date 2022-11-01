package domain.model;

import java.io.Serializable;

public enum Sound implements Serializable {

    // TODO 더 좋은 물방울 효과음으로 바꾸기
    PLAYER_TRAP("pt_in_react.wav"),

    // TODO 더 좋은 풍선 터지는 효과음으로 바꾸기
    PLAYER_DIE("player_die.wav"),

    EAT_ITEM("eat_item.wav"),

    BOMB_SET("bomb_set.wav"),

    WATER_WAVE("wave.wav");

    public final String path;

    Sound(String fileName) {
        this.path = "assets/sound/" + fileName;
    }
}
