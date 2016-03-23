package com.gabilheri.pawsalert.ui.home;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;

import com.gabilheri.pawsalert.base.BaseActivity;
import com.gabilheri.pawsalert.base.BaseRecyclerListFragment;
import com.gabilheri.pawsalert.base.ItemCallback;
import com.gabilheri.pawsalert.data.models.Animal;
import com.gabilheri.pawsalert.data.models.AnimalShelter;
import com.gabilheri.pawsalert.data.models.Favorite;
import com.gabilheri.pawsalert.data.models.TransitionWrapperModel;
import com.gabilheri.pawsalert.data.models.User;
import com.gabilheri.pawsalert.helpers.Const;
import com.gabilheri.pawsalert.ui.animations.BangAnimationView;
import com.gabilheri.pawsalert.ui.details.ActivityDetails;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Created by <a href="mailto:marcus@gabilheri.com">Marcus Gabilheri</a>
 *
 * @author Marcus Gabilheri
 * @version 1.0
 * @since 1/18/16.
 */
  public class PetListFragment extends BaseRecyclerListFragment
        implements ItemCallback<TransitionWrapperModel<Animal>>, FindCallback<Animal> {

    public static final String PET_LIST_TYPE = "com.gabilheri.paws.petlisttype";
    public static final String SHELTER_ID = "com.gabilheri.paws.shelterID";
    public static final String USER_ID = "com.gabilheri.paws.userID";

    public static final int FRAGMENT_ADOPT = 100;
    public static final int FRAGMENT_MISSING = 101;
    public static final int FRAGMENT_FAVORITE = 102;

    @IntDef({FRAGMENT_ADOPT, FRAGMENT_MISSING, FRAGMENT_FAVORITE})
    public @interface PetListType {}

    PetAdapter mPetAdapter;
    int mPetListType;
    String mShelterId;
    String mUserId;
    User mCurrentUser;
    AnimalShelter mAnimalShelter;
    protected BangAnimationView mBangAnimationView;


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
        try {
            mCurrentUser = (User) ParseUser.getCurrentUser();
        } catch (Exception ex) {

        }

        mPetAdapter = new PetAdapter(this);
        initGridCardsList(mPetAdapter);
        mBangAnimationView = BangAnimationView.attach2Window(getActivity());

        Bundle args = getArguments();
        List<String> petIds = args.getStringArrayList(Const.OBJECT_ID);

        if (petIds == null) {
            mPetListType = args.getInt(PET_LIST_TYPE);
            mShelterId = args.getString(SHELTER_ID);
            mUserId = args.getString(USER_ID);

            if (mShelterId != null) {
                AnimalShelter.getQuery()
                        .whereEqualTo("objectId", mShelterId)
                        .include("owner")
                        .orderByDescending("createdAt")
                        .getFirstInBackground(new GetCallback<AnimalShelter>() {
                            @Override
                            public void done(AnimalShelter object, ParseException e) {
                                queryData();
                            }
                        });
            } else if (mUserId != null) {
                queryUserPets();
            } else {
                queryData();
            }
        } else {
            queryIdList(petIds);
        }
    }

    @SuppressWarnings("unchecked")
    public void queryData() {
        mPetAdapter.clear();
        if (mPetListType == FRAGMENT_FAVORITE) {
            Favorite.getQuery()
                    .orderByDescending("createdAt")
                    .include("animal")
                    .include("user")
                    .findInBackground(new FindCallback<Favorite>() {
                @Override
                public void done(List<Favorite> objects, ParseException e) {
                    for(Favorite o : objects) {
                        o = o.fromParseObject(o);
                        Animal an = o.getAnimal();
                        an = an.fromParseObject(an);
                        an.setFavorite(true);
                        mPetAdapter.add(an);
                    }
                }
            });
        } else {
            getQuery().findInBackground(this);
        }
    }

    public void queryIdList(@NonNull List<String> petIds) {

        List<ParseQuery<Animal>> mQueryList = new ArrayList<>();

        for (String s : petIds) {
            mQueryList.add(Animal.getQuery().whereEqualTo(Const.OBJECT_ID, s));
        }

        ParseQuery<Animal> mainQuery = ParseQuery.or(mQueryList);
        mainQuery.include("user");
        mainQuery.findInBackground(this);
    }

    public ParseQuery<Animal> getQuery() {
        ParseQuery<Animal> query = Animal.getQuery()
                .orderByDescending("createdAt")
                .include("user")
                .whereEqualTo("isDisabled", false)
                .whereEqualTo("missing", mPetListType == FRAGMENT_MISSING);
        if (mAnimalShelter != null) {
            query.whereEqualTo("animalShelter", mAnimalShelter);
            query.whereEqualTo("isDisabled", false);
        }
        return query;
    }

    public void queryUserPets() {
        Animal.getQuery()
                .whereEqualTo("user", ParseUser.getCurrentUser())
                .include("user")
                .orderByDescending("createdAt")
                .findInBackground(this);
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

    public void refreshFavorites() {
        if (getActivity() instanceof HomeActivity) {
            HomeActivity activity = (HomeActivity) getActivity();
//            activity.refreshFavorites();
        }
    }

    @Override
    public void done(List<Animal> objects, ParseException e) {
        if (e != null) {
            Timber.e(e, "Error getting objects");
        }
        if (objects == null) {
            return;
        }
        for(Animal pf : objects) {
            pf = pf.fromParseObject(pf);
            mPetAdapter.add(pf);
        }

        if (mPetListType != FRAGMENT_FAVORITE) {
            if (mCurrentUser != null) {
                Favorite.getQuery().whereEqualTo("user", mCurrentUser)
                        .findInBackground(new FindCallback<Favorite>() {
                            @Override
                            public void done(List<Favorite> objects, ParseException e) {
                                for (Favorite f : objects) {
                                    f = f.fromParseObject(f);
                                    mPetAdapter.checkFavorite(f);
                                }
                            }
                        });
            }
        }
    }
}
