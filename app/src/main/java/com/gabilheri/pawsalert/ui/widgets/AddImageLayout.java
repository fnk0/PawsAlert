package com.gabilheri.pawsalert.ui.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.gabilheri.pawsalert.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by <a href="mailto:marcus@gabilheri.com">Marcus Gabilheri</a>
 *
 * @author Marcus Gabilheri
 * @version 1.0
 * @since 3/17/16.
 */
public class AddImageLayout extends RelativeLayout implements View.OnClickListener {

    @Bind(R.id.displayImage)
    ImageView mDisplayImage;

    @Bind(R.id.removeImage)
    ImageView mRemoveImage;

    String mImagePath;
    OnImageRemovedCallback mCallback;

    public AddImageLayout(Context context) {
        super(context);
        init();
    }

    public AddImageLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AddImageLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        inflate(getContext(), R.layout.layout_add_image, this);
        ButterKnife.bind(this);
        mRemoveImage.setOnClickListener(this);
    }

    public String getImagePath() {
        return mImagePath;
    }

    public AddImageLayout setImagePath(String imagePath) {
        mImagePath = imagePath;
        Glide.with(getContext())
                .load(imagePath)
                .into(mDisplayImage);
        return this;
    }

    public OnImageRemovedCallback getCallback() {
        return mCallback;
    }

    public AddImageLayout setCallback(OnImageRemovedCallback callback) {
        mCallback = callback;
        return this;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.removeImage) {
            if(mCallback != null) {
                mCallback.onImageRemoved(mImagePath);
            }
        }
    }
}
