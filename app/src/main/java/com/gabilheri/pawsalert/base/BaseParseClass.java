package com.gabilheri.pawsalert.base;

import com.parse.ParseObject;

import java.lang.reflect.Field;

import timber.log.Timber;

/**
 * Created by <a href="mailto:marcus@gabilheri.com">Marcus Gabilheri</a>
 *
 * @author Marcus Gabilheri
 * @version 1.0
 * @since 1/19/16.
 */
public abstract class BaseParseClass<T> extends ParseObject {

    public abstract T instance();

    public T fromParseObject(ParseObject po) {
        T obj = instance();
        Field[] fields = getClass().getDeclaredFields();
        for (Field f : fields) {
            if (f.isAnnotationPresent(Skip.class)) {
                continue;
            }
            f.setAccessible(true);
            try {
                f.set(obj, po.get(f.getName()));
            } catch (IllegalAccessException ex) {
                throwFieldException(ex, f);
            }
        }

        return obj;
    }

    public BaseParseClass<T> toParseObject(Object obj) {
        for (Field f : obj.getClass().getDeclaredFields()) {
            f.setAccessible(true);
            try {
                Object val = f.get(obj);
                if (val != null) {
                    put(f.getName(), val);
                }
            } catch (IllegalAccessException ex) {
                throwFieldException(ex, f);
            }
        }
        return this;
    }

    public static void throwFieldException(Exception ex, Field f) {
        Timber.e(ex, "Error setting property: " + f.getName());
    }
}
