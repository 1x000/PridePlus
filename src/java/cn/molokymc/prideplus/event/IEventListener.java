package cn.molokymc.prideplus.event;

public interface IEventListener<T> {
    void call(T event);
}
