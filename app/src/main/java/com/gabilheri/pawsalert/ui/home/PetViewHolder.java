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

import butterknife.Bind;

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

    @Bind(R.id.petType)
    AppCompatImageView mPetType;

    Animal mAnimal;

    ItemCallback<Animal> mAnimalItemCallback;

    public PetViewHolder(View itemView, ItemCallback<Animal> animalItemCallback) {
        super(itemView);
        mAnimalItemCallback = animalItemCallback;
        mBtnMore.setOnClickListener(this);
        mActionFavorite.setOnClickListener(this);
        mActionFavorite.setOnClickListener(this);
    }

    public void bind(Animal animal) {
        this.mAnimal = animal;
        Glide.with(itemView.getContext())
                .load(mAnimal.getPhotos().get(0))
                .fitCenter()
                .crossFade()
                .into(mPetImage);
        mPetName.setText(mAnimal.getName());
        mPetGender.setImageResource(Animal.getDrawableForGender(mAnimal.getGender()));
        mPetType.setImageResource(Animal.getDrawableForType(mAnimal.getPetType()));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.actionFavorite:
                break;
            case R.id.actionShare:
                break;
            case R.id.btnMore:
                mAnimalItemCallback.onItemCallback(mAnimal);
                break;
        }
    }
}
