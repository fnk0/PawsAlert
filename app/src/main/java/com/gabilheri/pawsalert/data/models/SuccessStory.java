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
 * @since 3/13/16.
 */
@ParseClassName("SuccessStory")
public class SuccessStory extends BaseParseClass<SuccessStory> {

    User user;
    ParseFile picture;
    String title;
    String petName;
    String story;
    boolean adoptionStory;
    int numLikes;

    public SuccessStory() {
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPetName() {
        return petName;
    }

    public void setPetName(String petName) {
        this.petName = petName;
    }

    public String getStory() {
        return story;
    }

    public void setStory(String story) {
        this.story = story;
    }

    public boolean isAdoptionStory() {
        return adoptionStory;
    }

    public void setAdoptionStory(boolean adoptionStory) {
        this.adoptionStory = adoptionStory;
    }

    public int getNumLikes() {
        return numLikes;
    }

    public void setNumLikes(int numLikes) {
        this.numLikes = numLikes;
    }

    public static ParseQuery<SuccessStory> getQuery() {
        return ParseQuery.getQuery(SuccessStory.class);
    }

    @Override
    public SuccessStory instance() {
        return this;
    }
}
