package com.gabilheri.pawsalert.ui.success;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;

import com.gabilheri.pawsalert.R;
import com.gabilheri.pawsalert.base.BaseActivity;
import com.gabilheri.pawsalert.data.models.SuccessStory;
import com.gabilheri.pawsalert.helpers.Const;
import com.parse.GetCallback;
import com.parse.ParseException;

import java.util.Locale;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by <a href="mailto:marcus@gabilheri.com">Marcus Gabilheri</a>
 *
 * @author Marcus Gabilheri
 * @version 1.0
 * @since 3/13/16.
 */
public class ActivitySuccessDetail extends BaseActivity implements GetCallback<SuccessStory> {

    @Bind(R.id.collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbarLayout;

    @Bind(R.id.storyImage)
    AppCompatImageView mStoryImage;

    @Bind(R.id.storyPet)
    AppCompatTextView mStoryPet;

    @Bind(R.id.storyWritter)
    AppCompatTextView mStoryWritter;

    @Bind(R.id.storyLikes)
    AppCompatTextView mStoryNumLikes;

    @Bind(R.id.story)
    AppCompatTextView mStory;

    @Bind(R.id.fabLike)
    FloatingActionButton mFabLike;

    SuccessStory mSuccessStory;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enableBackNav();

        String objectId = getIntent().getExtras().getString(Const.OBJECT_ID);
        String url = getIntent().getExtras().getString(Const.IMAGE_EXTRA);

        loadImage(url, mStoryImage);

        SuccessStory.getQuery()
                .whereEqualTo(Const.OBJECT_ID, objectId)
                .include("user")
                .getFirstInBackground(this);
    }

    @Override
    public void done(SuccessStory object, ParseException e) {
        mSuccessStory = object.fromParseObject(object);
        String name = mSuccessStory.getUser().getFirstName() + " " + mSuccessStory.getUser().getLastName();

        mCollapsingToolbarLayout.setTitle(mSuccessStory.getTitle());
        setTitle(mSuccessStory.getTitle());

        mStoryWritter.setText(name);
        mStoryPet.setText(mSuccessStory.getPetName());
        setStoryNumLikes(mSuccessStory.getNumLikes());
        mStory.setText(mSuccessStory.getStory());
    }

    public void setStoryNumLikes(int likes) {
        if (likes == 0) {
            return;
        }
        mStoryNumLikes.setText(String.format(Locale.getDefault(), "%d likes", likes));
    }

    @OnClick(R.id.fabLike)
    public void likeStory(View v) {
        mSuccessStory.setNumLikes(mSuccessStory.getNumLikes() + 1);
        setStoryNumLikes(mSuccessStory.getNumLikes());
    }

    @Override
    public int getLayoutResource() {
        return R.layout.activity_story_details;
    }
}
