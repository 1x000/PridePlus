package dev.stable.event;

public interface IEventListener<T> {
    void call(T event);
}
