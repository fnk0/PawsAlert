package com.gabilheri.pawsalert.data.queryManagers;

import android.support.annotation.NonNull;

import com.gabilheri.pawsalert.data.models.Animal;
import com.gabilheri.pawsalert.data.models.AnimalShelter;
import com.gabilheri.pawsalert.data.models.User;
import com.gabilheri.pawsalert.helpers.Const;
import com.gabilheri.pawsalert.ui.home.PetListFragment;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Created by <a href="mailto:marcus@gabilheri.com">Marcus Gabilheri</a>
 *
 * @author Marcus Gabilheri
 * @version 1.0
 * @since 3/26/16.
 */
public class AnimalListManager implements FindCallback<Animal>{

    public interface AnimalListCallback {
        void onAnimalList(List<Animal> animals);
        void onErrorFetchingAnimals(Exception ex);
    }

    protected AnimalListCallback mAnimalListCallback;

    private AnimalListManager() {}

    public AnimalListManager(@NonNull AnimalListCallback animalListCallback) {
        mAnimalListCallback = animalListCallback;
    }

    public void queryAllAnimals() {
        Animal.getQuery().findInBackground(this);
    }

    public void queryAnimals(int type, AnimalShelter shelter) {
        ParseQuery<Animal> query = Animal.getQuery()
                .orderByDescending("createdAt")
                .include("user")
                .whereEqualTo("isDisabled", false)
                .whereEqualTo("missing", type == PetListFragment.FRAGMENT_MISSING);
        if (shelter != null) {
            query.whereEqualTo("animalShelter", shelter);
        }
        query.findInBackground(this);
    }

    public void queryUserAnimals(User user) {
        Animal.getQuery()
                .whereEqualTo("user", user)
                .include("user")
                .orderByDescending("createdAt")
                .findInBackground(this);
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

    @Override
    public void done(List<Animal> objects, ParseException e) {
        if (e != null) {
            mAnimalListCallback.onErrorFetchingAnimals(e);
            Timber.e(e, "Error getting objects");
        }
        if (objects == null) {
            mAnimalListCallback.onErrorFetchingAnimals(null);
            return;
        }

        for(int i = 0; i < objects.size(); i++) {
            Animal pf = objects.get(i);
            objects.set(i, pf.fromParseObject());
        }

        if (mAnimalListCallback != null) {
            mAnimalListCallback.onAnimalList(objects);
        }

    }

}
