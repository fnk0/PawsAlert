package com.gabilheri.pawsalert.base;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.gabilheri.pawsalert.R;

import java.util.List;

import butterknife.ButterKnife;
import uk.co.senab.photoview.PhotoView;

/**
 * Created by <a href="mailto:marcus@gabilheri.com">Marcus Gabilheri</a>
 *
 * @author Marcus Gabilheri
 * @version 1.0
 * @since 3/13/16.
 */
public class ImagePagerAdapter extends PagerAdapter {

    Context mContext;
    List<String> imageUrls;
    LayoutInflater mInflater;

    public ImagePagerAdapter(Context mContext, List<String> imageUrls) {
        this.mContext = mContext;
        this.imageUrls = imageUrls;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return imageUrls.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View imageLayout = mInflater.inflate(R.layout.page_image, container, false);
        final PhotoView imageView = ButterKnife.findById(imageLayout, R.id.image);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageView.setMaximumScale(100);
        Glide.with(mContext)
                .load(imageUrls.get(position))
                .into(imageView);
        container.addView(imageLayout);
        return imageLayout;
    }

    public void addImages(List<String> urls) {
        imageUrls.addAll(urls);
        notifyDataSetChanged();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(((RelativeLayout) object));
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
