package cn.molokymc.prideplus.event.impl.game;

import cn.molokymc.prideplus.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class LegitUpdateEvent extends Event {
    private boolean isPost;

    public boolean isPre() {
        return !isPost;
    }

    public boolean isPost() {
        return isPost;
    }
}
