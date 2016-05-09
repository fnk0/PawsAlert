package com.gabilheri.pawsalert.ui.success;

import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.bumptech.glide.Glide;
import com.gabilheri.pawsalert.R;
import com.gabilheri.pawsalert.base.ItemCallback;
import com.gabilheri.pawsalert.data.models.SuccessStory;
import com.gabilheri.pawsalert.data.models.TransitionWrapperModel;

import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by <a href="mailto:marcus@gabilheri.com">Marcus Gabilheri</a>
 *
 * @author Marcus Gabilheri
 * @version 1.0
 * @since 3/13/16.
 */
public class StoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    @Bind(R.id.storyTitle)
    AppCompatTextView mStoryTitle;

    @Bind(R.id.storyImage)
    AppCompatImageView mStoryImage;

    @Bind(R.id.actionShare)
    AppCompatImageView mActionShareIV;

    @Bind(R.id.actionLike)
    AppCompatImageView mActionLikeIV;

    @Bind(R.id.btnMore)
    AppCompatButton mBtnMore;

    @Bind(R.id.numLikes)
    AppCompatTextView mNumLikesTV;

    SuccessStory mSuccessStory;
    ItemCallback<TransitionWrapperModel<SuccessStory>> mItemCallback;

    public StoryViewHolder(View itemView, ItemCallback<TransitionWrapperModel<SuccessStory>> itemCallback) {
        super(itemView);
        mItemCallback = itemCallback;
        ButterKnife.bind(this, itemView);
        mActionLikeIV.setOnClickListener(this);
        mActionShareIV.setOnClickListener(this);
        mBtnMore.setOnClickListener(this);
    }

    public void bind(SuccessStory successStory) {
        mSuccessStory = successStory;
        Glide.with(itemView.getContext())
                .load(successStory.getPicture().getUrl())
                .into(mStoryImage);

        mStoryTitle.setText(successStory.getTitle());
        if (successStory.getNumLikes() == 0) {
            mNumLikesTV.setText("");
        } else {
            mNumLikesTV.setText(String.format(Locale.getDefault(), "%d", successStory.getNumLikes()));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.actionShare:
                mItemCallback.onItemCallback(new TransitionWrapperModel<>(mSuccessStory, v, ActivitySuccessStory.SHARE_STORY));
                break;
            case R.id.actionLike:
                mItemCallback.onItemCallback(new TransitionWrapperModel<>(mSuccessStory, v, ActivitySuccessStory.LIKE_STORY));
                break;
            case R.id.btnMore:
                mItemCallback.onItemCallback(new TransitionWrapperModel<>(mSuccessStory, mStoryImage, ActivitySuccessStory.OPEN_STORY));
                break;
        }
    }
}
