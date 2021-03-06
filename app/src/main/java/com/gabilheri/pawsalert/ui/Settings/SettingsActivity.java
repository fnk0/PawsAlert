package com.gabilheri.pawsalert.ui.settings;

import android.os.Bundle;
import android.view.MenuItem;

import com.gabilheri.pawsalert.R;
import com.gabilheri.pawsalert.base.BaseActivity;

/**
 * Created by <a href="mailto:marcus@gabilheri.com">Marcus Gabilheri</a>
 *
 * @author Marcus Gabilheri
 * @version 1.0
 * @since 3/18/16.
 */
public class SettingsActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.settings));
        enableBackNav();
//        addSupportFragmentTocontainer(new SettingsFragment());
        addFragmentToContainer(new SettingsFragment(), "Settings");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public int getLayoutResource() {
        return R.layout.activity_settings;
    }
}
