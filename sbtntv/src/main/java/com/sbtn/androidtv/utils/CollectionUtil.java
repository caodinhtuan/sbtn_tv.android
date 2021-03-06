package com.sbtn.androidtv.utils;

import android.support.annotation.Nullable;

import java.util.Collection;
import java.util.List;

public final class CollectionUtil {
    /**
     * Whether a collection is null or empty
     */
    public static boolean isEmpty(@Nullable Collection items) {
        return items == null || items.isEmpty();
    }

    /**
     * Whether an array is null or empty
     */
    public static boolean isEmpty(@Nullable Object[] items) {
        return items == null || items.length == 0;
    }

    public static boolean isNotEmpty(@Nullable Collection items) {
        return items != null && !items.isEmpty();
    }

    public static boolean isNotEmpty(@Nullable Object[] items) {
        return items != null && items.length > 0;
    }

    /**
     * Get size of collection
     */
    public static int getSize(@Nullable Collection items) {
        if (isEmpty(items))
            return 0;
        return items.size();
    }

    /**
     * Whether a position is in range of collection
     */
    public static boolean inRange(@Nullable Collection items, int position) {
        return !isEmpty(items) && position >= 0 && position < items.size();
    }

    /**
     * Retrieve the first item in the collection
     */
    public static Object firstItem(@Nullable Collection items) {
        if (isEmpty(items))
            return null;
        return items.iterator().next();
    }

    /**
     * Retrieve the first item in the collection
     */
    public static Object firstItem(@Nullable List items) {
        if (isEmpty(items))
            return null;
        return items.get(0);
    }
}
