package com.gabilheri.pawsalert.base;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.AppCompatTextView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.gabilheri.pawsalert.R;
import com.gabilheri.pawsalert.data.models.User;
import com.gabilheri.pawsalert.ui.home.HomeActivity;
import com.gabilheri.pawsalert.ui.settings.SettingsActivity;
import com.gabilheri.pawsalert.ui.shelter.ActivityShelters;
import com.gabilheri.pawsalert.ui.sign_in.SignInActivity;
import com.gabilheri.pawsalert.ui.success.ActivitySuccessStory;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by <a href="mailto:marcusandreog@gmail.com">Marcus Gabilheri</a>
 *
 * @author Marcus Gabilheri
 * @version 1.0
 * @since 1/17/16.
 */
public abstract class BaseDrawerActivity extends BaseActivity implements View.OnClickListener,
        SharedPreferences.OnSharedPreferenceChangeListener, NavigationView.OnNavigationItemSelectedListener, LogOutCallback {

    @Bind(R.id.drawer_layout)
    protected DrawerLayout mDrawerLayout;

    @Bind(R.id.nav_view)
    protected NavigationView mNavigationView;

    protected User mCurrentUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        try {
            mCurrentUser = (User) ParseUser.getCurrentUser();
        } catch (Exception ex) {
            
        }

        setupDrawerContent();
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (mDrawerLayout != null) {
                    mDrawerLayout.openDrawer(GravityCompat.START);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupDrawerContent() {
        mNavigationView.setNavigationItemSelectedListener(this);
        if (mCurrentUser != null) {
            RelativeLayout navHeader = (RelativeLayout) mNavigationView.getHeaderView(0);
            AppCompatTextView nameTv = ButterKnife.findById(navHeader, R.id.headerName);
            nameTv.setText(mCurrentUser.getFullName());

            Menu menu = mNavigationView.getMenu();
            int size = menu.size();

            for(int i = 0; i < size; i++) {
                MenuItem item = menu.getItem(i);
                if (item.getItemId() == R.id.sign_in_out) {
                    item.setTitle(R.string.sign_out);
                }
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_in_out:
                if (mCurrentUser != null) {
                    ParseUser.logOutInBackground(this);
                } else {
                    startActivity(new Intent(this, SignInActivity.class));
                }
                return true;

            case R.id.home:
                startActivity(new Intent(this, HomeActivity.class));
                return true;

            case R.id.shelter:
                startActivity(new Intent(this, ActivityShelters.class));
                return true;

            case R.id.stories:
                startActivity(new Intent(this, ActivitySuccessStory.class));
                return true;

            case R.id.settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            default:
                return false;
        }
    }

    @Override
    public void done(ParseException e) {
        if (e == null) {
            Intent i = new Intent(this, HomeActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finishAfterTransition();
        }
    }

    @Override
    public int getLayoutResource() {
        return R.layout.activity_base_drawer;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        PrefManager.with(this).registerSharedPreferenceListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        PrefManager.with(this).unregisterSharedPreferenceListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    }
}
