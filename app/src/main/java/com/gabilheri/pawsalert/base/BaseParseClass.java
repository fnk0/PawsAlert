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
public abstract class BaseParseClass<T> {

    public abstract T instance();

    public T fromParseObject(ParseObject po) {
        T obj = instance();
        Field[] fields = getClass().getDeclaredFields();
        for (Field f : fields) {
            f.setAccessible(true);
            try {
                f.set(obj, po.get(f.getName()));
            } catch (IllegalAccessException ex) {
                throwFieldException(ex, f);
            }
        }
        return obj;
    }

    public static ParseObject toParseObject(Object obj, Class clazz) {
        ParseObject parseObject = ParseObject.create(clazz.getName());

        for (Field f : obj.getClass().getDeclaredFields()) {
            f.setAccessible(true);
            try {
                parseObject.put(f.getName(), f.get(obj));
            } catch (IllegalAccessException ex) {
                throwFieldException(ex, f);
            }
        }

        return parseObject;
    }

    public static void throwFieldException(Exception ex, Field f) {
        Timber.e(ex, "Error setting property: " + f.getName());
    }
}
