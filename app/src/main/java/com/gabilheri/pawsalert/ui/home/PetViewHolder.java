package com.gabilheri.pawsalert.ui.home;

import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.bumptech.glide.Glide;
import com.gabilheri.pawsalert.R;
import com.gabilheri.pawsalert.base.ItemCallback;
import com.gabilheri.pawsalert.data.models.Animal;
import com.gabilheri.pawsalert.data.models.TransitionWrapperModel;
import com.gabilheri.pawsalert.helpers.Const;
import com.gabilheri.pawsalert.helpers.DateFormatter;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by <a href="mailto:marcus@gabilheri.com">Marcus Gabilheri</a>
 *
 * @author Marcus Gabilheri
 * @version 1.0
 * @since 1/19/16.
 */
public class PetViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    @Bind(R.id.actionFavorite)
    AppCompatImageView mActionFavorite;

    @Bind(R.id.actionShare)
    AppCompatImageView mActionShare;

    @Bind(R.id.btnMore)
    AppCompatButton mBtnMore;

    @Bind(R.id.petImage)
    AppCompatImageView mPetImage;

    @Bind(R.id.petGender)
    AppCompatImageView mPetGender;

    @Bind(R.id.petName)
    AppCompatTextView mPetName;

    @Bind(R.id.petDate)
    AppCompatTextView mCreatedAt;

    Animal mAnimal;

    ItemCallback<TransitionWrapperModel<Animal>> mAnimalItemCallback;

    public PetViewHolder(View itemView, ItemCallback<TransitionWrapperModel<Animal>> animalItemCallback) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mAnimalItemCallback = animalItemCallback;
        mBtnMore.setOnClickListener(this);
        mActionFavorite.setOnClickListener(this);
        mActionShare.setOnClickListener(this);
    }

    public void bind(Animal animal) {
        this.mAnimal = animal;
        Glide.with(itemView.getContext())
                .load(mAnimal.getPhotos().get(0).getUrl())
                .fitCenter()
                .crossFade()
                .into(mPetImage);
        mPetName.setText(mAnimal.getName());
        mPetGender.setImageResource(Animal.getDrawableForGender(mAnimal.getGender()));
        mActionFavorite.setImageResource(mAnimal.isFavorite() ? R.drawable.ic_action_favorite_red : R.drawable.ic_action_favorite);
        mCreatedAt.setText(DateFormatter.prettyFormat(animal.getCreatedAt()));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.actionFavorite:
                mAnimalItemCallback.onItemCallback(new TransitionWrapperModel<>(mAnimal, mActionFavorite, Const.ANIMAL_FAVORITE));
                break;
            case R.id.actionShare:
                mAnimalItemCallback.onItemCallback(new TransitionWrapperModel<>(mAnimal, mActionShare, Const.ANIMAL_SHARE));
                break;
            case R.id.btnMore:
                mAnimalItemCallback.onItemCallback(new TransitionWrapperModel<>(mAnimal, mPetImage, Const.ANIMAL_OPEN));
                break;
        }
    }
}
