package com.gabilheri.pawsalert.base;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.gabilheri.pawsalert.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by <a href="mailto:marcusandreog@gmail.com">Marcus Gabilheri</a>
 *
 * @author Marcus Gabilheri
 * @version 1.0
 * @since 1/17/16.
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected FragmentManager mFragmentManager = null;

    @Nullable
    @Bind(R.id.fab)
    protected FloatingActionButton mFloatingActionButton;

    @Nullable
    @Bind(R.id.toolbar)
    protected Toolbar toolbar;

    @Nullable
    @Bind(R.id.container)
    protected FrameLayout containerLayout;

    protected boolean isBackNav = false;
    protected String theme;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResource());
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);

        ButterKnife.bind(this);

        if(toolbar != null) {
            setSupportActionBar(toolbar);
        }

        if(mFragmentManager == null) {
            mFragmentManager = this.getFragmentManager();
        }
    }

    public int getLayoutResource() {
        return R.layout.activity_base;
    }

    protected void enableBackNav() {
        if(getSupportActionBar() != null) {
            isBackNav = true;
            getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
    }

    public void setTitle(@NonNull String title) {
        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }

    public void addFragmentToContainer(Fragment fragment, @Nullable String backStack) {
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment).addToBackStack(backStack).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home && isBackNav) {
            super.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    protected void enableFab(boolean enable, @Nullable View.OnClickListener listener) {
        if (mFloatingActionButton != null) {
            if (listener != null) {
                mFloatingActionButton.setOnClickListener(listener);
            }
            mFloatingActionButton.setVisibility(enable ? View.VISIBLE : View.GONE);
        }
    }
}
