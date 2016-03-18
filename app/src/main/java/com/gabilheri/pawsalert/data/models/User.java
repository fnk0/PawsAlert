package com.gabilheri.pawsalert.data.models;

import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.Locale;

/**
 * Created by <a href="mailto:marcus@gabilheri.com">Marcus Gabilheri</a>
 *
 * @author Marcus Gabilheri
 * @version 1.0
 * @since 1/18/16.
 */
public class User extends ParseUser {

    public static final String FIRST_NAME = "firstName";
    public static final String LAST_NAME = "lastName";
    public static final String PHONE_NUMBER = "phoneNumber";

    String phoneNumber;

    public String getLastName() {
        return getString(LAST_NAME);
    }

    public void setLastName(String lastName) {
        put(LAST_NAME, lastName);
    }

    public String getFirstName() {
        return getString(FIRST_NAME);
    }

    public void setFirstName(String firstName) {
        put(FIRST_NAME, firstName);
    }

    public String getPhoneNumber() {
        return getString(PHONE_NUMBER);
    }

    public User setPhoneNumber(String phoneNumber) {
        put(PHONE_NUMBER, phoneNumber);
        return this;
    }

    public void setFullName(String fullName) {
        String[] fArray = fullName.split(" ");
        if (fArray.length != 2) {
            return;
        }
        setFirstName(fArray[0]);
        setLastName(fArray[1]);
    }

    public void setProfilePicture(ParseFile file) {
        put("profilePicture", file);
    }

    public ParseFile getProfilePicture() {
        return getParseFile("profilePicture");
    }

    public String getFullName() {
        return String.format(Locale.getDefault(), "%s %s", getFirstName(), getLastName());
    }

}
