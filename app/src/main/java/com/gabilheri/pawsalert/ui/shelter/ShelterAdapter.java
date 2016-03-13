package com.gabilheri.pawsalert.ui.shelter;

import android.view.View;

import com.gabilheri.pawsalert.R;
import com.gabilheri.pawsalert.base.BaseRecyclerAdapter;
import com.gabilheri.pawsalert.base.ItemCallback;
import com.gabilheri.pawsalert.data.models.AnimalShelter;
import com.gabilheri.pawsalert.data.models.TransitionWrapperModel;

/**
 * Created by <a href="mailto:marcus@gabilheri.com">Marcus Gabilheri</a>
 *
 * @author Marcus Gabilheri
 * @version 1.0
 * @since 3/12/16.
 */
public class ShelterAdapter extends BaseRecyclerAdapter<AnimalShelter, ShelterViewHolder> {

    ItemCallback<TransitionWrapperModel<AnimalShelter>> mTransitionWrapperModelItemCallback;

    public ShelterAdapter(ItemCallback<TransitionWrapperModel<AnimalShelter>> transitionWrapperModelItemCallback) {
        mTransitionWrapperModelItemCallback = transitionWrapperModelItemCallback;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.list_item_shelter;
    }

    @Override
    public ShelterViewHolder inflateViewHolder(View v) {
        return new ShelterViewHolder(v, mTransitionWrapperModelItemCallback);
    }

    @Override
    public void onBindViewHolder(ShelterViewHolder holder, int position) {
        holder.bind(mElements.get(position));
    }
}
