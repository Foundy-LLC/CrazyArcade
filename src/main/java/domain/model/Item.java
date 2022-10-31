package domain.model;

import lombok.Getter;
import lombok.NonNull;

import java.io.Serializable;

public class Item implements Serializable {

    public enum Type {
        BUBBLE(0.08),
        FLUID(0.08),
        ULTRA(0.01),
        ROLLER(0.06);

        final double appearanceProbability;

        Type(double appearanceProbability) {
            this.appearanceProbability = appearanceProbability;
        }
    }

    @Getter
    private final long createdMilli = System.currentTimeMillis();

    @NonNull
    @Getter
    private final Type type;

    public Item(@NonNull Type type) {
        this.type = type;
    }
}
