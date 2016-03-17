package com.gabilheri.pawsalert.ui.details;

import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.gabilheri.pawsalert.R;
import com.gabilheri.pawsalert.base.BaseActivity;
import com.gabilheri.pawsalert.base.ImagePagerAdapter;
import com.gabilheri.pawsalert.helpers.Const;

import java.util.List;

import butterknife.Bind;

/**
 * Created by <a href="mailto:marcus@gabilheri.com">Marcus Gabilheri</a>
 *
 * @author Marcus Gabilheri
 * @version 1.0
 * @since 3/13/16.
 */
public class ActivityPictures extends BaseActivity {

    @Bind(R.id.viewpager)
    ViewPager mViewPager;

    ImagePagerAdapter mImagePagerAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enableBackNav();

        String title = getIntent().getExtras().getString(Const.TITLE_EXTRA);

        if (title != null) {
            setTitle(title);
        }

        List<String> pictures = getIntent().getExtras().getStringArrayList(Const.IMAGE_EXTRA);
        if (pictures != null) {
            mImagePagerAdapter = new ImagePagerAdapter(this, pictures);
            mViewPager.setAdapter(mImagePagerAdapter);
        }

    }

    @Override
    public int getLayoutResource() {
        return R.layout.activity_pictures;
    }
}
