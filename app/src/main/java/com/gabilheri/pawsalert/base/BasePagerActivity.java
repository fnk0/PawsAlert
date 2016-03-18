package com.gabilheri.pawsalert.base;

import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import com.gabilheri.pawsalert.R;

import butterknife.Bind;

/**
 * Created by <a href="mailto:marcus@gabilheri.com">Marcus Gabilheri</a>
 *
 * @author Marcus Gabilheri
 * @version 1.0
 * @since 3/18/16.
 */
public class BasePagerActivity extends BaseActivity {

    @Bind(R.id.tabs)
    protected TabLayout mTabHost;

    @Bind(R.id.viewpager)
    protected ViewPager mPager;

    protected FragmentAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new FragmentAdapter(mFragmentManager);
    }



    protected void addFragment(String title, Fragment fragment) {
        mAdapter.addFragment(fragment, title);
    }

    protected void setPage(int page) {
        mPager.setCurrentItem(page);
    }

    protected void initPager() {
        mPager.setAdapter(mAdapter);
        if(mAdapter.getCount() > 0) {
            mPager.setOffscreenPageLimit(mAdapter.getCount());
        }
        mTabHost.setupWithViewPager(mPager);
    }

}
