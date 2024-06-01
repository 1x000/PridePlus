package utils.hodgepodge.object;

import utils.hodgepodge.object.time.TimerUtils;

import java.util.List;
import java.util.function.Consumer;

public final class DelayList<E> {
    private final TimerUtils timerUtils = new TimerUtils(true);
    private final List<E> currentList;

    private int index;

    public DelayList(List<E> currentList) {
        this.currentList = currentList;
    }

    public void forEach(long delay, Consumer<E> consumer) {
        if (delay < 0L) throw new IllegalArgumentException("Delay cannot be negative");

        while (index < currentList.size()) {
            implement(delay,consumer);
        }

        reset();
    }

    public void forEachNoStoppage(int delay,Consumer<E> consumer) {
        if (delay < 0) throw new IllegalArgumentException("Delay cannot be negative");

        if (index < currentList.size()) {
            implement(delay,consumer);
        } else {
            reset();
        }
    }

    private void implement(long delay,Consumer<E> consumer) {
        if (delay == 0L) {
            consumer.accept(currentList.get(index));
            index++;
        } else if (timerUtils.hasReached(delay)) {
            consumer.accept(currentList.get(index));
            index++;
        }
    }

    public void reset() {
        index = 0;
    }

    public List<E> getCurrentList() {
        return currentList;
    }
}
