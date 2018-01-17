package org.dimdev.ddutils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class TypeFilter {
    public static <T extends U, U> List<T> filter(Collection<U> list, Class<T> filterClass) {
        List<T> filtered = new ArrayList<>();
        for (U e : list) {
            if (filterClass.isAssignableFrom(e.getClass())) {
                filtered.add(filterClass.cast(e));
            }
        }
        return filtered;
    }
}
