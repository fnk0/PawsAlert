package com.gabilheri.pawsalert.ui.shelter;

import android.os.Bundle;

import com.gabilheri.pawsalert.base.BaseActivity;
import com.gabilheri.pawsalert.ui.home.PetListFragment;

/**
 * Created by <a href="mailto:marcus@gabilheri.com">Marcus Gabilheri</a>
 *
 * @author Marcus Gabilheri
 * @version 1.0
 * @since 3/13/16.
 */
public class ActivityShelterAnimals extends BaseActivity {

    PetListFragment mPetListFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enableBackNav();

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            String shelterId = extras.getString(PetListFragment.SHELTER_ID);
            mPetListFragment = PetListFragment.newInstance(PetListFragment.FRAGMENT_ADOPT, shelterId);
            addFragmentToContainer(mPetListFragment, "PetListFragment");
        }
    }
}
