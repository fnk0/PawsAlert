package com.gabilheri.pawsalert.data.models;

import android.support.annotation.DrawableRes;

import com.gabilheri.pawsalert.R;
import com.gabilheri.pawsalert.base.BaseParseClass;
import com.parse.ParseFile;
import com.parse.ParseQuery;

import java.security.InvalidParameterException;
import java.util.List;

/**
 * Created by <a href="mailto:marcus@gabilheri.com">Marcus Gabilheri</a>
 *
 * @author Marcus Gabilheri
 * @version 1.0
 * @since 1/18/16.
 */
public class Animal extends BaseParseClass<Animal> {

    public static final String MALE = "Male";
    public static final String FEMALE = "Female";
    public static final String DOG = "Dog";
    public static final String CAT = "Cat";

    String name;
    String petType;
    String gender;
    int age;
    List<String> markings;
    boolean isPuppy;
    boolean microchip;
    boolean vaccinations;
    boolean missing;
    boolean isNeutered;
    double latitude;
    double longitude;
    String otherInfo;
    String size;
    List<ParseFile> photos;
    User user;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPetType() {
        return petType;
    }

    public void setPetType(String petType) {
        this.petType = petType;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public List<String> getMarkings() {
        return markings;
    }

    public void setMarkings(List<String> markings) {
        this.markings = markings;
    }

    public boolean hasMicrochip() {
        return microchip;
    }

    public void setMicrochip(boolean microchip) {
        this.microchip = microchip;
    }

    public boolean isMissing() {
        return missing;
    }

    public void setMissing(boolean missing) {
        this.missing = missing;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getOtherInfo() {
        return otherInfo;
    }

    public void setOtherInfo(String otherInfo) {
        this.otherInfo = otherInfo;
    }

    public boolean hasVaccinations() {
        return vaccinations;
    }

    public void setVaccinations(boolean vaccinations) {
        this.vaccinations = vaccinations;
    }

    public boolean isNeutered() {
        return isNeutered;
    }

    public void setNeutered(boolean neutered) {
        isNeutered = neutered;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public boolean isPuppy() {
        return isPuppy;
    }

    public void setPuppy(boolean puppy) {
        isPuppy = puppy;
    }

    public List<ParseFile> getPhotos() {
        return photos;
    }

    public void setPhotos(List<ParseFile> photos) {
        this.photos = photos;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public static @DrawableRes int getDrawableForType(String type) {
        switch (type) {
            case CAT:
                return R.drawable.ic_cat;
            case DOG:
                return R.drawable.ic_dog;
            default:
                throw new InvalidParameterException("Could not find animal of type: " + type);
        }
    }

    public static @DrawableRes int getDrawableForGender(String gender) {
        switch (gender) {
            case MALE:
                return R.drawable.ic_male;
            case FEMALE:
                return R.drawable.ic_female;
            default:
                throw new InvalidParameterException("Could not find gender: " + gender);
        }
    }

    public static ParseQuery getQuery() {
        return ParseQuery.getQuery("Animal");
    }

    @Override
    public Animal instance() {
        return this;
    }
}
