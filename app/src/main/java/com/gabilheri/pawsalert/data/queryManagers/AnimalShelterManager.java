package com.gabilheri.pawsalert.data.queryManagers;

import android.support.annotation.NonNull;

import com.gabilheri.pawsalert.data.models.AnimalShelter;
import com.gabilheri.pawsalert.data.models.User;
import com.parse.GetCallback;
import com.parse.ParseException;

/**
 * Created by <a href="mailto:marcus@gabilheri.com">Marcus Gabilheri</a>
 *
 * @author Marcus Gabilheri
 * @version 1.0
 * @since 3/26/16.
 */
public class AnimalShelterManager implements GetCallback<AnimalShelter> {

    public interface AnimalShelterCallback {
        void onAnimalShelter(AnimalShelter animalShelter);
        void onErrorFetchingAnimalShelter(Exception ex);
    }

    AnimalShelterCallback mAnimalShelterCallback;

    private AnimalShelterManager() {}

    public AnimalShelterManager(@NonNull AnimalShelterCallback animalShelterCallback) {
        mAnimalShelterCallback = animalShelterCallback;
    }

    public void getAnimalShelter(String shelterId) {
        AnimalShelter.getQuery()
                .whereEqualTo("objectId", shelterId)
                .include("owner")
                .orderByDescending("createdAt")
                .getFirstInBackground(this);
    }

    public void getAnimalShelter(User owner) {
        AnimalShelter.getQuery()
                .whereEqualTo("owner", owner)
                .getFirstInBackground(this);
    }

    @Override
    public void done(AnimalShelter object, ParseException e) {
        if (mAnimalShelterCallback != null) {
            if (e == null) {
                mAnimalShelterCallback.onErrorFetchingAnimalShelter(e);
            }
            if (object != null) {
                mAnimalShelterCallback.onAnimalShelter(object.fromParseObject());
            }
        }
    }
}
