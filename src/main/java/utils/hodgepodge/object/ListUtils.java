package utils.hodgepodge.object;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class ListUtils {
    public static <E> List<E> emptyList() {
        return Collections.emptyList();
    }

    public static <E> List<E> search(List<E> searchedList,ListSearchBooleanFunction<E> function) {
        return searchedList.stream().filter(function::function).collect(Collectors.toList());
    }

    public static <E> E firstItem(List<E> list) {
        if (list.isEmpty()) return null;

        return list.get(0);
    }

    public static <E> E lastItem(List<E> list) {
        if (list.size() <= 0) return null;

        return list.get(list.size() - 1);
    }

    public static <E> boolean isFirstItem(List<E> list,E item) {
        return list.indexOf(item) == 0;
    }

    public static <E> boolean isLastItem(List<E> list,E item) {
        return list.indexOf(item) == list.size() - 1;
    }

    public static <E> E pollFirst(List<E> list) {
        final E item = firstItem(list);

        if (item != null) {
            list.remove(item);
            return item;
        }

        return null;
    }

    public static <E> E pollLast(List<E> list) {
        final E item = lastItem(list);

        if (item != null) {
            list.remove(item);
            return item;
        }

        return null;
    }

    public static <E> String asString(List<E> list) {
        if (list == null || list.isEmpty()) return "[]";

        StringBuilder builder = new StringBuilder("[");

        for (E o : list) {
            builder.append(o);

            if (!isLastItem(list,o)) {
                builder.append(",");
            }
        }

        builder.append("]");

        return builder.toString();
    }

    @SuppressWarnings("unchecked")
    public static <E> void addArrayToList(List<E> list, E... array) {
        Collections.addAll(list, array);
    }

    public interface ListSearchBooleanFunction<E> {
        boolean function(E item);
    }
}
