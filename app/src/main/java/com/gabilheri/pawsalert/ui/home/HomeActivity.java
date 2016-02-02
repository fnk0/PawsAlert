package com.gabilheri.pawsalert.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.gabilheri.pawsalert.base.BaseDrawerPagerActivity;
import com.gabilheri.pawsalert.ui.add.AddPetActivity;

/**
 * Created by <a href="mailto:marcus@gabilheri.com">Marcus Gabilheri</a>
 *
 * @author Marcus Gabilheri
 * @version 1.0
 * @since 1/18/16.
 */
public class HomeActivity extends BaseDrawerPagerActivity implements View.OnClickListener {

    public static final int ADD_PET = 100;

    PetListFragment mMissingFragment;
    PetListFragment mAdoptFragment;
    PetListFragment mFavoritesFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        enableFab(true, this);

        mMissingFragment = PetListFragment.newInstance(PetListFragment.FRAGMENT_MISSING);
        mAdoptFragment = PetListFragment.newInstance(PetListFragment.FRAGMENT_ADOPT);
        mFavoritesFragment = PetListFragment.newInstance(PetListFragment.FRAGMENT_FAVORITE);

        addFragment("MISSING", mMissingFragment);
        addFragment("ADOPT", mAdoptFragment);
        addFragment("FAVORITES", mFavoritesFragment);

        super.initPager();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        startActivityForResult(new Intent(this, AddPetActivity.class), ADD_PET);
    }
}
