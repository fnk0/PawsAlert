package com.gabilheri.pawsalert.data.queryManagers;

import android.support.annotation.NonNull;

import com.gabilheri.pawsalert.data.models.AnimalShelter;
import com.parse.FindCallback;
import com.parse.ParseException;

import java.util.List;

import timber.log.Timber;

/**
 * Created by <a href="mailto:marcus@gabilheri.com">Marcus Gabilheri</a>
 *
 * @author Marcus Gabilheri
 * @version 1.0
 * @since 3/26/16.
 */
public class AnimalShelterListManager implements FindCallback<AnimalShelter> {

    public interface AnimalShelterListCallback {
        void onAnimalShelterList(List<AnimalShelter> animalShelters);
        void onErrorFetchingAnimalShelters(Exception ex);
    }

    AnimalShelterListCallback mAnimalShelterListCallback;

    private AnimalShelterListManager() {}

    public AnimalShelterListManager(@NonNull AnimalShelterListCallback animalShelterListCallback) {
        mAnimalShelterListCallback = animalShelterListCallback;
    }

    public void getAnimalShelters() {
        AnimalShelter.getQuery()
                .include("owner")
                .findInBackground(this);
    }

    @Override
    public void done(List<AnimalShelter> objects, ParseException e) {
        if (mAnimalShelterListCallback != null) {

            if (e != null) {
                Timber.e(e, "Error fetching shelters: " + e.getLocalizedMessage());
                mAnimalShelterListCallback.onErrorFetchingAnimalShelters(e);
            }

            for(int i = 0; i < objects.size(); i++) {
                AnimalShelter as = objects.get(i);
                objects.set(i, as.fromParseObject());
            }
            mAnimalShelterListCallback.onAnimalShelterList(objects);
        }
    }
}
