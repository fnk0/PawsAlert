package com.gabilheri.pawsalert.ui.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.RadioButton;

import com.gabilheri.pawsalert.R;

import info.hoang8f.android.segmented.SegmentedGroup;

/**
 * Created by <a href="mailto:marcus@gabilheri.com">Marcus Gabilheri</a>
 *
 * @author Marcus Gabilheri
 * @version 1.0
 * @since 1/20/16.
 */
public class SegmentedControl extends SegmentedGroup {

    String[] mValues;

    public SegmentedControl(Context context) {
        super(context);
        build(null);
    }

    public SegmentedControl(Context context, AttributeSet attrs) {
        super(context, attrs);
        build(attrs);
    }

    public void build(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray arr = null;
            try {
                arr = getContext().obtainStyledAttributes(attrs, R.styleable.SegmentedControl);
                final int id = arr.getResourceId(R.styleable.SegmentedControl_values, -1);
                if (id != -1) {
                    mValues = getResources().getStringArray(id);

                    for (int i = 0; i < mValues.length; i++) {
                        RadioButton btn = new RadioButton(getContext(), null, info.hoang8f.android.segmented.R.style.RadioButton);
                        btn.setText(mValues[i]);
                        btn.setId(i);
                    }
                }
            } finally {
                if (arr != null) {
                    arr.recycle();
                }
            }
        }
    }

    public String[] getValues() {
        return mValues;
    }
}
