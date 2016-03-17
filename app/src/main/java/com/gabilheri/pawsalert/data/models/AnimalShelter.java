package com.gabilheri.pawsalert.data.models;

import com.gabilheri.pawsalert.base.BaseParseClass;
import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseQuery;

/**
 * Created by <a href="mailto:marcus@gabilheri.com">Marcus Gabilheri</a>
 *
 * @author Marcus Gabilheri
 * @version 1.0
 * @since 3/9/16.
 */
@ParseClassName("AnimalShelter")
public class AnimalShelter extends BaseParseClass<AnimalShelter> {

    User owner;
    String shelterName;
    double latitude;
    double longitude;
    String address;
    String website;
    String shelterDescription;
    String email;
    String phoneNumber;
    String emergencyNumber;
    String openTime;
    String closeTime;
    ParseFile mCoverPhoto;

    public AnimalShelter() {
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public String getShelterName() {
        return shelterName;
    }

    public void setShelterName(String shelterName) {
        this.shelterName = shelterName;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getShelterDescription() {
        return shelterDescription;
    }

    public void setShelterDescription(String shelterDescription) {
        this.shelterDescription = shelterDescription;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmergencyNumber() {
        return emergencyNumber;
    }

    public void setEmergencyNumber(String emergencyNumber) {
        this.emergencyNumber = emergencyNumber;
    }

    public String getOpenTime() {
        return openTime;
    }

    public void setOpenTime(String openTime) {
        this.openTime = openTime;
    }

    public String getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(String closeTime) {
        this.closeTime = closeTime;
    }

    public static ParseQuery<AnimalShelter> getQuery() {
        return ParseQuery.getQuery(AnimalShelter.class);
    }

    public ParseFile getCoverPhoto() {
        return mCoverPhoto;
    }

    public void setCoverPhoto(ParseFile coverPhoto) {
        mCoverPhoto = coverPhoto;
    }

    public String isValidShelter() {



        return null;
    }

    @Override
    public AnimalShelter instance() {
        return this;
    }
}
