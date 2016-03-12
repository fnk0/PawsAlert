package com.gabilheri.pawsalert.ui.shelter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.gabilheri.pawsalert.R;
import com.gabilheri.pawsalert.base.BaseDrawerActivity;

/**
 * Created by <a href="mailto:marcus@gabilheri.com">Marcus Gabilheri</a>
 *
 * @author Marcus Gabilheri
 * @version 1.0
 * @since 3/9/16.
 */
public class ActivityShelters extends BaseDrawerActivity implements View.OnClickListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enableFab(true, this);
        setTitle(getString(R.string.shelters));
        mNavigationView.getMenu().findItem(R.id.shelter).setChecked(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                startActivity(new Intent(this, ActivityAddShelter.class));
                break;
        }
    }
}
