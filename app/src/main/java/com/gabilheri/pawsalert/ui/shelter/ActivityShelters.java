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
import com.gabilheri.pawsalert.data.models.AnimalShelter;
import com.gabilheri.pawsalert.data.models.TransitionWrapperModel;
import com.gabilheri.pawsalert.data.models.User;
import com.gabilheri.pawsalert.helpers.Const;
import com.parse.FindCallback;
import com.parse.ParseException;
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
        FindCallback<AnimalShelter>, View.OnClickListener {

    public static final int ADD_SHELTER = 9862;

    @Bind(R.id.recyclerview)
    RecyclerView mRecyclerView;

    ShelterAdapter mShelterAdapter;
    User mCurrentUser;
    boolean hasAnimalShelter = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enableFab(true, this);
        setTitle(getString(R.string.shelters));
        mNavigationView.setCheckedItem(R.id.shelter);
        mShelterAdapter = new ShelterAdapter(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mShelterAdapter);
        mCurrentUser = (User) ParseUser.getCurrentUser();
        queryData();
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == ADD_SHELTER) {
                queryData();
            }
        }
    }

    public void queryData() {
        AnimalShelter.getQuery()
                .include("owner")
                .findInBackground(this);
    }

    @Override
    public void done(List<AnimalShelter> objects, ParseException e) {
        for(int i = 0; i < objects.size(); i++) {
            AnimalShelter as = objects.get(i);
            as = as.fromParseObject(as);

            if (as.getOwner().getObjectId().equals(mCurrentUser.getObjectId())) {
                hasAnimalShelter = true;
            }

            objects.set(i, as);
        }
        mShelterAdapter.addAll(objects);
    }

    @Override
    public int getLayoutResource() {
        return R.layout.activity_long_toolbar_rv;
    }
}
