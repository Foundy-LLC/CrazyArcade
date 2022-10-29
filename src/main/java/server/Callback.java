package server;

public interface Callback<T> {
    void call(T t);
}
