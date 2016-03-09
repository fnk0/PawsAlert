package com.gabilheri.pawsalert.data;

/**
 * Created by <a href="mailto:marcus@gabilheri.com">Marcus Gabilheri</a>
 *
 * @author Marcus Gabilheri
 * @version 1.0
 * @since 1/19/16.
 */
public interface RxCallback<T> {
    void onNext(T data);
    void onDataReady(T data);
    void onDataError(Throwable e);
}
