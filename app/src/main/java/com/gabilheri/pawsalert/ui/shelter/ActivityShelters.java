package com.gabilheri.pawsalert.ui.shelter;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.gabilheri.pawsalert.R;
import com.gabilheri.pawsalert.base.BaseDrawerActivity;
import com.gabilheri.pawsalert.base.ItemCallback;
import com.gabilheri.pawsalert.data.queryManagers.AnimalShelterListManager;
import com.gabilheri.pawsalert.data.models.AnimalShelter;
import com.gabilheri.pawsalert.data.models.TransitionWrapperModel;
import com.gabilheri.pawsalert.data.models.User;
import com.gabilheri.pawsalert.helpers.Const;
import com.gabilheri.pawsalert.ui.animations.BangAnimationView;
import com.parse.ParseUser;

import java.util.List;

import butterknife.Bind;

/**
 * Created by <a href="mailto:marcus@gabilheri.com">Marcus Gabilheri</a>
 *
 * @author Marcus Gabilheri
 * @version 1.0
 * @since 3/9/16.
 */
public class ActivityShelters extends BaseDrawerActivity implements ItemCallback<TransitionWrapperModel<AnimalShelter>>,
        AnimalShelterListManager.AnimalShelterListCallback, View.OnClickListener {

    public static final int ADD_SHELTER = 9862;
    public static final int VIEW_SHELTER = 730;
    public static final int SHARE_SHELTER = 731;
    public static final int CALL_SHELTER = 732;

    @Bind(R.id.recyclerview)
    RecyclerView mRecyclerView;

    ShelterAdapter mShelterAdapter;
    AnimalShelterListManager mAnimalShelterListManager;
    User mCurrentUser;
    boolean hasAnimalShelter = false;
    protected BangAnimationView mBangAnimationView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.shelters));
        mNavigationView.setCheckedItem(R.id.shelter);

        mAnimalShelterListManager = new AnimalShelterListManager(this);

        mShelterAdapter = new ShelterAdapter(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mShelterAdapter);

        mCurrentUser = (User) ParseUser.getCurrentUser();
        enableFab(mCurrentUser != null, this);
        mBangAnimationView = BangAnimationView.attach2Window(this);

        mAnimalShelterListManager.getAnimalShelters();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                if (hasAnimalShelter) {
                    showSnackbar("A user can only have 1 animal shelter.");
                } else {
                    startActivity(new Intent(this, ActivityAddShelter.class));
                }
                break;
        }
    }

    @Override
    public void onItemCallback(TransitionWrapperModel<AnimalShelter> item) {
        switch (item.getState()) {
            case VIEW_SHELTER:
                AnimalShelter shelter = item.getModel();
                Intent intent = new Intent(this, ActivityShelterDetails.class);
                intent.putExtra(Const.OBJECT_ID, shelter.getObjectId());
                String picUrl = shelter.getCoverPhoto().getUrl();
                intent.putExtra(Const.IMAGE_EXTRA, picUrl);
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, item.getView(), Const.IMAGE_EXTRA);
                    startActivity(intent, options.toBundle());
                } else {
                    startActivity(intent);
                }
                break;
            case SHARE_SHELTER:
                mBangAnimationView.bang(item.getView());
                shareURL("http://www.stillwaterpaws.com/shelter.html?id=" + item.getModel().getObjectId());
                break;
            case CALL_SHELTER:
                mBangAnimationView.bang(item.getView());
                makePhoneCall(item.getModel().getPhoneNumber());
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == ADD_SHELTER) {
                mAnimalShelterListManager.getAnimalShelters();
            }
        }
    }

    @Override
    public void onAnimalShelterList(List<AnimalShelter> animalShelters) {
        for(AnimalShelter as : animalShelters) {
            if (as.getOwner() != null && mCurrentUser != null) {
                if (as.getOwner().getObjectId().equals(mCurrentUser.getObjectId())) {
                    hasAnimalShelter = true;
                }
            }
        }
        mShelterAdapter.addAll(animalShelters);
    }

    @Override
    public void onErrorFetchingAnimalShelters(Exception ex) {
        showSnackbar("Error fetching animal shelters. Please try again later.");
    }

    @Override
    public int getLayoutResource() {
        return R.layout.activity_long_toolbar_rv;
    }
}
