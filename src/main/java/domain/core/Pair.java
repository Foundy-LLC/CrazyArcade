package domain.core;

import lombok.Getter;

public class Pair<T> {

    @Getter
    private T first;

    @Getter
    private T second;

    public Pair(T first, T second) {
        this.first = first;
        this.second = second;
    }
}
