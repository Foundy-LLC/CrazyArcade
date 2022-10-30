package domain.model;

import lombok.Getter;

import java.io.Serializable;

public class Tile implements Serializable {

    public enum Type {
        T1, T2, T3, T4, T5, T6, T7, T8, T9, T10
    }

    @Getter
    private final Type type;

    public Tile() {
        double random = Math.random();
        if (random < 0.2) {
            type = Type.T5;
        } else if (random < 0.4) {
            type = Type.T6;
        } else if (random < 0.6) {
            type = Type.T7;
        } else if (random < 0.8) {
            type = Type.T8;
        } else {
            type = Type.T9;
        }
    }
}
