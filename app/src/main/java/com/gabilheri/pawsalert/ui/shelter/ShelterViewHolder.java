package com.gabilheri.pawsalert.ui.shelter;

import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.bumptech.glide.Glide;
import com.gabilheri.pawsalert.R;
import com.gabilheri.pawsalert.base.ItemCallback;
import com.gabilheri.pawsalert.data.models.AnimalShelter;
import com.gabilheri.pawsalert.data.models.TransitionWrapperModel;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by <a href="mailto:marcus@gabilheri.com">Marcus Gabilheri</a>
 *
 * @author Marcus Gabilheri
 * @version 1.0
 * @since 3/12/16.
 */
public class ShelterViewHolder extends RecyclerView.ViewHolder {

    @Bind(R.id.shelterImage)
    AppCompatImageView mShelterImageIV;

    @Bind(R.id.shelterName)
    AppCompatTextView mShelterNameTV;

    AnimalShelter mAnimalShelter;
    ItemCallback<TransitionWrapperModel<AnimalShelter>> mTransitionWrapperModelItemCallback;

    public ShelterViewHolder(View itemView, ItemCallback<TransitionWrapperModel<AnimalShelter>> transitionWrapperModelItemCallback) {
        super(itemView);
        mTransitionWrapperModelItemCallback = transitionWrapperModelItemCallback;
        ButterKnife.bind(this, itemView);
    }

    public void bind(AnimalShelter shelter) {
        mAnimalShelter = shelter;
        Glide.with(itemView.getContext())
                .load(shelter.getCoverPhoto().getUrl())
                .into(mShelterImageIV);
        mShelterNameTV.setText(shelter.getShelterName());
    }

    @OnClick(R.id.actionCall)
    public void call(View v) {
        mTransitionWrapperModelItemCallback.onItemCallback(new TransitionWrapperModel<>(mAnimalShelter, v, ActivityShelters.CALL_SHELTER));
    }

    @OnClick(R.id.actionShare)
    public void share(View v) {
        mTransitionWrapperModelItemCallback.onItemCallback(new TransitionWrapperModel<>(mAnimalShelter, v, ActivityShelters.SHARE_SHELTER));
    }

    @OnClick(R.id.btnMore)
    public void seeMore(View v) {
        mTransitionWrapperModelItemCallback.onItemCallback(new TransitionWrapperModel<>(mAnimalShelter, mShelterImageIV, ActivityShelters.VIEW_SHELTER));

    }
}
