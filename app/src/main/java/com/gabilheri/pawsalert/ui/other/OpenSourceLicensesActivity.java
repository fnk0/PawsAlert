package com.gabilheri.pawsalert.ui.other;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.gabilheri.pawsalert.R;
import com.gabilheri.pawsalert.base.BaseActivity;
import com.mikepenz.aboutlibraries.LibsBuilder;
import com.mikepenz.aboutlibraries.ui.LibsFragment;

/**
 * Created by <a href="mailto:marcus@gabilheri.com">Marcus Gabilheri</a>
 *
 * @author Marcus Gabilheri
 * @version 1.0
 * @since 3/18/16.
 */
public class OpenSourceLicensesActivity extends BaseActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.open_source_licenses));
        enableBackNav();

        LibsFragment fragment = new LibsBuilder().fragment();
        addFragmentToContainer(fragment, "LibsFragment");
    }

    @Override
    public int getLayoutResource() {
        return R.layout.activity_licenses;
    }
}
