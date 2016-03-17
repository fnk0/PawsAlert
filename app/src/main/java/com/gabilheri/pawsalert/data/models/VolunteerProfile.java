package com.gabilheri.pawsalert.data.models;

import com.gabilheri.pawsalert.base.BaseParseClass;
import com.parse.ParseClassName;
import com.parse.ParseFile;

import java.util.List;

/**
 * Created by <a href="mailto:marcus@gabilheri.com">Marcus Gabilheri</a>
 *
 * @author Marcus Gabilheri
 * @version 1.0
 * @since 3/13/16.
 */
@ParseClassName("VolunteerProfile")
public class VolunteerProfile extends BaseParseClass<VolunteerProfile> {

    User user;
    ParseFile picture;
    List<String> availableTimes;

    public VolunteerProfile() {
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ParseFile getPicture() {
        return picture;
    }

    public void setPicture(ParseFile picture) {
        this.picture = picture;
    }

    public List<String> getAvailableTimes() {
        return availableTimes;
    }

    public void setAvailableTimes(List<String> availableTimes) {
        this.availableTimes = availableTimes;
    }

    @Override
    public VolunteerProfile instance() {
        return this;
    }
}
