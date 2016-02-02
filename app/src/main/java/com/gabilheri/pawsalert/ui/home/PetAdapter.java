package com.gabilheri.pawsalert.ui.home;

import android.view.View;

import com.gabilheri.pawsalert.R;
import com.gabilheri.pawsalert.base.BaseRecyclerAdapter;
import com.gabilheri.pawsalert.base.ItemCallback;
import com.gabilheri.pawsalert.data.models.Animal;

/**
 * Created by <a href="mailto:marcus@gabilheri.com">Marcus Gabilheri</a>
 *
 * @author Marcus Gabilheri
 * @version 1.0
 * @since 1/19/16.
 */
public class PetAdapter extends BaseRecyclerAdapter<Animal, PetViewHolder> {

    ItemCallback<Animal> mAnimalItemCallback;

    public PetAdapter(ItemCallback<Animal> animalItemCallback) {
        mAnimalItemCallback = animalItemCallback;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.list_item_pet;
    }

    @Override
    public PetViewHolder inflateViewHolder(View v) {
        return new PetViewHolder(v, mAnimalItemCallback);
    }

    @Override
    public void onBindViewHolder(PetViewHolder holder, int position) {
        holder.bind(mElements.get(position));
    }
}
