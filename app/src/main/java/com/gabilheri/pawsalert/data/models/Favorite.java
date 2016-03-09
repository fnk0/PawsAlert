package com.gabilheri.pawsalert.data.models;

import com.gabilheri.pawsalert.base.BaseParseClass;
import com.parse.ParseClassName;
import com.parse.ParseQuery;

/**
 * Created by <a href="mailto:marcus@gabilheri.com">Marcus Gabilheri</a>
 *
 * @author Marcus Gabilheri
 * @version 1.0
 * @since 1/20/16.
 */
@ParseClassName("Favorite")
public class Favorite extends BaseParseClass<Favorite> {

    Animal animal;
    User user;

    public Favorite() {
    }

    public Animal getAnimal() {
        return animal;
    }

    public Favorite setAnimal(Animal animal) {
        this.animal = animal;
        return this;
    }

    public User getUser() {
        return user;
    }

    public Favorite setUser(User user) {
        this.user = user;
        return this;
    }

    @Override
    public Favorite instance() {
        return this;
    }

    public static ParseQuery<Favorite> getQuery() {
        return ParseQuery.getQuery(Favorite.class);
    }
}
