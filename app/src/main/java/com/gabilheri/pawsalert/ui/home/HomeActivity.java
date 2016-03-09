package com.gabilheri.pawsalert.ui.home;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.gabilheri.pawsalert.R;
import com.gabilheri.pawsalert.base.BaseDrawerPagerActivity;
import com.gabilheri.pawsalert.helpers.Const;
import com.gabilheri.pawsalert.ui.add.AddPetActivity;
import com.gabilheri.pawsalert.ui.sign_in.SignInActivity;

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

        addFragment(getString(R.string.missing), mMissingFragment);
        addFragment(getString(R.string.adopt), mAdoptFragment);
        addFragment(getString(R.string.favorites), mFavoritesFragment);

        super.initPager();
        mNavigationView.setCheckedItem(R.id.home);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (mCurrentUser != null) {
            Intent intent = new Intent(this, AddPetActivity.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                intent.putExtra(Const.TRANSITION_LAYOUT, true);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, mFloatingActionButton, Const.TRANSITION_LAYOUT);
                startActivityForResult(intent, ADD_PET, options.toBundle());
            } else {
                startActivityForResult(intent, ADD_PET);
            }
        } else {
            Intent intent = new Intent(this, SignInActivity.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                intent.putExtra(Const.TRANSITION_LAYOUT, true);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, mFloatingActionButton, Const.TRANSITION_LAYOUT);
                startActivity(intent, options.toBundle());
            } else {
                startActivity(intent);
            }
        }
    }
}
