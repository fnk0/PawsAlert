package com.gabilheri.pawsalert.ui.home;

import android.os.Bundle;
import android.support.annotation.IntDef;
import android.view.View;

import com.gabilheri.pawsalert.base.BaseRecyclerListFragment;
import com.gabilheri.pawsalert.base.ItemCallback;
import com.gabilheri.pawsalert.base.OnScrolledCallback;
import com.gabilheri.pawsalert.data.RxCallback;
import com.gabilheri.pawsalert.data.RxSubscriber;
import com.gabilheri.pawsalert.data.models.Animal;
import com.gabilheri.pawsalert.data.models.Favorite;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.parse.ParseObservable;
import rx.schedulers.Schedulers;

/**
 * Created by <a href="mailto:marcus@gabilheri.com">Marcus Gabilheri</a>
 *
 * @author Marcus Gabilheri
 * @version 1.0
 * @since 1/18/16.
 */
public class PetListFragment extends BaseRecyclerListFragment
        implements ItemCallback<Animal>, RxCallback<List<ParseObject>>, OnScrolledCallback {

    public static final String PET_LIST_TYPE = "com.gabilheri.paws.petlisttype";

    public static final int FRAGMENT_ADOPT = 100;
    public static final int FRAGMENT_MISSING = 101;
    public static final int FRAGMENT_FAVORITE = 102;

    @IntDef({FRAGMENT_ADOPT, FRAGMENT_MISSING, FRAGMENT_FAVORITE})
    public @interface PetListType {}

    PetAdapter mPetAdapter;
    int mPetListType;
    int mCurrentPage = 1;

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
        mPetAdapter = new PetAdapter(this);

        Bundle args = getArguments();

        mPetListType = args.getInt(PET_LIST_TYPE);

        initGridCardsList(mPetAdapter);

        queryData(mCurrentPage);
    }

    @SuppressWarnings("unchecked")
    public void queryData(int page) {
        if (page == 1) {
            mPetAdapter.clear();
        }

        Observable<List<ParseObject>> animals = ParseObservable.find(getQuery())
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread());

        animals.subscribe(new RxSubscriber<>(this));
    }

    public ParseQuery getQuery() {
        if (mPetListType == FRAGMENT_FAVORITE) {
            return Favorite.getQuery().setLimit(10);
        }
        return Animal.getQuery()
                .setLimit(10)
                .whereEqualTo("missing", mPetListType == FRAGMENT_MISSING);
    }

    @Override
    public void onItemCallback(Animal item) {
        //TODO open details activity with the selected animal
    }

    @Override
    public void onDataReady(List<ParseObject> data) {
        for(ParseObject po : data) {
            mPetAdapter.add(new Animal().fromParseObject(po));
        }
    }

    @Override
    public void onDataError(Throwable e) {

    }

    @Override
    public void onScrolled(int page) {
        mCurrentPage = page;
        queryData(mCurrentPage);
    }
}
