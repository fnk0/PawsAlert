package com.gabilheri.pawsalert.base;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.SharedElementCallback;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;

import com.gabilheri.pawsalert.R;
import com.gabilheri.pawsalert.helpers.Const;

import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.BindColor;
import butterknife.ButterKnife;

/**
 * Created by <a href="mailto:marcusandreog@gmail.com">Marcus Gabilheri</a>
 *
 * @author Marcus Gabilheri
 * @version 1.0
 * @since 1/17/16.
 */
public abstract class BaseActivity extends AppCompatActivity {

    @BindColor(R.color.white_light_grey)
    protected int WHITE_COLOR;

    protected FragmentManager mFragmentManager = null;

    @Nullable
    @Bind(R.id.main_content)
    protected CoordinatorLayout mCoordinatorLayout;

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

    protected ProgressDialog pD;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResource());
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);

        ButterKnife.bind(this);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        if (mFragmentManager == null) {
            mFragmentManager = this.getFragmentManager();
        }
    }

    public int getLayoutResource() {
        return R.layout.activity_base;
    }

    protected void enableBackNav() {
        if (getSupportActionBar() != null) {
            isBackNav = true;
            getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
    }

    public void setTitle(@NonNull String title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }

    public void addFragmentToContainer(Fragment fragment, @Nullable String backStack) {
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment).addToBackStack(backStack).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home && isBackNav) {
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

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void enableActivityTransition() {
        setEnterSharedElementCallback(new SharedElementCallback() {
            View mSnapshot;
            @Override
            public void onSharedElementStart(List<String> sharedElementNames,
                                             List<View> sharedElements,
                                             List<View> sharedElementSnapshots) {
                addSnapshot(sharedElementNames, sharedElements, sharedElementSnapshots, false);
                if (mSnapshot != null) {
                    mSnapshot.setVisibility(View.VISIBLE);
                }
                findViewById(R.id.transitionLayout).setVisibility(View.INVISIBLE);
            }

            @Override
            public void onSharedElementEnd(List<String> sharedElementNames,
                                           List<View> sharedElements,
                                           List<View> sharedElementSnapshots) {
                addSnapshot(sharedElementNames, sharedElements, sharedElementSnapshots, true);
                if (mSnapshot != null) {
                    mSnapshot.setVisibility(View.INVISIBLE);
                }
                findViewById(R.id.transitionLayout).setVisibility(View.VISIBLE);
            }

            @Override
            public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                findViewById(R.id.transitionLayout).setVisibility(View.INVISIBLE);
            }

            private void addSnapshot(List<String> sharedElementNames,
                                     List<View> sharedElements,
                                     List<View> sharedElementSnapshots,
                                     boolean relayoutContainer) {
                if (mSnapshot == null) {
                    for (int i = 0; i < sharedElementNames.size(); i++) {
                        if (Const.TRANSITION_LAYOUT.equals(sharedElementNames.get(i))) {
                            if (sharedElements.get(i) instanceof FrameLayout) {
                                FrameLayout element = (FrameLayout) sharedElements.get(i);
                                mSnapshot = sharedElementSnapshots.get(i);
                                int width = mSnapshot.getWidth();
                                int height = mSnapshot.getHeight();
                                FrameLayout.LayoutParams layoutParams =
                                        new FrameLayout.LayoutParams(width, height);
                                layoutParams.gravity = Gravity.CENTER;
                                int widthSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
                                int heightSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);
                                mSnapshot.measure(widthSpec, heightSpec);
                                mSnapshot.layout(0, 0, width, height);
                                mSnapshot.setTransitionName("snapshot");
                                if (relayoutContainer) {
                                    ViewGroup container = (ViewGroup) findViewById(R.id.transitionLayout);
                                    int left = (container.getWidth() - width) / 2;
                                    int top = (container.getHeight() - height) / 2;
                                    element.measure(widthSpec, heightSpec);
                                    element.layout(left, top, left + width, top + height);
                                }
                                element.addView(mSnapshot, layoutParams);
                            }
                        }
                    }
                }
            }
        });
    }

    public SpannableStringBuilder getStringWithColor(int color, String text) {
        ForegroundColorSpan fgColorSpan = new ForegroundColorSpan(color);
        SpannableStringBuilder ssBuilder = new SpannableStringBuilder(text);
        ssBuilder.setSpan(fgColorSpan, 0, text.length(), 0);
        return ssBuilder;
    }

    public void showProgressDialog(String title, String text) {
        if (pD == null) {
            pD = new ProgressDialog(this);
            pD.setIndeterminate(true);
            pD.setCancelable(false);
        }
        if (title != null) {
            pD.setTitle(title);
        }
        if (text != null) {
            pD.setMessage(text);
        }
        pD.show();
    }

    public void dismissDialog() {
        if (pD != null && pD.isShowing()) {
            pD.dismiss();
        }
    }

    public void showSnackbar(String message) {
        if (mCoordinatorLayout != null) {
            Snackbar.make(mCoordinatorLayout, message, Snackbar.LENGTH_LONG).show();
        }
    }

    public void hideKeyboard() {
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void finishAfterTransition() {
        super.finishAfterTransition();
        try {
            findViewById(R.id.transitionLayout).setVisibility(View.VISIBLE);
        } catch (Exception ex) {}
    }
}
