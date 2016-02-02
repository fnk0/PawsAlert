package com.gabilheri.pawsalert.data.models;

import com.gabilheri.pawsalert.base.BaseParseClass;
import com.parse.ParseQuery;

/**
 * Created by <a href="mailto:marcus@gabilheri.com">Marcus Gabilheri</a>
 *
 * @author Marcus Gabilheri
 * @version 1.0
 * @since 1/20/16.
 */
public class Favorite extends BaseParseClass<Favorite> {

    Animal animal;
    User user;

    public Animal getAnimal() {
        return animal;
    }

    public void setAnimal(Animal animal) {
        this.animal = animal;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public Favorite instance() {
        return this;
    }

    public static ParseQuery getQuery() {
        return ParseQuery.getQuery("Favorite");
    }
}
