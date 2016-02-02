package com.gabilheri.pawsalert.data;

import rx.Subscriber;
import timber.log.Timber;

/**
 * Created by <a href="mailto:marcus@gabilheri.com">Marcus Gabilheri</a>
 *
 * @author Marcus Gabilheri
 * @version 1.0
 * @since 1/19/16.
 */
public class RxSubscriber<T> extends Subscriber<T> {

    RxCallback<T> mRxCallback;

    public RxSubscriber(RxCallback<T> rxCallback) {
        mRxCallback = rxCallback;
    }

    @Override
    public void onCompleted() {
        this.unsubscribe();
    }

    @Override
    public void onError(Throwable e) {
        Timber.e(e, "Error getting data: " + e.getLocalizedMessage());
        mRxCallback.onDataError(e);
    }

    @Override
    public void onNext(T t) {
        mRxCallback.onDataReady(t);
    }
}
