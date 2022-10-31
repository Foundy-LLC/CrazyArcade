package domain.model;

import lombok.Getter;
import lombok.NonNull;

import java.io.Serializable;

public class Item implements Serializable {

    public enum Type {
        BUBBLE, FLUID, ULTRA, ROLLER
    }

    @Getter
    private long createdMilli = System.currentTimeMillis();

    @NonNull
    @Getter
    private final Type type;

    public Item(@NonNull Type type) {
        this.type = type;
    }
}
