package com.gabilheri.pawsalert.ui.home;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;

import com.gabilheri.pawsalert.base.BaseActivity;
import com.gabilheri.pawsalert.base.BaseRecyclerListFragment;
import com.gabilheri.pawsalert.base.ItemCallback;
import com.gabilheri.pawsalert.data.models.Animal;
import com.gabilheri.pawsalert.data.models.AnimalShelter;
import com.gabilheri.pawsalert.data.models.TransitionWrapperModel;
import com.gabilheri.pawsalert.data.models.User;
import com.gabilheri.pawsalert.data.queryManagers.AnimalListManager;
import com.gabilheri.pawsalert.data.queryManagers.AnimalShelterManager;
import com.gabilheri.pawsalert.helpers.Const;
import com.gabilheri.pawsalert.ui.animations.BangAnimationView;
import com.gabilheri.pawsalert.ui.details.ActivityDetails;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by <a href="mailto:marcus@gabilheri.com">Marcus Gabilheri</a>
 *
 * @author Marcus Gabilheri
 * @version 1.0
 * @since 1/18/16.
 */
  public class PetListFragment extends BaseRecyclerListFragment
        implements ItemCallback<TransitionWrapperModel<Animal>>,
        AnimalListManager.AnimalListCallback, AnimalShelterManager.AnimalShelterCallback {

    public static final String PET_LIST_TYPE = "com.gabilheri.paws.petlisttype";
    public static final String SHELTER_ID = "com.gabilheri.paws.shelterID";
    public static final String USER_ID = "com.gabilheri.paws.userID";

    public static final int FRAGMENT_FOUND = 100;
    public static final int FRAGMENT_MISSING = 101;

    @IntDef({FRAGMENT_FOUND, FRAGMENT_MISSING})
    public @interface PetListType {}

    PetAdapter mPetAdapter;
    int mPetListType;
    String mShelterId;
    String mUserId;
    List<String> mPetIds;

    protected BangAnimationView mBangAnimationView;
    AnimalListManager mAnimalManager;
    AnimalShelterManager mAnimalShelterManager;

    public static PetListFragment newInstance(@PetListType int type, String shelterID) {
        Bundle args = new Bundle();
        PetListFragment fragment = new PetListFragment();
        args.putInt(PET_LIST_TYPE, type);
        args.putString(SHELTER_ID, shelterID);
        fragment.setArguments(args);
        return fragment;
    }

    public static PetListFragment newInstance(String reference, String id) {
        Bundle args = new Bundle();
        PetListFragment fragment = new PetListFragment();
        args.putString(reference, id);
        fragment.setArguments(args);
        return fragment;
    }

    public static PetListFragment newInstance(ArrayList<String> petIds) {
        Bundle args = new Bundle();
        args.putStringArrayList(Const.OBJECT_ID, petIds);
        PetListFragment fragment = new PetListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAnimalManager = new AnimalListManager(this);
        mAnimalShelterManager = new AnimalShelterManager(this);

        mPetAdapter = new PetAdapter(this);
        initGridCardsList(mPetAdapter);
        mBangAnimationView = BangAnimationView.attach2Window(getActivity());

        Bundle args = getArguments();
        mPetIds = args.getStringArrayList(Const.OBJECT_ID);

        mPetListType = args.getInt(PET_LIST_TYPE);
        mShelterId = args.getString(SHELTER_ID);
        mUserId = args.getString(USER_ID);

        queryData();
    }

    public void queryData() {
        if (mPetIds == null) {
            if (mShelterId != null) {
                mAnimalShelterManager.getAnimalShelter(mShelterId);
            } else if (mUserId != null) {
                mAnimalManager.queryUserAnimals((User) ParseUser.getCurrentUser());
            } else {
                mAnimalManager.queryAnimals(mPetListType, null);
            }
        } else {
            mAnimalManager.queryIdList(mPetIds);
        }
    }

    @Override
    public void onItemCallback(TransitionWrapperModel<Animal> item) {
        // If the model is null we just want to animate the view
        switch (item.getState()) {
            case Const.ANIMAL_CALL:
                if (item.getView() instanceof AppCompatImageView) {
                    mBangAnimationView.bang(item.getView());
                    ((BaseActivity) getActivity()).makePhoneCall(item.getModel().getUser().getPhoneNumber());
                }
                break;
            case Const.ANIMAL_OPEN:
                Animal animal = item.getModel();
                Intent intent = new Intent(getActivity(), ActivityDetails.class);
                intent.putExtra(Const.OBJECT_ID, animal.getObjectId());
                String picUrl = animal.getPhotos().get(0).getUrl();
                intent.putExtra(Const.IMAGE_EXTRA, picUrl);
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(getActivity(), item.getView(), Const.IMAGE_EXTRA);
                    startActivity(intent, options.toBundle());
                } else {
                    startActivity(intent);
                }
                break;
            case Const.ANIMAL_SHARE:
                mBangAnimationView.bang(item.getView());
                ((BaseActivity) getActivity()).shareURL("http://www.stillwaterpaws.com/pet.html?id=" + item.getModel().getObjectId());
                break;
        }
    }

    @Override
    public void onAnimalList(List<Animal> animals) {
        mPetAdapter.addAll(animals);
    }

    @Override
    public void onErrorFetchingAnimals(Exception ex) {
        showSnackbar("Error fetching animals. Please try again later.");
    }

    @Override
    public void onAnimalShelter(AnimalShelter animalShelter) {
        mAnimalManager.queryAnimals(mPetListType, animalShelter);
    }

    @Override
    public void onErrorFetchingAnimalShelter(Exception ex) {
        showSnackbar("Error fetching animals for shelter. Please try again later.");
    }
}
