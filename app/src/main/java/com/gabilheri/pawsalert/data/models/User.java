package com.gabilheri.pawsalert.data.models;

import com.parse.ParseUser;

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
    public static final String CONTACT_INFO = "contactInfo";

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

    public ContactInfo getContactInfo() {
        return (ContactInfo) get(CONTACT_INFO);
    }

    public void setContactInfo(ContactInfo contactInfo) {
        put(CONTACT_INFO, contactInfo);
    }
}
