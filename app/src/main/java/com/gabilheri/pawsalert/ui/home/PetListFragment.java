package com.gabilheri.pawsalert.ui.home;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;

import com.gabilheri.pawsalert.R;
import com.gabilheri.pawsalert.base.BaseRecyclerListFragment;
import com.gabilheri.pawsalert.base.ItemCallback;
import com.gabilheri.pawsalert.base.OnScrolledCallback;
import com.gabilheri.pawsalert.data.models.Animal;
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
        implements ItemCallback<TransitionWrapperModel<Animal>>, FindCallback<Animal>, OnScrolledCallback {

    public static final String PET_LIST_TYPE = "com.gabilheri.paws.petlisttype";

    public static final int FRAGMENT_ADOPT = 100;
    public static final int FRAGMENT_MISSING = 101;
    public static final int FRAGMENT_FAVORITE = 102;

    @IntDef({FRAGMENT_ADOPT, FRAGMENT_MISSING, FRAGMENT_FAVORITE})
    public @interface PetListType {}

    PetAdapter mPetAdapter;
    int mPetListType;
    int mCurrentPage = 1;

    User mCurrentUser;
    protected BangAnimationView mBangAnimationView;

    public static PetListFragment newInstance(@PetListType int type) {
        Bundle args = new Bundle();
        PetListFragment fragment = new PetListFragment();
        args.putInt(PET_LIST_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mCurrentUser = (User) ParseUser.getCurrentUser();
        mPetAdapter = new PetAdapter(this);
        Bundle args = getArguments();
        mPetListType = args.getInt(PET_LIST_TYPE);
        initGridCardsList(mPetAdapter);
        queryData(mCurrentPage);
        mBangAnimationView = BangAnimationView.attach2Window(getActivity());
    }

    @SuppressWarnings("unchecked")
    public void queryData(int page) {
        if (page == 1) {
            mPetAdapter.clear();
        }
        if (mPetListType == FRAGMENT_FAVORITE) {
            Favorite.getQuery()
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

    public ParseQuery getQuery() {
        return Animal.getQuery()
                .setLimit(10)
                .include("user")
                .whereEqualTo("missing", mPetListType == FRAGMENT_MISSING);
    }

    @Override
    public void onItemCallback(TransitionWrapperModel<Animal> item) {
        // If the model is null we just want to animate the view
        switch (item.getState()) {
            case Const.ANIMAL_FAVORITE:
                if (item.getView() instanceof AppCompatImageView) {
                    AppCompatImageView view = (AppCompatImageView) item.getView();
                    mBangAnimationView.bang(view);
                    if (item.getModel().isFavorite()) {
                        view.setImageResource(R.drawable.ic_action_favorite);
                        Favorite.getQuery().getFirstInBackground(new GetCallback<Favorite>() {
                            @Override
                            public void done(Favorite object, ParseException e) {
                                object.deleteInBackground();
                            }
                        });
                    } else {
                        view.setImageResource(R.drawable.ic_action_favorite_red);
                        Favorite fav = new Favorite()
                                .setUser(mCurrentUser)
                                .setAnimal(item.getModel());
                        fav.toParseObject(fav).saveInBackground();

                    }
                    item.getModel().setFavorite(!item.getModel().isFavorite());
                }
                break;
            case Const.ANIMAL_OPEN:
                Animal animal = item.getModel();
                Intent intent = new Intent(getActivity(), ActivityDetails.class);
                intent.putExtra(Const.OBJECT_ID, animal.getObjectId());
                String picUrl = animal.getPhotos().get(0).getUrl();
                intent.putExtra(Const.PET_IMAGE, picUrl);
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(getActivity(), item.getView(), Const.PET_IMAGE);
                    startActivity(intent, options.toBundle());
                } else {
                    startActivity(intent);
                }
                break;
            case Const.ANIMAL_SHARE:
                break;
        }
    }

    @Override
    public void done(List<Animal> objects, ParseException e) {
        if (e != null) {
            Timber.e(e, "Error getting objects");
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

    @Override
    public void onScrolled(int page) {
        mCurrentPage = page;
        queryData(mCurrentPage);
    }
}
