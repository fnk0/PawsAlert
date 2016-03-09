package com.gabilheri.pawsalert.data.models;

import android.view.View;

/**
 * Created by <a href="mailto:marcusandreog@gmail.com">Marcus Gabilheri</a>
 *
 * @author Marcus Gabilheri
 * @version 1.0
 * @since 3/8/16.
 */
public class TransitionWrapperModel<T> {

    T model;
    View view;
    int state;

    public TransitionWrapperModel() {
    }

    public TransitionWrapperModel(T model, View view, int state) {
        this.model = model;
        this.view = view;
        this.state = state;
    }

    public T getModel() {
        return model;
    }

    public TransitionWrapperModel setModel(T model) {
        this.model = model;
        return this;
    }

    public View getView() {
        return view;
    }

    public TransitionWrapperModel setView(View view) {
        this.view = view;
        return this;
    }

    public int getState() {
        return state;
    }

    public TransitionWrapperModel setState(int state) {
        this.state = state;
        return this;
    }
}
