package domain.model;

import java.io.Serializable;

public enum Sound implements Serializable {

    LOGIN_VIEW_BGM("login_scene.wav"),

    LOBBY_VIEW_BGM("lobby_scene.wav"),

    GAME_VIEW_BGM("play_scene.wav"),

    // TODO 더 좋은 물방울 효과음으로 바꾸기
    PLAYER_TRAP("pt_in_react.wav"),

    // TODO 더 좋은 풍선 터지는 효과음으로 바꾸기
    PLAYER_DIE("player_die.wav"),

    EAT_ITEM("eat_item.wav"),

    BOMB_SET("bomb_set.wav"),

    WATER_WAVE("wave.wav"),

    WIN("win.wav"),

    // TODO 무승부 효과음 바꾸기
    DRAW("lose.wav"),

    LOSE("lose.wav");

    public final String path;

    Sound(String fileName) {
        this.path = "assets/sound/" + fileName;
    }
}
