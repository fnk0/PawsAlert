package com.gabilheri.pawsalert.data.queryManagers;

import android.support.annotation.NonNull;

import com.gabilheri.pawsalert.data.models.Animal;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import timber.log.Timber;

/**
 * Created by <a href="mailto:marcus@gabilheri.com">Marcus Gabilheri</a>
 *
 * @author Marcus Gabilheri
 * @version 1.0
 * @since 3/26/16.
 */
public class AnimalManager implements GetCallback<Animal>, SaveCallback {

    public interface AnimalCallback {
        void onAnimalFetched(Animal animal);
        void onErrorFetchingAnimal(Exception ex);
    }

    public interface SaveAnimalCallback {
        void onAnimalSaved();
        void onErrorSavingAnimal(Exception e);
    }

    AnimalCallback mAnimalCallback;
    SaveAnimalCallback mSaveAnimalCallback;

    private AnimalManager() {}

    public AnimalManager(@NonNull AnimalCallback animalCallback) {
        mAnimalCallback = animalCallback;
    }

    public AnimalManager(@NonNull SaveAnimalCallback saveAnimalCallback) {
        mSaveAnimalCallback = saveAnimalCallback;
    }

    public AnimalManager(@NonNull AnimalCallback animalCallback, @NonNull SaveAnimalCallback saveAnimalCallback) {
        mAnimalCallback = animalCallback;
        mSaveAnimalCallback = saveAnimalCallback;
    }

    public void queryAnimal(@NonNull String animalId) {
        ParseQuery<Animal> query = Animal.getQuery();
        query.include("user");
        query.getInBackground(animalId, this);

    }

    @Override
    public void done(Animal object, ParseException e) {
        if (e != null) {
            Timber.e(e, "Error fetching animal: " + e.getLocalizedMessage());
            mAnimalCallback.onErrorFetchingAnimal(e);
        }
        mAnimalCallback.onAnimalFetched(object.fromParseObject());
    }

    public void saveAnimal(Animal a) {
        a.toParseObject();
        a.saveInBackground(this);
    }

    @Override
    public void done(ParseException e) {
        if (e == null) {
            mSaveAnimalCallback.onAnimalSaved();
        } else {
            Timber.e("Error saving animal: " + e.getLocalizedMessage());
            mSaveAnimalCallback.onErrorSavingAnimal(e);
        }
    }
}
