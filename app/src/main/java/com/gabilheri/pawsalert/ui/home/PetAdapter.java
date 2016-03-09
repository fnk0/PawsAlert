package com.gabilheri.pawsalert.ui.home;

import android.view.View;

import com.gabilheri.pawsalert.R;
import com.gabilheri.pawsalert.base.BaseRecyclerAdapter;
import com.gabilheri.pawsalert.base.ItemCallback;
import com.gabilheri.pawsalert.data.models.Animal;
import com.gabilheri.pawsalert.data.models.Favorite;
import com.gabilheri.pawsalert.data.models.TransitionWrapperModel;

/**
 * Created by <a href="mailto:marcus@gabilheri.com">Marcus Gabilheri</a>
 *
 * @author Marcus Gabilheri
 * @version 1.0
 * @since 1/19/16.
 */
public class PetAdapter extends BaseRecyclerAdapter<Animal, PetViewHolder> {

    ItemCallback<TransitionWrapperModel<Animal>> mAnimalItemCallback;

    public PetAdapter(ItemCallback<TransitionWrapperModel<Animal>> animalItemCallback) {
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

    public void checkFavorite(Favorite favorite) {
        int index = mElements.indexOf(favorite.getAnimal());
        if (index != -1) {
            Animal a = mElements.get(index);
            a.setFavorite(true);
            notifyItemChanged(index);
        }
    }
}
